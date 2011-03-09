package com.afforess.growbie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public abstract class GrowbieConfiguration {
	private static HashMap<Material,Integer> growablePlants;
	private static HashMap<Material,Material> growableBlocks;
	private static HashMap<Material,Material> spreadableBlocks;
	private static Boolean betterTrees = false;
	
	
	public static void checkConfigFile() {
		// create config file if it doesn't exist
		File configFile = new File(Growbie.instance.getDataFolder(), "config.yml");
		if (!configFile.canRead()) try {
			configFile.getParentFile().mkdirs();
			JarFile jar = new JarFile(Growbie.growbie);
			JarEntry entry = jar.getJarEntry("config.yml");
			InputStream is = jar.getInputStream(entry);
			FileOutputStream os = new FileOutputStream(configFile);
			byte[] buf = new byte[(int)entry.getSize()];
			is.read(buf, 0, (int)entry.getSize());
			os.write(buf);
			os.close();
			Growbie.instance.getConfiguration().load();
		} catch (Exception e) {
			System.out.println("Growbie: could not create configuration file");
		}

		// load stuff
		growablePlants = new HashMap<Material,Integer>();
		growableBlocks = new HashMap<Material, Material>();
		spreadableBlocks = new HashMap<Material, Material>();
		
		try {
			HashMap<?, ?> srcGrowablePlants = (HashMap<?, ?>)Growbie.instance.getConfiguration().getProperty("growable_plants");
			HashMap<?, ?> srcGrowableBlocks = (HashMap<?, ?>)Growbie.instance.getConfiguration().getProperty("growable_blocks");
			HashMap<?, ?> srcSpreadableBlocks = (HashMap<?, ?>)Growbie.instance.getConfiguration().getProperty("spreadable_blocks");
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
			betterTrees = Growbie.instance.getConfiguration().getBoolean("better_trees", false);
			
		} catch (Exception e) {
			System.out.println("Growbie: error loading configuration");
		}
	}
	
	public static boolean isGrowablePlant(Material m) {
		return growablePlants.containsKey(m);
	}

	public static int plantGrowthRate(Material m) {
		return growablePlants.get(m).intValue();
	}

	public static boolean canGrowPlantOnBlock(Block b) {
		Block under = b.getFace(BlockFace.DOWN);
		return (b.getType() == Material.AIR && (under.getType() == Material.DIRT || under.getType() == Material.GRASS));
	}

	public static boolean isGrowableBlock(Material m) {
		return growableBlocks.containsKey(m);
	}
	
	public static boolean isSpreadableBlock(Material m) {
		return spreadableBlocks.containsKey(m);
	}
	
	public static boolean isSapling(Material m) {
		return (betterTrees && (m == Material.SAPLING));
	}

	public static Material blockForGrowableBlock(Material m) {
		return growableBlocks.get(m);
	}
	
	public static Material blockForSpreadableBlock(Material m) {
		return spreadableBlocks.get(m);
	}
}
