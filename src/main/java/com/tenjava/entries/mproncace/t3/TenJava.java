package com.tenjava.entries.mproncace.t3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin {
	
	public static TenJava plugin;
	public static Logger log;
	public static int CORRUPT_CHANCE;
	public static int SPREAD_CHANCE;
	
	public void onEnable(){
		plugin = this;
		log = getLogger();
		
		saveDefaultConfig();
		
		CORRUPT_CHANCE = getConfig().getInt("corruption-form-chance");
		SPREAD_CHANCE = getConfig().getInt("corruption-spread-chance");
		
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){
			public void run(){
				Random r = new Random();
				if (r.nextInt(CORRUPT_CHANCE) == 0){
					Chunk[] chunks = Bukkit.getWorlds().get(0).getLoadedChunks();
					Chunk chunk = chunks[r.nextInt(chunks.length)];
					boolean solid = false;
					int x = r.nextInt(16);
					int z = r.nextInt(16);
					Material[] validTypes = new Material[]{Material.DIRT, Material.GRASS, Material.STONE, Material.GRAVEL, Material.CLAY,
							Material.SAND, Material.COAL_ORE, Material.ICE, Material.SNOW_BLOCK, Material.HARD_CLAY, Material.STAINED_CLAY,
							Material.MYCEL, Material.PACKED_ICE};
					for (int y = 255; y > 0; y++){
						Block b = chunk.getBlock(x, y, z);
						if (b.getType() != Material.AIR && (Arrays.asList(validTypes).contains(b.getType()))){
							b.setType(Material.NETHERRACK);
						}
					}
				}
				if (r.nextInt(SPREAD_CHANCE) == 0){
					//TODO: handle spreading
				}
			}
		}, 0L, 20L);
		
	}
	
	public void onDisable(){
		log = null;
		plugin = null;
	}
	
}
