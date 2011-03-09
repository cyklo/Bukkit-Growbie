package com.afforess.growbie;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.BlockListener;
import java.util.ArrayList;
import org.bukkit.block.BlockFace;

public class GrowbieBlockListener extends BlockListener {

	public static Growbie plugin;

	public GrowbieBlockListener(Growbie instance) {
		plugin = instance;
	}

	private void useItem(Player player) {
		/* Make the bone meal decrease on use, as it normally would. */
		int amt = player.getItemInHand().getAmount();
		if (amt > 1) {
			--amt;
			player.getItemInHand().setAmount(amt);
		} else {
			player.getInventory().remove(player.getItemInHand());
		}
	}
	@Override
	public void onBlockRightClick(BlockRightClickEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		ItemStack holding = player.getItemInHand();
		
		if (holding.getType() != Material.INK_SACK || holding.getDurability() != 15) {
			return;
		}

		if (plugin.isGrowablePlant(block.getType())) {
			// grow plant

			// blocks where we can grow on
			ArrayList<Block> growInBlocks = new ArrayList<Block>(8);
			growInBlocks.add(block.getFace(BlockFace.NORTH));
			growInBlocks.add(block.getFace(BlockFace.NORTH_EAST));
			growInBlocks.add(block.getFace(BlockFace.EAST));
			growInBlocks.add(block.getFace(BlockFace.SOUTH_EAST));
			growInBlocks.add(block.getFace(BlockFace.SOUTH));
			growInBlocks.add(block.getFace(BlockFace.SOUTH_WEST));
			growInBlocks.add(block.getFace(BlockFace.WEST));
			growInBlocks.add(block.getFace(BlockFace.NORTH_WEST));

			// get number of plants to grow
			int plantsToGrow = plugin.plantGrowthRate(block.getType());

			boolean didGrow = false;
			while (plantsToGrow > 0 && !growInBlocks.isEmpty()) {
				// get a random block from the list
				int i = Math.round((float)Math.random() * (growInBlocks.size()-1));
				Block growBlock = growInBlocks.get(i);
				growInBlocks.remove(i);
				// try upper and lower block if this one isn't available
				if (!plugin.canGrowPlantOnBlock(growBlock)) {
					growBlock = growBlock.getFace(BlockFace.UP);
				}
				if (!plugin.canGrowPlantOnBlock(growBlock)) {
					growBlock = growBlock.getFace(BlockFace.DOWN, 2);
				}
				if (!plugin.canGrowPlantOnBlock(growBlock)) {
					continue;
				}
				// grow plant
				growBlock.setType(block.getType());
				didGrow = true;
				plantsToGrow--;
			}

			// use 1 bone meal, only if something happened
			if (didGrow) {
				useItem(player);
			}

		} else if (plugin.isGrowableBlock(block.getType())) {
			
			boolean didGrow = false;
			//Leaves is a special case (really just a special case of me abusing the config file, but whatever)
			if (block.getType().equals(Material.LOG) && plugin.blockForGrowableBlock(block.getType()).equals(Material.LEAVES)) {
				int range = 1;
				for (int dx = -(range); dx <= range; dx++){
					for (int dy = -(range); dy <= range; dy++){
						for (int dz = -(range); dz <= range; dz++){
							Block loop = block.getRelative(dx, dy, dz);
							if (loop.getTypeId() == Material.AIR.getId()) {
								loop.setType(plugin.blockForGrowableBlock(block.getType()));
								didGrow = true;
							}
						}
					}
				}
			}
			else {
				// transform block
				block.setType(plugin.blockForGrowableBlock(block.getType()));
				didGrow = true;
			}
			// use 1 bone meal, only if something happened
			if (didGrow) {
				useItem(player);
			}
		} else if (plugin.isSpreadableBlock(block.getType())) {
			
			boolean didGrow = false;
			
			// Let's loop over three surrounding dimensions
			int range = 1;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						
						Block loop = block.getRelative(dx, dy, dz);
						
						if (loop.getType() == plugin.blockForSpreadableBlock(block.getType())) {
							
							// Special check for grass - only grow if air on block above
							if(block.getType() == Material.GRASS && loop.getRelative(BlockFace.UP).getType() != Material.AIR) {
								continue;
							}
							
							loop.setType(block.getType());
							didGrow = true;
						}
					}
				}
			}
			// use 1 bone meal, only if something happened
			if (didGrow) {
				useItem(player);
			}
		} else if(plugin.isSapling(block.getType())){

			// Biome data stolen from here
			// http://www.minecraftforum.net/viewtopic.php?f=1020&t=151067
			// Seems slightly incorrect though... definitely get some Birch in rainforests
			// May need to play with probabilities

			TreeType treeKind = TreeType.TREE;
			Double treeRoll = Math.random();

			switch(block.getBiome()) {
			case RAINFOREST:
				if(treeRoll <= 0.33) { treeKind = TreeType.BIG_TREE; }
				break;
			case SWAMPLAND:
			case FOREST:
				if(treeRoll <= 0.20) { treeKind = TreeType.BIRCH; }
				else if(treeRoll <=  0.47) { treeKind = TreeType.BIG_TREE; }
				break;
			case TUNDRA:
			case TAIGA:
				if(treeRoll <= 0.33) { treeKind = TreeType.REDWOOD; }
				else { treeKind = TreeType.TALL_REDWOOD; }
				break;
			default:
				if(treeRoll <= 0.10) { treeKind = TreeType.BIG_TREE; }
				break;
			}
			
			block.setType(Material.AIR);

			if(!block.getWorld().generateTree(block.getLocation(), treeKind)) {
				block.setType(Material.SAPLING);
				// We do not need to use useItem() as this will pass
				// through to the Minecraft engine and also fail,
				// consuming the bonemeal itself
			} else {
				useItem(player);
			}
		}
	}
}
