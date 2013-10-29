package com.Vislo.BetterSigns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BetterSignsCommand implements CommandExecutor
{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmds, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "This command can only be executed in-game.");
			return true;
		}
			
		Player p = (Player) sender;
		if (args.length == 2 && args[0].equalsIgnoreCase("create") && (sender.hasPermission("bettersigns.create") || sender.hasPermission("bettersigns.*")))
		{
			Block sign = p.getTargetBlock(null, 10);
			if (!(sign.getState() instanceof Sign))
			{
				p.sendMessage(ChatColor.RED + "You're not pointing a sign.");
				return true;
			}
			Sign _sign = (Sign) sign.getState();
			SignManager.addSign(args[1], _sign.getLocation());
			_sign.setLine(0, "Please wait for");
			_sign.setLine(1, "the sign to");
			_sign.setLine(2, "update.");
			_sign.update();
			File Database = new File(BetterSigns.getInstance().getDataFolder() + File.separator + "SignDatabase.yml");
			FileConfiguration dbConfig = YamlConfiguration.loadConfiguration(Database);
			List<String> signs = dbConfig.getStringList("Signs." + args[1]);
			if (signs == null)
				signs = new ArrayList<String>();
			signs.add(Config.LocToString(_sign.getLocation()));
			dbConfig.set("Signs."+args[1], signs);
			try
			{
				dbConfig.save(Database);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			p.sendMessage(ChatColor.GREEN + "Your sign is now up! Wait a few seconds so the dsign updates himself!");
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("reload") && (sender.hasPermission("bettersigns.reload") || sender.hasPermission("bettersigns.*")))
		{
			BetterSigns.getInstance().reloadConfig();
			Config.loadConfig(BetterSigns.getInstance().getConfig(), true);
			p.sendMessage(ChatColor.GREEN + "Config Reloaded.");
			return true;
		}
		if(sender.hasPermission("bettersigns.reload") || sender.hasPermission("bettersigns.create") || sender.hasPermission("bettersigns.*"))
		{
			sender.sendMessage(ChatColor.RED+"Use the command properly: /bettersigns create username or /bettersigns reload");
			return true;
		}
		sender.sendMessage(ChatColor.RED + "You don't have enough permissions to use this.");
		return true;
	}

}
