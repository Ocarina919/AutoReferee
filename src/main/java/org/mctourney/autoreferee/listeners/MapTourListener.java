package org.mctourney.autoreferee.listeners;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.PressurePlate;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Maps;

import org.mctourney.autoreferee.AutoRefMatch;
import org.mctourney.autoreferee.AutoRefPlayer;
import org.mctourney.autoreferee.AutoRefSpectator;
import org.mctourney.autoreferee.AutoRefTeam;
import org.mctourney.autoreferee.AutoReferee;
import org.mctourney.autoreferee.commands.PracticeCommands;
import org.mctourney.autoreferee.goals.AutoRefGoal;
import org.mctourney.autoreferee.goals.BlockGoal;
import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.autoreferee.util.LocationUtil;
import org.mctourney.autoreferee.util.PlayerUtil;
import org.mctourney.autoreferee.util.TeleportationUtil;

public class MapTourListener implements Listener
{

	public MapTourListener(Plugin plugin)
	{
		this.plugin = (AutoReferee) plugin;
	}

	AutoReferee plugin = null;

	private Inventory mapTourMenu;

	public enum ToolAction //TODO what does this do?
	{
		TOOL_OPENMENU(Material.ARROW),
		TOOL_QUIT(Material.BLAZE_POWDER);

		public final Material tooltype;

		ToolAction(Material type)
		{ this.tooltype = type; }

		private static Map<Material, MapTourListener.ToolAction> _map;
		static
		{
			_map = Maps.newHashMap();
			for (MapTourListener.ToolAction tool : MapTourListener.ToolAction.values())
				_map.put(tool.tooltype, tool);
		}

		public static MapTourListener.ToolAction fromMaterial(Material material)
		{ return _map.get(material); }
	}


	class Sortbydistance implements Comparator<AutoRefGoal> {
		AutoRefMatch match;
		public Sortbydistance(AutoRefMatch m){
			this.match = m;
		}
		public int compare(AutoRefGoal goal_one, AutoRefGoal goal_two){
			return (int) (getDistanceFromSpawn(goal_one) - getDistanceFromSpawn(goal_two));
		}
		private double getDistanceFromSpawn(AutoRefGoal goal){
			AutoReferee.log(String.valueOf(this.match.getWorldSpawn().distance(goal.getTarget())));
			return this.match.getWorldSpawn().distance(goal.getTarget());
		}
	}

	private void setupMapTourMenu(AutoRefMatch match, Player pl)
	{
		mapTourMenu = Bukkit.createInventory(null, 9 * 2,
				ChatColor.BOLD + match.getMap().getName() + "Map Tour");

		AutoRefTeam team = match.getTeams().iterator().next();

		AutoRefGoal[] goals = new AutoRefGoal[team.getTeamGoals().size()];
		team.getTeamGoals().toArray(goals);

		Arrays.sort(goals, new Sortbydistance(match));

		int slot = 0;
		for (AutoRefGoal goal : goals){
			mapTourMenu.setItem(slot, new ItemStack(goal.getItem().getMaterial(), 1, goal.getItem().getData()));
			slot++;
		}
		AutoReferee.log("added items to inventory");
	}

	@EventHandler
	public void toolUsage(PlayerInteractEvent event)
	{
		Player pl = event.getPlayer();
		//AutoReferee.log("got event");

		AutoRefMatch match = plugin.getMatch(pl.getWorld());
		if (match == null) return;

		if (match.getCurrentState().inProgress()) return;
		if (match.getCurrentState().isAfterMatch()) return;

		Block block;

		// this event is not an "item" event
		if (!event.hasItem()) return;

		// get type id of the event and check if its one of our tools
		ToolAction action = ToolAction.fromMaterial(event.getMaterial());
		if (action == null) return;

		// get which action to perform
		switch (action)
		{
			case TOOL_OPENMENU:
				/*AutoReferee.log("found tool #1"); //TODO re-add maptour tool
				setupMapTourMenu(match, pl);
				showMapTourMenu(pl);
				 */
				break;

			case TOOL_QUIT:
				PlayerUtil.setGameMode(pl, GameMode.SURVIVAL);
				pl.getInventory().clear();
				pl.teleport(match.getWorldSpawn());
				pl.setFallDistance(0.0f);

				break;

			// this isn't one of our tools...
			default: return;
		}

		// cancel the event, since it was one of our tools being used properly
		event.setCancelled(true);
	}

	private void showMapTourMenu(Player viewer)
	{ viewer.openInventory(mapTourMenu); }

}
