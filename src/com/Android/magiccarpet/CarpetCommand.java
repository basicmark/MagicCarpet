package com.Android.magiccarpet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

public class CarpetCommand implements CommandExecutor {
	private MagicCarpet plugin;

	public CarpetCommand(MagicCarpet plug) {
		plugin = plug;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Sorry, only players can use the carpet!");
			return true;
		}
		Player player = (Player)sender;
		int c = 5;
		Carpet carpet = plugin.carpets.get(player.getName());
		if(plugin.canFly(player)) {
			if(carpet == null) {
				if(args.length < 1) {
					player.sendMessage("A glass carpet appears below your feet.");
					Carpet newCarpet = new Carpet(player.getLocation(), plugin.carpSize, plugin.lights.get(player.getName()), plugin.lightsOn.get(player.getName()));
					plugin.carpets.put(player.getName(), newCarpet);
				} else {
					try {
						c = Integer.valueOf(args[0]);
					} catch(NumberFormatException e) {
						player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
						return false;
					}
					
					if(c != 3 && c != 5 && c != 7) {
						player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
						return false;
					}
					player.sendMessage("A glass carpet appears below your feet.");
					Carpet newCarpet = new Carpet(player.getLocation(), c, plugin.lights.get(player.getName()), plugin.lightsOn.get(player.getName()));
					plugin.carpets.put(player.getName(), newCarpet);
				}
				
			}
			if(carpet != null) {
				if(args.length == 1) {
					try {
						c = Integer.valueOf(args[0]);
					} catch(NumberFormatException e) {
						player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
						return false;
					}
					
					if(c != 3 && c != 5 && c != 7) {
						player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
						return false;
					}
					if(c != carpet.getSize()) {
						player.sendMessage("The carpet seems to react to your words, and suddenly changes shape!");
						carpet.changeCarpet(c);
					} else {
						player.sendMessage("Poof! The magic carpet disappears.");
						plugin.carpets.remove(player.getName());
						carpet.removeCarpet();
					}
				} else {
					player.sendMessage("Poof! The magic carpet disappears.");
					plugin.carpets.remove(player.getName());
					carpet.removeCarpet();
				}
				
			}
			return true;
		} else {
			player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
			return true;
		}
	
	}
	
}
