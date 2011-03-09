package com.afforess.growbie;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.BlockListener;

public class GrowbieBlockListener extends BlockListener {

	public GrowbieBlockListener() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onBlockRightClick(BlockRightClickEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (!Gardener.isBonemeal(player.getItemInHand())) {
			return;
		}
		
		boolean action = Gardener.growPlants(block);
		if (!action) {
			action = Gardener.growBlocks(block);
		}
		if (!action) {
			action = Gardener.spreadBlocks(block);
		}
		if (!action) {
			action = Gardener.growTree(block);
		}

		if (action){
			Gardener.useItem(player);
		}
	}
}
