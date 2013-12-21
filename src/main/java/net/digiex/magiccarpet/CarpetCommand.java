package net.digiex.magiccarpet;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class CarpetCommand implements CommandExecutor {

	private final MagicCarpet plugin;

	CarpetCommand(MagicCarpet plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
				Player who = null;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[1].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[1])) {
						who = p;
					}
				}
				if (who != null) {
					MagicCarpet.getCarpets().setGiven(who, true);
					who.sendMessage("The magic carpet has been given to you. Use /mc");
					sender.sendMessage("The magic carpet was given to "
							+ who.getName());
					return true;
				} else {
					sender.sendMessage("Can't find player " + args[1]);
					return true;
				}
			} else if (args.length == 2 && args[0].equalsIgnoreCase("take")) {
				Player who = null;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[1].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[1])) {
						who = p;
					}
				}
				if (who != null) {
					if (MagicCarpet.getCarpets().has(who)) {
						MagicCarpet.getCarpets().getCarpet(who).hide();
					}
					MagicCarpet.getCarpets().setGiven(who, false);
					who.sendMessage("The magic carpet has been taken from you.");
					sender.sendMessage("The magic carpet was taken from "
							+ who.getName());
					return true;
				} else {
					sender.sendMessage("Can't find player " + args[1]);
					return true;
				}
			} else {
				sender.sendMessage("Sorry, only players can use the carpet!");
				return true;
			}
		}
		Player player = (Player) sender;
		Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
		if (!MagicCarpet.canFly(player)) {
			player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
			return true;
		}
		int c;
		if (carpet == null) {
			if (player.getFallDistance() > 0
					&& !player.getLocation().getBlock().isLiquid()) {
				return true;
			}
			if (MagicCarpet.getCarpets().wasGiven(player)) {
				Carpet.create(player).show();
				return true;
			}
			if (plugin.getVault() != null) {
				if (MagicCarpet.canNotPay(player)) {
					Carpet.create(player).show();
					return true;
				}
				if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb.");
					return true;
				}
				if (MagicCarpet.chargeTimeBased && MagicCarpet.getCarpets().getTime(player) == 0L) {
					player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
					return true;
				}
			}
			Carpet.create(player).show();
			return true;
		}
		if (args.length < 1) {
			if (carpet.isVisible()) {
				carpet.hide();
				return true;
			} else {
				if (player.getFallDistance() > 0
						&& !player.getLocation().getBlock().isLiquid()) {
					return true;
				}
				if (MagicCarpet.getCarpets().wasGiven(player)) {
					carpet.show();
					return true;
				}
				if (plugin.getVault() != null) {
					if (MagicCarpet.canNotPay(player)) {
						Carpet.create(player).show();
						return true;
					}
					if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
						player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb.");
						return true;
					}
					if (MagicCarpet.chargeTimeBased && MagicCarpet.getCarpets().getTime(player) == 0L) {
						player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
						return true;
					}
				}
				carpet.show();
				return true;
			}
		} else {
			if (args.length == 2 && args[0].equals("give")) {
				if (player.isOp()) {
					Player who = null;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase()
								.contains(args[1].toLowerCase())
								|| p.getName().equalsIgnoreCase(args[1])) {
							who = p;
						}
					}
					if (who != null) {
						MagicCarpet.getCarpets().setGiven(who, true);
						who.sendMessage("The magic carpet has been given to you. Use /mc");
						player.sendMessage("You've given the magic carpet to "
								+ who.getName());
						return true;
					} else {
						player.sendMessage("Can't find player " + args[1]);
						return true;
					}
				} else {
					player.sendMessage("You don't have permission to use this.");
					return true;
				}
			} else if (args.length == 2 && args[0].equals("take")) {
				if (player.isOp()) {
					Player who = null;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase()
								.contains(args[1].toLowerCase())
								|| p.getName().equalsIgnoreCase(args[1])) {
							who = p;
						}
					}
					if (who != null) {
						if (MagicCarpet.getCarpets().has(who)) {
							MagicCarpet.getCarpets().getCarpet(who).hide();
						}
						MagicCarpet.getCarpets().setGiven(who, false);
						who.sendMessage("The magic carpet has been taken from you.");
						player.sendMessage("You've taken the magic carpet from "
								+ who.getName());
						return true;
					} else {
						player.sendMessage("Can't find player " + args[1]);
						return true;
					}
				} else {
					player.sendMessage("You don't have permission to use this.");
					return true;
				}
			}
			if (carpet.isVisible()) {
				try {
					c = Integer.valueOf(args[0]);
				} catch (NumberFormatException e) {
					String commanddata = "";
					for (String a : args) {
						if (commanddata.isEmpty()) {
							commanddata = a;
						} else {
							commanddata += " " + a;
						}
					}

					String[] splitData = commanddata.split(":");
					String material = splitData[0];
					byte data = 0;
					if (splitData.length == 2) {
						try {
							data = Byte.parseByte(splitData[1]);
						} catch (NumberFormatException ee) {
						}
					}
					
					Material m = Material.getMaterial(material.toUpperCase()
							.replace(" ", "_"));
					if (m != null) {
						carpet.changeCarpet(m, data);
						return true;
					} else {
						player.sendMessage("Material error; Material may be entered as GOLD_BLOCK or just plain gold block");
						return true;
					}
				}
				carpet.changeCarpet(c);
				return true;
			} else {
				player.sendMessage("You don't have a carpet yet.");
				return true;
			}
		}
	}
}
