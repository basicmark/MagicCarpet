package com.Android.magiccarpet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * Magic Carpet 2.0
 * Copyright (C) 2011 Celtic Minstrel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

public class MagicCarpet extends JavaPlugin {
	private final MagicPlayerListener playerListener = new MagicPlayerListener(this);
	private final MagicBlockListener blockListener = new MagicBlockListener(this);
	private Configuration config;
	private static Logger log = Logger.getLogger("Minecraft");
	Map<String, Carpet.LightMode> lights = new HashMap<String, Carpet.LightMode>();
	Map<String, Boolean> lightsOn = new HashMap<String, Boolean>();
	Map<String, Carpet> carpets = new HashMap<String, Carpet>();
	boolean crouchDef = true;
	boolean glowCenter = true;
	int carpSize = 5;
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		String name = pdfFile.getName();
		config = getConfiguration();
		
		loadConfig();
		if( !getDataFolder().exists()) getDataFolder().mkdirs();
		
		log.info("[" + name + "] " + name + " version " + pdfFile.getVersion() + " is enabled!");
		log.info("[" + name + "] Take yourself wonder by wonder, using /magiccarpet or /mc. ");
		registerEvents();
	}
	
	public void loadConfig() {
		config.load();
		crouchDef = config.getBoolean("crouch-descent", config.getBoolean("Crouch Default", true));
		glowCenter = config.getBoolean("center-light", config.getBoolean("Put glowstone for light in center", false));
		carpSize = config.getInt("default-size", config.getInt("Default size for carpet", 5));
		config.removeProperty("Use Properties Permissions");
		config.removeProperty("Crouch Default");
		config.removeProperty("Put glowstone for light in center");
		config.removeProperty("Default size for carpet");
		saveConfig();
	}
	
	public void saveConfig() {
		config.setProperty("Crouch Default", crouchDef);
		config.setProperty("Put glowstone for light in center", glowCenter);
		config.setProperty("Default size for carpet", carpSize);
		config.save();
	}
	
	@Override
	public void onDisable() {
		Iterator<String> e = carpets.keySet().iterator();
		// iterate through Hashtable keys Enumeration
		while(e.hasNext()) {
			String name = e.next();
			Carpet c = carpets.get(name);
			c.removeCarpet();
		}
		carpets.clear();
		System.out.println("Magic Carpet disabled. Thanks for trying the plugin!");
	}
	
	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		getCommand("magiccarpet").setExecutor(new CarpetCommand(this));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		Player player;
		if(sender instanceof Player) {
			player = (Player)sender;
		} else {
			return true;
		}
		Carpet carpet = carpets.get(player.getName());
		
		
			if(commandName.equals("ml")) {
				if(canLight(player)) {
					if(lightsOn.containsKey(player.getName())) {
						lightsOn.remove(player.getName());
						player.sendMessage("The luminous stones in the carpet slowly fade away.");
						if(carpet != null) carpet.lightsOff();;
					} else {
						lightsOn.put(player.getName(),true);
						player.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
						if(carpet != null) carpet.lightsOn();
					}
				} else {
					player.sendMessage("You do not have permission to use Magic Light!");
				}
				return true;
			} else {
				if(commandName.equals("carpetswitch")
					|| commandName.equals("mcs")) {
					if(canFly(player)) {
						boolean crouch = playerListener.CarpetSwitch(player.getName());
						if( !crouchDef) {
							if(crouch) {
								player.sendMessage("You now crouch to descend");
							} else {
								player.sendMessage("You now look down to descend");
							}
						} else {
							if( !crouch) {
								player.sendMessage("You now crouch to descend");
							} else {
								player.sendMessage("You now look down to descend");
							}
						}
					}
					return true;
				} else {
					return false;
				}
			}
	}
	
	public boolean canFly(Player player) {
		return player.hasPermission("magiccarpet.mc");
	}
	
	private boolean canLight(Player player) {
		return player.hasPermission("magiccarpet.ml");
	}
}
