package com.Vislo.BetterSigns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
	private static Long delaySeconds;
	private static List<String> signFormat = new ArrayList<String>();
	private static String[] status = new String[3];

	public static void loadConfig(FileConfiguration config,boolean reload)
	{
		
		delaySeconds = config.getLong("delayPing");
		for(String k :  config.getStringList("Format"))
		{
			signFormat.add(k.replace("&", "§"));
		}		
		status[0] = config.getString("Status.Online").replace("&", "§");
		status[1] = config.getString("Status.Offline").replace("&", "§");
		status[2] = config.getString("Status.Full").replace("&", "§");
		if(!reload)
			return;
		File Database = new File(BetterSigns.getInstance().getDataFolder() + File.separator + "SignDatabase.yml");
		if (!Database.exists())
		{
			try
			{
				Database.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		FileConfiguration dbConfig = YamlConfiguration.loadConfiguration(Database);
		ConfigurationSection servs = dbConfig.getConfigurationSection("Signs");
		if (servs != null)
		{
			for (String key : servs.getKeys(false))
			{
				for(String loc : dbConfig.getStringList("Signs."+key))
				{
					SignManager.addSign(key, StringToLoc(loc));
				}
			}
		}

	}
	public static String LocToString(Location loc)
	{
		String x = String.valueOf(loc.getX());
		String FullData = x.replace("-", "e") + "/" + loc.getY() + "/" + loc.getZ() + "/" + loc.getYaw() + "/" + loc.getPitch()+"/"+loc.getWorld().getName();
		return FullData;
	}

	public static Location StringToLoc(String loc)
	{
		if (loc == null)
			return null;
		String[] Data = loc.split("/");
		double X = Double.parseDouble(Data[0].replace("e", "-"));
		double Y = Double.parseDouble(Data[1]);
		double Z = Double.parseDouble(Data[2]);
		float Yaw = Float.parseFloat(Data[3]);
		float Pitch = Float.parseFloat(Data[4]);
		return new Location(Bukkit.getWorld(Data[5]), X, Y, Z, Yaw, Pitch);
		
	}
	public static String[] getStatus()
	{
		return status;
	}

	public static List<String> getSignFormat()
	{
		return signFormat;
	}

	public static Long getDelaySeconds()
	{
		return delaySeconds;
	}

}
