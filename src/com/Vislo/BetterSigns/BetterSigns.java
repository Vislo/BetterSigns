package com.Vislo.BetterSigns;

import org.bukkit.plugin.java.JavaPlugin;

public class BetterSigns extends JavaPlugin
{
	private static BetterSigns plugin;
	public void onEnable()
	{
		plugin = this;
		saveDefaultConfig();
		Config.loadConfig(getConfig(),false);
		new InfoManager();
		new SignListener();
		getCommand("BetterSigns").setExecutor(new BetterSignsCommand());
	}
	
	public static BetterSigns getInstance()
	{
		return plugin;
	}
}
