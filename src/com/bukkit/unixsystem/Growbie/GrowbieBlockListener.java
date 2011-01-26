package com.bukkit.unixsystem.Growbie;

import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.BlockListener;

public class GrowbieBlockListener extends BlockListener {
	
	public static Growbie plugin;
	
	public GrowbieBlockListener(Growbie instance) {
		plugin = instance;
	}
	
	public void onBlockRightClick(BlockRightClickEvent event) {
		Player player = event.getPlayer();
		Inventory playerInventory = player.getInventory();
		Block block = event.getBlock();
		World world = player.getWorld();
		ItemStack holding = player.getItemInHand();
		
		if ((block.getType() == Material.RED_ROSE || block.getType() == Material.YELLOW_FLOWER || block.getType() == Material.PUMPKIN) 
				&& holding.getType() == Material.INK_SACK
				&& holding.getDamage() == 15 ) { // WHITE INK damage value, hard-coded for your pleasure
			
			Block northBlock = world.getBlockAt(block.getX() + 1, block.getY(), block.getZ());
			Block southBlock = world.getBlockAt(block.getX() - 1, block.getY(), block.getZ());
			Block eastBlock = world.getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
			Block westBlock = world.getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
					
			/* Under my current rule set, the block will only spawn in AIR, and only if the area underneath
			 * is DIRT or GRASS.  This is to prevent stuff like the chest exploit that was used on a lot of servers.*/
			if (northBlock.getType() == Material.AIR
					&& (world.getBlockAt(northBlock.getX(), northBlock.getY() - 1, northBlock.getZ()).getType() == Material.DIRT
						|| world.getBlockAt(northBlock.getX(), northBlock.getY() - 1, northBlock.getZ()).getType() == Material.GRASS)) {
				northBlock.setType(block.getType());
			}
			 
			if (southBlock.getType() == Material.AIR
					&& (world.getBlockAt(southBlock.getX(), southBlock.getY() - 1, southBlock.getZ()).getType() == Material.DIRT
						|| world.getBlockAt(southBlock.getX(), southBlock.getY() - 1, southBlock.getZ()).getType() == Material.GRASS)) {
				southBlock.setType(block.getType());
			}
			
			if (eastBlock.getType() == Material.AIR
					&& (world.getBlockAt(eastBlock.getX(), eastBlock.getY() - 1, eastBlock.getZ()).getType() == Material.DIRT
						|| world.getBlockAt(eastBlock.getX(), eastBlock.getY() - 1, eastBlock.getZ()).getType() == Material.GRASS)) {
				eastBlock.setType(block.getType());
			}
			
			if (westBlock.getType() == Material.AIR
					&& (world.getBlockAt(westBlock.getX(), westBlock.getY() - 1, westBlock.getZ()).getType() == Material.DIRT
							|| world.getBlockAt(westBlock.getX(), westBlock.getY() - 1, westBlock.getZ()).getType() == Material.GRASS)) {
				westBlock.setType(block.getType());
			}
			
			/* Make the bone meal decrease on use, as it normally would. */
			int amt = holding.getAmount();
			if (amt > 1) {
				--amt;
				holding.setAmount(amt);
			} else {
				playerInventory.remove(holding);
			}
		}
	}
}
