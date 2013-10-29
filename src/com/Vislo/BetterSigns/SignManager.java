package com.Vislo.BetterSigns;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SignManager
{
	private static Map<Location, String> signsLoc = new HashMap<Location, String>();
	private static Map<String, Set<Location>> signsUpdate = new HashMap<String, Set<Location>>();

	public static void addSign(String username, Location location)
	{
		Set<Location> signs = null;
		if (signsUpdate.containsKey(username))
			signs = signsUpdate.get(username);
		else
			signs = new HashSet<Location>();
		signs.add(location);
		signsUpdate.put(username, signs);
		signsLoc.put(location, username);
	}

	public static void deleteSign(String username, Location sign)
	{
		File Database = new File(BetterSigns.getInstance().getDataFolder() + File.separator + "SignDatabase.yml");
		FileConfiguration dbConfig = YamlConfiguration.loadConfiguration(Database);
		List<String> signs = dbConfig.getStringList("Signs." + username);
		if (signs == null)
			return;
		signs.remove(Config.LocToString(sign));
		dbConfig.set("Signs."+username, signs);
		try
		{
			dbConfig.save(Database);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		signsLoc.remove(signs);
		Set<Location> set = signsUpdate.get(username);
		set.remove(signs);
		signsUpdate.put(username, set);
	}

	public static void updateSigns(String username, String usersOnline, String maxUsers, String motd)
	{
		if (signsLoc.isEmpty() || !signsUpdate.containsKey(username))
			return;
		String[] signsLine = new String[4];
		String status = Config.getStatus()[0];
		ServerStatus stat = InfoManager.getServerStatus(username);
		if (stat == ServerStatus.Offline)
			status = Config.getStatus()[1];
		if (stat == ServerStatus.Full)
			status = Config.getStatus()[2];
		signsLine[0] = replaceInfo(Config.getSignFormat().get(0), usersOnline, maxUsers, motd, status);
		signsLine[1] = replaceInfo(Config.getSignFormat().get(1), usersOnline, maxUsers, motd, status);
		signsLine[2] = replaceInfo(Config.getSignFormat().get(2), usersOnline, maxUsers, motd, status);
		signsLine[3] = replaceInfo(Config.getSignFormat().get(3), usersOnline, maxUsers, motd, status);
		for (Location sign : signsUpdate.get(username))
		{
			if (sign.getBlock().getState() instanceof Sign)
			{
				Sign s = (Sign) sign.getBlock().getState();
				for (int x = 0; x < 4; x++)
					s.setLine(x, signsLine[x]);
				s.update();
				
			} else
			{
				deleteSign(username,sign);
			}
		}

	}

	private static String replaceInfo(String info, String usersOnline, String maxUsers, String motd, String status)
	{
		info = info.replace("%status%", status);
		info = info.replace("%onusers%", usersOnline);
		info = info.replace("%maxusers%", maxUsers);
		info = info.replace("%motd%", motd);
		return info;
	}

	public static void updateOfflineSigns(String username)
	{
		if (signsLoc.isEmpty() || !signsUpdate.containsKey(username))
			return;
		String[] signsLine = new String[4];
		String status = Config.getStatus()[0];
		ServerStatus stat = InfoManager.getServerStatus(username);
		if (stat == ServerStatus.Offline)
			status = Config.getStatus()[1];
		if (stat == ServerStatus.Full)
			status = Config.getStatus()[2];
		signsLine[0] = replaceInfo(Config.getSignFormat().get(0), "0", "0", "", status);
		signsLine[1] = replaceInfo(Config.getSignFormat().get(1), "0", "0", "", status);
		signsLine[2] = replaceInfo(Config.getSignFormat().get(2), "0", "0", "", status);
		signsLine[3] = replaceInfo(Config.getSignFormat().get(3), "0", "0", "", status);
		for (Location sign : signsUpdate.get(username))
		{
			Sign s = (Sign) sign.getBlock().getState();
			for (int x = 0; x < 4; x++)
				s.setLine(x, signsLine[x]);
			s.update();
		}
	}

	public static String getServerSign(Location loc)
	{
		return signsLoc.get(loc);
	}
}