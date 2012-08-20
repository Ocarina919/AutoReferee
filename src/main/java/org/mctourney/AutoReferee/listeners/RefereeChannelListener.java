package org.mctourney.AutoReferee.listeners;

import java.util.Set;
import java.io.UnsupportedEncodingException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.mctourney.AutoReferee.AutoReferee;
import org.mctourney.AutoReferee.AutoRefMatch;

import com.google.common.collect.Sets;

public class RefereeChannelListener implements PluginMessageListener, Listener
{
	AutoReferee plugin = null;

	public RefereeChannelListener(Plugin p)
	{ plugin = (AutoReferee) p; }
	
	public void onPluginMessageReceived(String channel, Player player, byte[] mbytes)
	{
		try
		{
			String message = new String(mbytes, "UTF-8");
			AutoRefMatch match = plugin.getMatch(player.getWorld());
		}
		catch (UnsupportedEncodingException e) {  }
	}

	@EventHandler
	public void channelRegistration(PlayerRegisterChannelEvent event)
	{
		AutoRefMatch match = plugin.getMatch(event.getPlayer().getWorld());
		if (AutoReferee.REFEREE_PLUGIN_CHANNEL.equals(event.getChannel()) && match != null
			&& match.isReferee(event.getPlayer())) match.updateReferee(event.getPlayer());
	}
}