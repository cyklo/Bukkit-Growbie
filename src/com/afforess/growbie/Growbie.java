package com.afforess.growbie;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


/**
 * Growbie for Bukkit
 * 
 * @author Afforess, Cyklo, UnixSystem
 */
public class Growbie extends JavaPlugin {

	private final GrowbieBlockListener blockListener = new GrowbieBlockListener(this);
	private HashMap<Material,Integer> growablePlants;
	private HashMap<Material,Material> growableBlocks;
	private HashMap<Material,Material> spreadableBlocks;
	private Boolean betterTrees = false;

	public void checkConfigFile() {
		// create config file if it doesn't exist
		File configFile = new File(this.getDataFolder(), "config.yml");
		if (!configFile.canRead()) try {
			configFile.getParentFile().mkdirs();
			JarFile jar = new JarFile(this.getFile());
			JarEntry entry = jar.getJarEntry("config.yml");
			InputStream is = jar.getInputStream(entry);
			FileOutputStream os = new FileOutputStream(configFile);
			byte[] buf = new byte[(int)entry.getSize()];
			is.read(buf, 0, (int)entry.getSize());
			os.write(buf);
			os.close();
			this.getConfiguration().load();
		} catch (Exception e) {
			System.out.println("Growbie: could not create configuration file");
		}

		// load stuff
		growablePlants = new HashMap<Material,Integer>();
		growableBlocks = new HashMap<Material, Material>();
		spreadableBlocks = new HashMap<Material, Material>();
		
		try {
			HashMap<?, ?> srcGrowablePlants = (HashMap<?, ?>)this.getConfiguration().getProperty("growable_plants");
			HashMap<?, ?> srcGrowableBlocks = (HashMap<?, ?>)this.getConfiguration().getProperty("growable_blocks");
			HashMap<?, ?> srcSpreadableBlocks = (HashMap<?, ?>)this.getConfiguration().getProperty("spreadable_blocks");
			Iterator<?> i;
			Entry<?, ?> e;

			// load plants
			i = srcGrowablePlants.entrySet().iterator();
			while (i.hasNext()) {
				e = (Entry<?, ?>)i.next();
				Material m = Material.getMaterial(e.getKey().toString());
				if (m == null && e.getKey() instanceof Integer) m = Material.getMaterial(((Integer)e.getKey()).intValue());
				if (m != null) growablePlants.put(m, (Integer)e.getValue());
			}

			// load growable blocks
			i = srcGrowableBlocks.entrySet().iterator();
			while (i.hasNext()) {
				e = (Entry<?, ?>)i.next();
				Material[] m = {Material.getMaterial(e.getKey().toString()), Material.getMaterial(e.getValue().toString())};
				if (m[0] == null && e.getKey() instanceof Integer) m[0] = Material.getMaterial(((Integer)e.getKey()).intValue());
				if (m[1] == null && e.getValue() instanceof Integer) m[1] = Material.getMaterial(((Integer)e.getValue()).intValue());
				if (m.length == 2 && m[0] != null && m[1] != null) growableBlocks.put(m[0], m[1]);
			}
			
			// load spreadable blocks
			i = srcSpreadableBlocks.entrySet().iterator();
			while (i.hasNext()) {
				e = (Entry<?, ?>)i.next();
				Material[] m = {Material.getMaterial(e.getKey().toString()), Material.getMaterial(e.getValue().toString())};
				if (m[0] == null && e.getKey() instanceof Integer) m[0] = Material.getMaterial(((Integer)e.getKey()).intValue());
				if (m[1] == null && e.getValue() instanceof Integer) m[1] = Material.getMaterial(((Integer)e.getValue()).intValue());
				if (m.length == 2 && m[0] != null && m[1] != null) spreadableBlocks.put(m[0], m[1]);
			}
			
			// load better trees option
			betterTrees = getConfiguration().getBoolean("better_trees", false);
			
		} catch (Exception e) {
			System.out.println("Growbie: error loading configuration");
		}


	}

	public void onDisable() {
		System.out.println("Growbie disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, this.blockListener, Event.Priority.Normal, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled.");
		this.checkConfigFile();
	}

	public boolean isGrowablePlant(Material m) {
		return growablePlants.containsKey(m);
	}

	public int plantGrowthRate(Material m) {
		return growablePlants.get(m).intValue();
	}

	public boolean canGrowPlantOnBlock(Block b) {
		Block under = b.getFace(BlockFace.DOWN);
		return (b.getType() == Material.AIR && (under.getType() == Material.DIRT || under.getType() == Material.GRASS));
	}

	public boolean isGrowableBlock(Material m) {
		return growableBlocks.containsKey(m);
	}
	
	public boolean isSpreadableBlock(Material m) {
		return spreadableBlocks.containsKey(m);
	}
	
	public boolean isSapling(Material m) {
		return (betterTrees && (m == Material.SAPLING));
	}

	public Material blockForGrowableBlock(Material m) {
		return growableBlocks.get(m);
	}
	
	public Material blockForSpreadableBlock(Material m) {
		return spreadableBlocks.get(m);
	}
}
