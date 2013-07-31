package net.digiex.magiccarpet;

import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.BOOKSHELF;
import static org.bukkit.Material.BRICK;
import static org.bukkit.Material.CLAY;
import static org.bukkit.Material.COAL_BLOCK;
import static org.bukkit.Material.COAL_ORE;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_BLOCK;
import static org.bukkit.Material.DIAMOND_ORE;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.DOUBLE_STEP;
import static org.bukkit.Material.EMERALD_BLOCK;
import static org.bukkit.Material.ENDER_STONE;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.HARD_CLAY;
import static org.bukkit.Material.HUGE_MUSHROOM_1;
import static org.bukkit.Material.HUGE_MUSHROOM_2;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.IRON_ORE;
import static org.bukkit.Material.JACK_O_LANTERN;
import static org.bukkit.Material.LAPIS_BLOCK;
import static org.bukkit.Material.LAPIS_ORE;
import static org.bukkit.Material.LEAVES;
import static org.bukkit.Material.LOG;
import static org.bukkit.Material.MELON_BLOCK;
import static org.bukkit.Material.MOSSY_COBBLESTONE;
import static org.bukkit.Material.MYCEL;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.NETHER_BRICK;
import static org.bukkit.Material.NOTE_BLOCK;
import static org.bukkit.Material.OBSIDIAN;
import static org.bukkit.Material.PUMPKIN;
import static org.bukkit.Material.QUARTZ_BLOCK;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.SNOW_BLOCK;
import static org.bukkit.Material.SOIL;
import static org.bukkit.Material.SOUL_SAND;
import static org.bukkit.Material.SPONGE;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WOOD;
import static org.bukkit.Material.WOOL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.Logger;

import net.digiex.magiccarpet.Metrics.Graph;
import net.digiex.magiccarpet.commands.BuyCommand;
import net.digiex.magiccarpet.commands.CarpetCommand;
import net.digiex.magiccarpet.commands.LightCommand;
import net.digiex.magiccarpet.commands.ReloadCommand;
import net.digiex.magiccarpet.commands.SwitchCommand;
import net.digiex.magiccarpet.commands.ToolCommand;
import net.digiex.magiccarpet.lib.VaultHandler;
import net.digiex.magiccarpet.lib.WorldGuardHandler;
import net.digiex.magiccarpet.nms.NMSHelper;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Magic Carpet 2.3 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class MagicCarpet extends JavaPlugin {

	private static EnumSet<Material> acceptableCarpet = EnumSet.of(STONE,
			GRASS, DIRT, COBBLESTONE, WOOD, BEDROCK, GOLD_ORE, IRON_ORE,
			COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK,
			SANDSTONE, NOTE_BLOCK, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP,
			BRICK, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, DIAMOND_ORE,
			DIAMOND_BLOCK, SOIL, SNOW_BLOCK, CLAY, PUMPKIN, NETHERRACK,
			SOUL_SAND, MYCEL, NETHER_BRICK, ENDER_STONE, HUGE_MUSHROOM_1,
			HUGE_MUSHROOM_2, MELON_BLOCK, COAL_BLOCK, EMERALD_BLOCK, HARD_CLAY,
			QUARTZ_BLOCK);
	private static EnumSet<Material> acceptableLight = EnumSet.of(GLOWSTONE,
			JACK_O_LANTERN);

	private Logger log;
	private Config config;
	private Carpets carpets;

	private VaultHandler vault;
	private static WorldGuardHandler worldGuardHandler;

	private void registerCommands() {
		getCommand("magiccarpet").setExecutor(new CarpetCommand(this));
		getCommand("magiclight").setExecutor(new LightCommand(this));
		getCommand("carpetswitch").setExecutor(new SwitchCommand(this));
		getCommand("magicreload").setExecutor(new ReloadCommand(this));
		getCommand("magiccarpetbuy").setExecutor(new BuyCommand(this));
		getCommand("magictools").setExecutor(new ToolCommand(this));
	}

	private void registerEvents(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private void startStats() {
		try {
			Metrics metrics = new Metrics(this);
			Graph graph = metrics.createGraph("Carpets");
			graph.addPlotter(new Metrics.Plotter("Total") {
				@Override
				public int getValue() {
					int i = 0;
					for (Iterator<Carpet> iterator = carpets.all().iterator(); iterator
							.hasNext();) {
						i = i + 1;
						iterator.next();
					}
					return i;
				}
			});
			graph.addPlotter(new Metrics.Plotter("Current") {
				@Override
				public int getValue() {
					int i = 0;
					for (Carpet c : carpets.all()) {
						if (c == null || !c.isVisible()) {
							continue;
						}
						i = i + 1;
					}
					return i;
				}
			});
			metrics.start();
		} catch (IOException e) {
			log.warning("Failed to submit stats.");
		}
	}

	public Carpets getCarpets() {
		return carpets;
	}

	public Config getMCConfig() {
		return config;
	}

	public boolean canFly(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.mc");
	}

	public boolean canLight(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.ml");
	}

	public boolean canSwitch(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.mcs");
	}

	public boolean canTool(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.mct");
	}

	public boolean canReload(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.mr");
	}

	public boolean canNotPay(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.np");
	}

	public static EnumSet<Material> getAcceptableCarpetMaterial() {
		return acceptableCarpet;
	}

	public static EnumSet<Material> getAcceptableLightMaterial() {
		return acceptableLight;
	}

	public boolean canFlyHere(Location location) {
		return (worldGuardHandler == null) ? true : worldGuardHandler
				.canFlyHere(location);
	}

	public boolean canFlyAt(Player player, int i) {
		if (i == config.getDefaultCarpSize()) {
			return true;
		}
		if (getCarpets().wasGiven(player)) {
			return true;
		}
		if (player.hasPermission("magiccarpet.*")) {
			return true;
		}
		return player.hasPermission("magiccarpet.mc." + i);
	}

	@Override
	public void onDisable() {
		if (config.getDefaultSaveCarpets()) {
			saveCarpets();
		} else {
			for (Carpet c : carpets.all()) {
				if (c == null || !c.isVisible()) {
					continue;
				}
				c.removeCarpet();
			}
		}
		log.info("is now disabled!");
	}

	@Override
	public void onEnable() {
		log = getLogger();
		new NMSHelper(this);
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		config = new Config(this);
		carpets = new Carpets().attach(this);
		if (config.getDefaultSaveCarpets()) {
			loadCarpets();
		}
		registerEvents(new MagicListener(this));
		registerCommands();
		getVault();
		getWorldGuard();
		startStats();
		log.info("is now enabled!");
	}

	public VaultHandler getVault() {
		if (!config.getDefaultCharge()) {
			return null;
		}
		if (vault != null) {
			return vault;
		}
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if (plugin == null || !(plugin instanceof net.milkbowl.vault.Vault)) {
			return null;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return null;
		}
		return vault = new VaultHandler(this, rsp.getProvider());
	}

	private void getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null
				|| !(plugin instanceof com.sk89q.worldguard.bukkit.WorldGuardPlugin)) {
			return;
		}
		worldGuardHandler = new WorldGuardHandler(
				(com.sk89q.worldguard.bukkit.WorldGuardPlugin) plugin);
	}

	private File carpetsFile() {
		return new File(getDataFolder(), "carpets.dat");
	}

	public void saveCarpets() {
		File carpetDat = carpetsFile();
		log.info("Saving carpets...");
		if (!carpetDat.exists()) {
			try {
				carpetDat.createNewFile();
			} catch (Exception e) {
				log.severe("Unable to create carpets.dat");
			}
		}
		try {
			FileOutputStream file = new FileOutputStream(carpetDat);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(carpets);
			out.close();
		} catch (Exception e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
		}
		carpets.clear();
	}

	public void loadCarpets() {
		File carpetDat = carpetsFile();
		if (!carpetDat.exists()) {
			return;
		}
		log.info("Loading carpets...");
		try {
			FileInputStream file = new FileInputStream(carpetDat);
			ObjectInputStream in = new ObjectInputStream(file);
			carpets = (Carpets) in.readObject();
			carpets.attach(this);
			in.close();
		} catch (Exception e) {
			log.warning("Error loading carpets.dat; it may be corrupt and will be overwritten with new data.");
			return;
		}
		carpets.checkCarpets();
	}
}