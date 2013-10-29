package com.Vislo.BetterSigns;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class InfoManager implements Listener
{
	private static Connect connect;
	private static Map<String, ServerStatus> status = new HashMap<String, ServerStatus>();

	public InfoManager()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, BetterSigns.getInstance());
		connect = Bukkit.getServer().getServicesManager().getRegistration(Connect.class).getProvider();
		connect.registerEvents(this);
		startSendingTimer();
	}

	public static void writeServerStatus(String username, ServerStatus st)
	{
		status.put(username, st);
	}

	public static ServerStatus getServerStatus(String username)
	{
		return status.get(username);
	}

	private void startSendingTimer()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				String motd = Bukkit.getMotd();
				if (connect == null || !connect.isConnected())
					return;
				String username = connect.getSettings().getUsername();
				String usersOnline = Bukkit.getOnlinePlayers().length + "";
				String maxUsers = Bukkit.getMaxPlayers() + "";
				try
				{
					MessageRequest request = new MessageRequest(Collections.<String> emptyList(), "BetterSigns", username + ",," + usersOnline + ",," + maxUsers + ",," + motd);
					connect.request(request);
				} catch (UnsupportedEncodingException | RequestException e)
				{
					e.printStackTrace();
				}
			}

		}.runTaskTimer(BetterSigns.getInstance(), Config.getDelaySeconds(), Config.getDelaySeconds());
	}

	public static Connect getLilyPad()
	{
		return connect;
	}

	@EventListener
	public void onMessage(MessageEvent event)
	{
		if (event.getChannel().equalsIgnoreCase("BetterSigns"))
		{
			String[] data = null;
			try
			{
				data = event.getMessageAsString().split(",,");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				return;
			}
			String usr = data[0];
			String usersOnline = data[1];
			String maxUsers = data[2];
			String motd = "";
			if (data.length == 4)
			{
				motd = data[3];
			}
			SignManager.updateSigns(usr, usersOnline, maxUsers, motd);
		}
	}
}
