package com.afforess.growbie;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A utility class for handling Growbie events
 */
public abstract class Gardener {
	
	public static boolean isBonemeal(ItemStack item) {
		return item.getType() == Material.INK_SACK && item.getDurability() == 15;
	}
	
	public static boolean growPlants(Block block) {
		boolean didGrow = false;
		if (GrowbieConfiguration.isGrowablePlant(block.getType())) {
			int plantsToGrow = GrowbieConfiguration.plantGrowthRate(block.getType());

			//Populate list of suitable blocks adjacent to us
			ArrayList<Block> growInBlocks = new ArrayList<Block>(27);
			int range = 2;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						growInBlocks.add(block.getRelative(dx, dy, dz));
					}
				}
			}

			while (plantsToGrow > 0 && !growInBlocks.isEmpty()) {
				// get a random block from the list
				int i = Math.round((float)Math.random() * (growInBlocks.size()-1));
				Block growBlock = growInBlocks.get(i);
				growInBlocks.remove(i);
				
				if (GrowbieConfiguration.canGrowPlantOnBlock(growBlock)) {
					// grow plant
					growBlock.setType(block.getType());
					didGrow = true;
					plantsToGrow--;
				}
			}
		}
		return didGrow;
	}
	
	public static boolean growBlocks(Block block) {
		boolean didGrow = false;
		if (GrowbieConfiguration.isGrowableBlock(block.getType())) {
			//Leaves is a special case (really just a special case of me abusing the config file, but whatever)
			if (block.getType().equals(Material.LOG) && GrowbieConfiguration.blockForGrowableBlock(block.getType()).equals(Material.LEAVES)) {
				int range = 1;
				for (int dx = -(range); dx <= range; dx++){
					for (int dy = -(range); dy <= range; dy++){
						for (int dz = -(range); dz <= range; dz++){
							Block loop = block.getRelative(dx, dy, dz);
							if (loop.getTypeId() == Material.AIR.getId()) {
								loop.setType(GrowbieConfiguration.blockForGrowableBlock(block.getType()));
								didGrow = true;
							}
						}
					}
				}
			}
			// if the target is grass and no air above, do not do
			else if(block.getRelative(BlockFace.UP).getType() == Material.AIR || GrowbieConfiguration.blockForGrowableBlock(block.getType()) != Material.GRASS) {
				// transform block
				block.setType(GrowbieConfiguration.blockForGrowableBlock(block.getType()));
				didGrow = true;
			}
		}
		return didGrow;
	}
	
	public static boolean spreadBlocks(Block block) {
		boolean didGrow = false;
		if (GrowbieConfiguration.isSpreadableBlock(block.getType())) {

			// Let's loop over three surrounding dimensions
			int range = 1;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						
						Block loop = block.getRelative(dx, dy, dz);
						
						if (loop.getType() == GrowbieConfiguration.blockForSpreadableBlock(block.getType())) {
							
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
		}
		return didGrow;
	}
	
	public static boolean growTree(Block block) {
		if(GrowbieConfiguration.isSapling(block.getType())){

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
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static void useItem(Player player) {
		/* Make the bone meal decrease on use, as it normally would. */
		int amt = player.getItemInHand().getAmount();
		if (amt > 1) {
			--amt;
			player.getItemInHand().setAmount(amt);
		} else {
			player.getInventory().remove(player.getItemInHand());
		}
	}

}
