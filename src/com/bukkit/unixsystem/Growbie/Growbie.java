package com.bukkit.unixsystem.Growbie;

import java.io.File;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


/**
 * Growbie for Bukkit
 * 
 * @author UnixSystem
 */
public class Growbie extends JavaPlugin {

	private final GrowbieBlockListener blockListener = new GrowbieBlockListener(this);
	
	public Growbie(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	public void onDisable() {
		System.out.println("Growbie disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, this.blockListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled.");
	}
}
