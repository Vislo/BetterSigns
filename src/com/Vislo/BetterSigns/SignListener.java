package com.Vislo.BetterSigns;

import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import lilypad.client.connect.api.result.FutureResultListener;
import lilypad.client.connect.api.result.StatusCode;
import lilypad.client.connect.api.result.impl.RedirectResult;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener
{
	public SignListener()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, BetterSigns.getInstance());
	}
	@EventHandler
	public void breakSign(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if(e.getBlock().getState() instanceof Sign && SignManager.getServerSign(e.getBlock().getState().getLocation()) != null && (p.hasPermission("bettersigns.delete") || p.hasPermission("bettersigns.*")))
		{
			SignManager.deleteSign(SignManager.getServerSign(e.getBlock().getState().getLocation()), e.getBlock().getState().getLocation());
			p.sendMessage(ChatColor.RED+"Sign destroyed and removed");
			e.getBlock().setType(Material.AIR);
			e.setCancelled(true);
		}
		if(SignManager.getServerSign(e.getBlock().getState().getLocation()) != null && !(p.hasPermission("bettersigns.delete") || p.hasPermission("bettersigns.*")))
		{
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void clickSign(PlayerInteractEvent e)
	{
		if (e.getClickedBlock() == null && e.getClickedBlock().getState() == null)
			return;
		if (!(e.getClickedBlock().getState() instanceof Sign))
			return;
		final String user = SignManager.getServerSign(e.getClickedBlock().getState().getLocation());
		if (user == null)
			return;
		Player p = e.getPlayer();
		ServerStatus status = InfoManager.getServerStatus(user);
		if (status == ServerStatus.Offline)
		{
			p.sendMessage(ChatColor.RED + "This server is currently offline");
			return;
		}
		if (status == ServerStatus.Full)
		{
			p.sendMessage(ChatColor.RED + "This server is currently full");
			return;
		}
		RedirectRequest r = new RedirectRequest(user, p.getName());
		try
		{
			InfoManager.getLilyPad().request(r).registerListener(new FutureResultListener<RedirectResult>()
			{

				@Override
				public void onResult(RedirectResult result)
				{
					if(result.getStatusCode() != StatusCode.SUCCESS)
					{
						SignManager.updateOfflineSigns(user);
					}
				}
			});
		} catch (RequestException e1)
		{
			e1.printStackTrace();
		}

	}
}
