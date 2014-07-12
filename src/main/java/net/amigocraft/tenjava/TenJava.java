package net.amigocraft.tenjava;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin {

	public static TenJava plugin;
	public static Logger log;
	public static int CORRUPT_CHANCE;
	public static int SPREAD_CHANCE;
	public static Material CORRUPT_BLOCK;
	public static Material CLEANSE_BLOCK;
	public static int CLEANSE_RADIUS;
	public static Connection conn;
	public static Statement st;
	public static ResultSet rs;

	public static List<Integer[]> corruptBlocks = new ArrayList<Integer[]>();

	public void onEnable(){

		plugin = this;
		log = getLogger();

		saveDefaultConfig();

		CORRUPT_CHANCE = getConfig().getInt("corruption-form-chance"); // for efficiency
		SPREAD_CHANCE = getConfig().getInt("corruption-spread-chance");
		{
			CORRUPT_BLOCK = Material.getMaterial(getConfig().getString("corrupt-block")) != null ?
					Material.getMaterial(getConfig().getString("corrupt-block")) : Material.NETHERRACK;
		}
		{
			CORRUPT_BLOCK = Material.getMaterial(getConfig().getString("cleanse-block")) != null ?
					Material.getMaterial(getConfig().getString("cleanse-block")) : Material.GLOWSTONE;
		}
		CLEANSE_RADIUS = getConfig().getInt("cleanse-radius");

		try {
			Class.forName("org.sqlite.JDBC"); // to this day I still have no clue why this is necessary
			String dbPath = "jdbc:sqlite:" + getDataFolder() + File.separator + "data.db";
			if (!new File(getDataFolder(), "data.db").exists()){
				try {
					new File(getDataFolder(), "data.db").createNewFile(); // create the database file
				}
				catch (IOException ex){
					ex.printStackTrace();
					log.severe("Failed to create database file!");
				}
			}
			conn = DriverManager.getConnection(dbPath);
			st = conn.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS blocks (" +
					"id INTEGER NOT NULL PRIMARY KEY," +
					"x INTEGER NOT NULL," +
					"y INTEGER NOT NULL," +
					"z INTEGER NOT NULL)");
			rs = st.executeQuery("SELECT * FROM blocks");
			while (rs.next()){
				corruptBlocks.add(new Integer[]{rs.getInt("x"), rs.getInt("y"), rs.getInt("z")});
			}
		}
		catch (SQLException | ClassNotFoundException ex){
			ex.printStackTrace();
			log.severe("Failed to create database!");
		}
		finally {
			try {
				rs.close();
				st.close();
				conn.close();
			}
			catch (SQLException ex){
				ex.printStackTrace();
				log.severe("Failed to close the database connection!");
			}
		}
		List<Material> corruptTypes1 = new ArrayList<Material>();
		List<Material> spreadTypes1 = new ArrayList<Material>();
		for (String m : getConfig().getStringList("allowed-form-types")){
			Material mat = Material.getMaterial(m);
			if (mat != null)
				corruptTypes1.add(mat);
		}
		for (String m : getConfig().getStringList("allowed-spread-types")){
			Material mat = Material.getMaterial(m);
			if (mat != null)
				spreadTypes1.add(mat);
		}
		final List<Material> corruptTypes = corruptTypes1;
		final List<Material> spreadTypes = spreadTypes1;
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){
			public void run(){
				Random r = new Random(); // now I'm automatically complying to the theme (*holds up spork*)
				if (r.nextInt(CORRUPT_CHANCE) == 0){
					Chunk[] chunks = Bukkit.getWorlds().get(0).getLoadedChunks();
					Chunk chunk = chunks[r.nextInt(chunks.length)];
					int x = r.nextInt(16);
					int z = r.nextInt(16);
					for (int y = 255; y > 0; y++){
						Block b = chunk.getBlock(x, y, z);
						if (b.getType() != Material.AIR && (corruptTypes.contains(b.getType()))){
							boolean protect = false;
							for (int xx = -5; xx <= 5; xx++){
								for (int yy = -5; yy <= 5; yy++){
									for (int zz = -5; zz <= 5; zz++){
										if (b.getWorld().getBlockAt(b.getX() + xx, b.getY() + yy, b.getZ() + zz).getType() == Material.GLOWSTONE)
											protect = true;
									}
								}
							}
							if (!protect){
								b.setType(CORRUPT_BLOCK);
								corruptBlocks.add(new Integer[]{b.getX(), b.getY(), b.getZ()});
							}
							break;
						}
					}
				}
				List<Integer[]> buffer = new ArrayList<Integer[]>();
				for (Integer[] ia : corruptBlocks){
					if (r.nextInt(SPREAD_CHANCE) == 0){
						Block b = Bukkit.getWorlds().get(0).getBlockAt(ia[0], ia[1], ia[2]);
						BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
						Block bl = null;
						int i = 0;
						while (bl == null){
							if (i >= 10)
								break; // to prevent an infinite loop
							i += 1;
							BlockFace face = faces[r.nextInt(4)];
							Block tb = b.getRelative(face);
							for (int y = b.getY(); tb.getType() == Material.AIR ? y > 0 : y < 255; y += (tb.getType() == Material.AIR ? -1 : 1)){
								if (tb.getWorld().getBlockAt(tb.getX(), y, tb.getZ()).getType() != Material.AIR &&
										(spreadTypes.contains(tb.getType()))){
									boolean protect = false;
									for (int xx = -CLEANSE_RADIUS; xx <= CLEANSE_RADIUS; xx++){
										for (int yy = -CLEANSE_RADIUS; yy <= CLEANSE_RADIUS; yy++){
											for (int zz = -CLEANSE_RADIUS; zz <= CLEANSE_RADIUS; zz++){
												if (b.getWorld().getBlockAt(tb.getX() + xx, tb.getY() + yy,
														tb.getZ() + zz).getType() == CLEANSE_BLOCK){
													protect = true;
													break;
												}
											}
											if (protect)
												break;
										}
										if (protect)
											break;
									}
									if (!protect){
										bl = tb.getWorld().getBlockAt(tb.getX(), y, tb.getZ());
										break;
									}
									else
										break;
								}
							}
						}
						if (bl != null){
							bl.setType(CORRUPT_BLOCK);
							buffer.add(new Integer[]{bl.getX(), bl.getY(), bl.getZ()});
						}
					}
				}
				corruptBlocks.addAll(buffer);
			}
		}, 0L, 20L);
	}

	public void onDisable(){
		if (corruptBlocks.size() > 0){
			try {
				Class.forName("org.sqlite.JDBC");
				String dbPath = "jdbc:sqlite:" + getDataFolder() + File.separator + "data.db";
				conn = DriverManager.getConnection(dbPath);
				st = conn.createStatement();
				for (Integer[] ia : corruptBlocks){
					st.executeUpdate("INSERT INTO `blocks` (x, y, z) VALUES (" + ia[0] + ", " + ia[1] + ", " + ia[2] + ")");
					System.out.println(ia[0] + ", " + ia[1] + ", " + ia[2]);
				}
			}
			catch (SQLException | ClassNotFoundException ex){
				ex.printStackTrace();
			}
			finally {
				try {
					rs.close();
					st.close();
					conn.close();
				}
				catch (Exception ex){
					ex.printStackTrace();
					log.severe("Failed to close database connection!");
				}
			}
		}
		rs = null;
		st = null;
		conn = null;
		log = null;
		plugin = null;
	}

}
