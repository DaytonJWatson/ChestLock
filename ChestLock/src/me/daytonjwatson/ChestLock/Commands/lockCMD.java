package me.daytonjwatson.ChestLock.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.daytonjwatson.ChestLock.Main;
import me.daytonjwatson.ChestLock.Utils;

public class lockCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("chestlock") && sender.hasPermission("chestlock.lock"))
			if (args.length == 0) {
				p.sendMessage(Utils.chat("&7+============== &6Chestlock &7==============+"));
				p.sendMessage(Utils.chat("&7/chestlock"));
				p.sendMessage(Utils.chat("&7/chestlock &6add &7<&6player&7>"));
			} else if (args[0].equalsIgnoreCase("public")) {
				if ((Main.getInstance()).givePermission.containsKey(p)) {
					(Main.getInstance()).givePermission.remove(p);
					(Main.getInstance()).makePublic.add(p);
					p.sendMessage(Utils.chat("&7Open &6Chest &7or &6Furnace &7to make it public!"));
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (args.length == 1) {
					p.sendMessage(Utils.chat("&7Please specify a &6Player&7!"));
				} else {
					if ((Main.getInstance()).makePublic.contains(p))
						(Main.getInstance()).makePublic.remove(p);
					Player player = Bukkit.getPlayer(args[1]);
					if (!player.hasPlayedBefore()) {
						p.sendMessage(Utils.chat("&7Unrecognized &6Player&7!"));
					} else {
						UUID targetPlayerUUID = player.getUniqueId();
						(Main.getInstance()).givePermission.put(p, targetPlayerUUID);
						p.sendMessage(Utils.chat("&7Open a &6Chest &7or &6Furnace &7you own to give " + player.getName() + " permission to open it!"));
					}
				}
			}
		return false;
	}
}