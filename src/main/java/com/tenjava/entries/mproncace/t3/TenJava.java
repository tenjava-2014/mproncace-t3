package com.tenjava.entries.mproncace.t3;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin {
	
	public static TenJava plugin;
	public static Logger log;
	
	public void onEnable(){
		plugin = this;
		log = getLogger();
	}
	
	public void onDisable(){
		log = null;
		plugin = null;
	}
	
}
