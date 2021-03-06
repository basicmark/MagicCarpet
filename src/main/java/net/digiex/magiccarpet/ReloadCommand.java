package net.digiex.magiccarpet;

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
public class ReloadCommand implements CommandExecutor {

	private final MagicCarpet plugin;

	ReloadCommand(MagicCarpet plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			reload();
			sender.sendMessage("has been reloaded!");
			return true;
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (MagicCarpet.canReload(player)) {
				reload();
				player.sendMessage("MagicCarpet has been reloaded!");
			} else {
				player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
			}
			return true;
		}
		return false;
	}

	private void reload() {
		MagicCarpet.getCarpets().clear();
		if (plugin.getVault() != null) {
			plugin.getVault().getPackages().clear();
		}
		plugin.loadSettings();
		if (plugin.getVault() != null) {
			plugin.getVault().loadPackages();
		}
		if (MagicCarpet.saveCarpets) {
			plugin.saveCarpets();
			plugin.loadCarpets();
		}
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (MagicCarpet.getCarpets().has(p)) {
				Carpet.create(p).show();
			}
		}
	}
}
