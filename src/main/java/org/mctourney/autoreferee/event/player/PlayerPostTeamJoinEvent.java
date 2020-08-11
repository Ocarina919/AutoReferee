package org.mctourney.autoreferee.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.mctourney.autoreferee.AutoRefTeam;

/**
 * Called after a player joins a team,
 * and all AR logic has been processed
 *
 * @author char
 */
public class PlayerPostTeamJoinEvent extends PlayerTeamEvent {

	public PlayerPostTeamJoinEvent(Player player, AutoRefTeam team) {
		super(player, team);
	}
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers()
	{ return handlers; }

	public static HandlerList getHandlerList()
	{ return handlers; }
}
