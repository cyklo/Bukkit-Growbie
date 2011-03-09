package com.afforess.growbie;

import java.io.File;

import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Growbie for Bukkit
 * 
 * @author Afforess, Cyklo, UnixSystem
 */
public class Growbie extends JavaPlugin {

	private final GrowbieBlockListener blockListener = new GrowbieBlockListener();
	public static Growbie instance;
	public static File growbie;

	public void onDisable() {
		instance = null;
		System.out.println(getDescription().getName() +" disabled.");
	}

	public void onEnable() {
		instance = this;
		growbie = this.getFile();
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, this.blockListener, Event.Priority.Normal, this);
		System.out.println( getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
		GrowbieConfiguration.checkConfigFile();
	}
}
