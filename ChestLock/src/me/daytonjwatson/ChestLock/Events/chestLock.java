package me.daytonjwatson.ChestLock.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import me.daytonjwatson.ChestLock.Main;
import me.daytonjwatson.ChestLock.Utils;

public class chestLock implements Listener {
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType().equals(Material.CHEST)) {
			YamlConfiguration yamlConfiguration = (Main.getInstance()).locks;
			Location location = event.getBlockPlaced().getLocation();
			Player p = event.getPlayer();
			World world = p.getWorld();
			int ax = location.getBlockX() - 1;
			int bx = location.getBlockX();
			int cx = location.getBlockX() + 1;
			int y = location.getBlockY();
			int az = location.getBlockZ() - 1;
			int bz = location.getBlockZ();
			int cz = location.getBlockZ() + 1;
			Location north = new Location(world, bx, y, az);
			Location south = new Location(world, bx, y, cz);
			Location west = new Location(world, ax, y, bz);
			Location east = new Location(world, cx, y, bz);
			String worldName = location.getWorld().getName();
			UUID playerUUID = p.getUniqueId();
			UUID checkPlayerUUID = null;
			if (!yamlConfiguration.contains("chest." + worldName + "," + bx + "," + y + "," + bz + ".owner")
					&& !yamlConfiguration.contains("chest." + worldName + "," + ax + "," + y + "," + bz + ".owner")
					&& !yamlConfiguration.contains("chest." + worldName + "," + cx + "," + y + "," + bz + ".owner")
					&& !yamlConfiguration.contains("chest." + worldName + "," + bx + "," + y + "," + az + ".owner")
					&& !yamlConfiguration.contains("chest." + worldName + "," + bx + "," + y + "," + cz + ".owner")) {
				p.sendMessage(Utils.chat("&7Locking your &6Chest&7!"));
				yamlConfiguration.set("chest." + worldName + "," + bx + "," + y + "," + bz + ".owner",
						playerUUID.toString());
				yamlConfiguration.set("chest." + worldName + "," + bx + "," + y + "," + bz + ".private",
						Boolean.valueOf(true));
				List<UUID> list = new ArrayList<>();
				yamlConfiguration.set("chest." + worldName + "," + bx + "," + y + "," + bz + ".allowedPlayers",
						list.toString());
				Main.getInstance().saveLockFile();
				return;
			}
			if (yamlConfiguration.getString("chest." + worldName + "," + bx + "," + y + "," + bz + ".owner") != null) {
				checkPlayerUUID = UUID.fromString(
						yamlConfiguration.getString("chest." + worldName + "," + bx + "," + y + "," + bz + ".owner"));
			} else if (yamlConfiguration
					.getString("chest." + worldName + "," + ax + "," + y + "," + bz + ".owner") != null) {
				checkPlayerUUID = UUID.fromString(
						yamlConfiguration.getString("chest." + worldName + "," + ax + "," + y + "," + bz + ".owner"));
			} else if (yamlConfiguration
					.getString("chest." + worldName + "," + cx + "," + y + "," + bz + ".owner") != null) {
				checkPlayerUUID = UUID.fromString(
						yamlConfiguration.getString("chest." + worldName + "," + cx + "," + y + "," + bz + ".owner"));
			} else if (yamlConfiguration
					.getString("chest." + worldName + "," + bx + "," + y + "," + az + ".owner") != null) {
				checkPlayerUUID = UUID.fromString(
						yamlConfiguration.getString("chest." + worldName + "," + bx + "," + y + "," + az + ".owner"));
			} else if (yamlConfiguration
					.getString("chest." + worldName + "," + bx + "," + y + "," + cz + ".owner") != null) {
				checkPlayerUUID = UUID.fromString(
						yamlConfiguration.getString("chest." + worldName + "," + bx + "," + y + "," + cz + ".owner"));
			}
			if (Bukkit.getPlayer(checkPlayerUUID) != Bukkit.getPlayer(playerUUID)) {
				Player checkPlayer = Bukkit.getServer().getPlayer(checkPlayerUUID);
				if (p.hasPermission("chestlock.placeagainst")) {
					p.sendMessage(
							Utils.chat("&7You placed a &6Chest &7against " + checkPlayer.getName() + "'s &6Chest&7!"));
				} else {
					p.sendMessage(Utils
							.chat("&7You cannot set a &6Chest &7agaist " + checkPlayer.getName() + "'s &6Chest&7!"));
					event.setCancelled(true);
				}
				return;
			}
			p.sendMessage(Utils.chat("&7Locking your &6Chest&7!"));
			yamlConfiguration.set("chest." + worldName + "," + bx + "," + y + "," + bz + ".owner",
					playerUUID.toString());
			yamlConfiguration.set("chest." + worldName + "," + bx + "," + y + "," + bz + ".private",
					Boolean.valueOf(true));
			List<String> allowedPlayersUUID = new ArrayList<>();
			if (north.getBlock().getType().equals(Material.CHEST)) {
				allowedPlayersUUID.addAll(yamlConfiguration
						.getStringList("chest." + worldName + "," + bx + "," + y + "," + az + ".allowedPlayers"));
			} else if (south.getBlock().getType().equals(Material.CHEST)) {
				allowedPlayersUUID.addAll(yamlConfiguration
						.getStringList("chest." + worldName + "," + bx + "," + y + "," + cz + ".allowedPlayers"));
			} else if (west.getBlock().getType().equals(Material.CHEST)) {
				allowedPlayersUUID.addAll(yamlConfiguration
						.getStringList("chest." + worldName + "," + ax + "," + y + "," + bz + ".allowedPlayers"));
			} else if (east.getBlock().getType().equals(Material.CHEST)) {
				allowedPlayersUUID.addAll(yamlConfiguration
						.getStringList("chest." + worldName + "," + cx + "," + y + "," + bz + ".allowedPlayers"));
			}
			yamlConfiguration.set("chest." + worldName + "," + bx + "," + y + "," + bz + ".allowedPlayers",
					allowedPlayersUUID);
			Main.getInstance().saveLockFile();
		}
		if (event.getBlockPlaced().getType().equals(Material.FURNACE)) {
			YamlConfiguration yamlConfiguration = (Main.getInstance()).locks;
			Location location = event.getBlockPlaced().getLocation();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			String worldName = location.getWorld().getName();
			Player p = event.getPlayer();
			UUID playerUUID = p.getUniqueId();
			p.sendMessage(Utils.chat("&7Locking your &6Furnace&7!"));
			List<String> allowedPlayersUUID = new ArrayList<>();
			yamlConfiguration.set("furnace." + worldName + "," + x + "," + y + "," + z + ".owner",
					playerUUID.toString());
			yamlConfiguration.set("furnace." + worldName + "," + x + "," + y + "," + z + ".private",
					Boolean.valueOf(true));
			yamlConfiguration.set("furnace." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers",
					allowedPlayersUUID);
			Main.getInstance().saveLockFile();
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().equals(Material.CHEST)) {
			YamlConfiguration yamlConfiguration = (Main.getInstance()).locks;
			Player p = event.getPlayer();
			UUID playerUUID = p.getUniqueId();
			Location location = event.getBlock().getLocation();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			World world = location.getWorld();
			String worldName = world.getName();
			if (yamlConfiguration.getString("chest." + worldName + "," + x + "," + y + "," + z + ".owner") == null)
				return;
			UUID checkPlayerUUID = UUID.fromString(
					yamlConfiguration.getString("chest." + worldName + "," + x + "," + y + "," + z + ".owner"));
			Player checkPlayer = Bukkit.getPlayer(checkPlayerUUID);
			if (Bukkit.getPlayer(playerUUID) != checkPlayer) {
				if (p.hasPermission("chestlock.remove")) {
					p.sendMessage(Utils.chat("&7You removed " + checkPlayer.getName() + "'s &6Chest&7!"));
				} else {
					p.sendMessage(Utils.chat("&7You cant remove " + checkPlayer.getName() + "'s &6Chest&7!"));
					event.setCancelled(true);
				}
			} else {
				yamlConfiguration.set("chest." + worldName + "," + x + "," + y + "," + z, null);
				p.sendMessage(Utils.chat("&7Locked &6Chest &7removed!"));
				Main.getInstance().saveLockFile();
			}
		}
		if (event.getBlock().getType() == Material.FURNACE) {
			YamlConfiguration yamlConfiguration = (Main.getInstance()).locks;
			Player p = event.getPlayer();
			UUID playerUUID = p.getUniqueId();
			Location location = event.getBlock().getLocation();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			World world = location.getWorld();
			String worldName = world.getName();
			if (yamlConfiguration.getString("furnace." + worldName + "," + x + "," + y + "," + z + ".owner") == null)
				return;
			UUID checkPlayerUUID = UUID.fromString(
					yamlConfiguration.getString("furnace." + worldName + "," + x + "," + y + "," + z + ".owner"));
			Player checkPlayer = Bukkit.getPlayer(checkPlayerUUID);
			if (Bukkit.getPlayer(playerUUID) != checkPlayer) {
				if (p.hasPermission("chestlock.remove")) {
					p.sendMessage(Utils.chat("&7You removed " + checkPlayer.getName() + "'s &6Furnace&7!"));
				} else {
					p.sendMessage(Utils.chat("&7You cant remove " + checkPlayer.getName() + "'s &6Furnace&7!"));
					event.setCancelled(true);
				}
			} else {
				yamlConfiguration.set("furnace." + worldName + "," + x + "," + y + "," + z, null);
				p.sendMessage(Utils.chat("&7Locked &6Furnace &7removed!"));
				Main.getInstance().saveLockFile();
			}
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player p = (Player) event.getPlayer();
		if (event.getInventory().getHolder() instanceof Chest
				|| event.getInventory().getHolder() instanceof DoubleChest) {
			YamlConfiguration yamlConfiguration = (Main.getInstance()).locks;
			InventoryHolder invHolder = event.getInventory().getHolder();
			Location location = null;
			if (event.getInventory().getHolder() instanceof Chest) {
				location = ((Chest) invHolder).getLocation();
			} else {
				location = ((DoubleChest) invHolder).getLocation();
			}
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			World world = location.getWorld();
			String worldName = world.getName();
			if (yamlConfiguration.getString("chest." + worldName + "," + x + "," + y + "," + z + ".owner") == null)
				return;
			UUID playerUUID = p.getUniqueId();
			UUID checkPlayerUUID = UUID.fromString(
					yamlConfiguration.getString("chest." + worldName + "," + x + "," + y + "," + z + ".owner"));
			Player checkPlayer = Bukkit.getPlayer(checkPlayerUUID);
			if (Bukkit.getPlayer(playerUUID) != checkPlayer) {
				if (yamlConfiguration.getBoolean("chest." + worldName + "," + x + "," + y + "," + z + ".private")) {
					List<String> allowedPlayerNames = yamlConfiguration
							.getStringList("chest." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers");
					boolean allowed = false;
					if (allowedPlayerNames.size() != 0) {
						Player offlinePlayer = Bukkit.getPlayer(p.getName());
						for (String allowedPlayerName : allowedPlayerNames) {
							UUID allowedPlayerUUID = UUID.fromString(allowedPlayerName);
							Player allowedPlayer = Bukkit.getPlayer(allowedPlayerUUID);
							if (allowedPlayer == offlinePlayer)
								allowed = true;
						}
					}
					if (allowed) {
						p.sendMessage(Utils.chat("&7You can now open " + checkPlayer.getName() + "'s &6Chest&7!"));
					} else {
						if (p.hasPermission("chestlock.open")) {
							p.sendMessage(Utils.chat("&7You opened " + checkPlayer.getName() + "'s &6Chest&7!"));
							return;
						}
						p.sendMessage(Utils.chat("&7You cant open " + checkPlayer.getName() + "'s &6Chest&7!"));
						event.setCancelled(true);
					}
				} else {
					p.sendMessage(
							Utils.chat("&7You opened a public &6Chest &7owned by " + checkPlayer.getName() + "!"));
				}
			} else if ((Main.getInstance()).makePublic.contains(p)) {
				p.sendMessage(Utils.chat("&7Making &6Chest &7public!"));
				(Main.getInstance()).makePublic.remove(p);
				yamlConfiguration.set("chest." + worldName + "," + x + "," + y + "," + z + ".private",
						Boolean.valueOf(false));
				int ax = location.getBlockX() - 1;
				int cx = location.getBlockX() + 1;
				int az = location.getBlockZ() - 1;
				int cz = location.getBlockZ() + 1;
				Location north = new Location(world, x, y, az);
				Location south = new Location(world, x, y, cz);
				Location west = new Location(world, ax, y, z);
				Location east = new Location(world, cx, y, z);
				if (north.getBlock().getType().equals(Material.CHEST)) {
					yamlConfiguration.set("chest." + worldName + "," + x + "," + y + "," + az + ".private",
							Boolean.valueOf(false));
				} else if (south.getBlock().getType().equals(Material.CHEST)) {
					yamlConfiguration.set("chest." + worldName + "," + x + "," + y + "," + cz + ".private",
							Boolean.valueOf(false));
				} else if (west.getBlock().getType().equals(Material.CHEST)) {
					yamlConfiguration.set("chest." + worldName + "," + ax + "," + y + "," + z + ".private",
							Boolean.valueOf(false));
				} else if (east.getBlock().getType().equals(Material.CHEST)) {
					yamlConfiguration.set("chest." + worldName + "," + cx + "," + y + "," + z + ".private",
							Boolean.valueOf(false));
				}
				Main.getInstance().saveLockFile();
			} else if ((Main.getInstance()).givePermission.containsKey(p)) {
				List<String> allowedPlayers = yamlConfiguration
						.getStringList("chest." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers");
				UUID targetPlayerUUID = (UUID) (Main.getInstance()).givePermission.get(p);
				(Main.getInstance()).givePermission.remove(p);
				if (allowedPlayers.contains(targetPlayerUUID.toString())) {
					p.sendMessage(Utils.chat(
							"&7" + Bukkit.getPlayer(targetPlayerUUID).getName() + " can already open this &6Chest&7!"));
				} else {
					allowedPlayers.add(targetPlayerUUID.toString());
					yamlConfiguration.set("chest." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers",
							allowedPlayers);
					Main.getInstance().saveLockFile();
					p.sendMessage(Utils.chat(
							"&7" + Bukkit.getPlayer(targetPlayerUUID).getName() + " can now open this &6Chest&7!"));
				}
			} else {
				return;
			}
		}
		if (event.getInventory().getHolder() instanceof Furnace) {
			YamlConfiguration yamlConfiguration = (Main.getInstance()).locks;
			InventoryHolder invHolder = event.getInventory().getHolder();
			Location location = ((Furnace) invHolder).getLocation();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			World world = location.getWorld();
			String worldName = world.getName();
			if (yamlConfiguration.getString("furnace." + worldName + "," + x + "," + y + "," + z + ".owner") == null)
				return;
			UUID playerUUID = p.getUniqueId();
			UUID checkPlayerUUID = UUID.fromString(
					yamlConfiguration.getString("furnace." + worldName + "," + x + "," + y + "," + z + ".owner"));
			Player checkPlayer = Bukkit.getPlayer(checkPlayerUUID);
			if (Bukkit.getPlayer(playerUUID) != checkPlayer) {
				if (yamlConfiguration.getBoolean("furnace." + worldName + "," + x + "," + y + "," + z + ".private")) {
					List<String> allowedPlayerNames = yamlConfiguration
							.getStringList("furnace." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers");
					boolean allowed = false;
					if (allowedPlayerNames.size() != 0) {
						Player offlinePlayer = Bukkit.getPlayer(p.getName());
						for (String allowedPlayerName : allowedPlayerNames) {
							UUID allowedPlayerUUID = UUID.fromString(allowedPlayerName);
							Player allowedPlayer = Bukkit.getPlayer(allowedPlayerUUID);
							if (allowedPlayer == offlinePlayer)
								allowed = true;
						}
					}
					if (allowed) {
						p.sendMessage(Utils.chat("&7You can now open " + checkPlayer.getName() + "'s &6Furnace&7!"));
					} else {
						if (p.hasPermission("chestlock.open")) {
							p.sendMessage(Utils.chat("&7You opened " + checkPlayer.getName() + "'s &6Furance&7!"));
							return;
						}
						p.sendMessage(Utils.chat("&7You cant open " + checkPlayer.getName() + "'s &6Furnace&7!"));
						event.setCancelled(true);
					}
				} else {
					p.sendMessage(
							Utils.chat("&7You opened a public &6Furnace &7owned by " + checkPlayer.getName() + "!"));
				}
			} else if ((Main.getInstance()).makePublic.contains(p)) {
				p.sendMessage(Utils.chat("&7Making &6Furnace &7public!"));
				(Main.getInstance()).makePublic.remove(p);
				yamlConfiguration.set("furnace." + worldName + "," + x + "," + y + "," + z + ".private",
						Boolean.valueOf(false));
				Main.getInstance().saveLockFile();
			} else if ((Main.getInstance()).givePermission.containsKey(p)) {
				List<String> allowedPlayers = yamlConfiguration
						.getStringList("furnace." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers");
				UUID targetPlayerUUID = (UUID) (Main.getInstance()).givePermission.get(p);
				(Main.getInstance()).givePermission.remove(p);
				if (allowedPlayers.contains(targetPlayerUUID.toString())) {
					p.sendMessage(Utils.chat("&7" + Bukkit.getPlayer(targetPlayerUUID).getName()
							+ " can already open this &6Furnace&7!"));
				} else {
					allowedPlayers.add(targetPlayerUUID.toString());
					yamlConfiguration.set("furnace." + worldName + "," + x + "," + y + "," + z + ".allowedPlayers",
							allowedPlayers);
					Main.getInstance().saveLockFile();
					p.sendMessage(Utils.chat(
							"&7" + Bukkit.getPlayer(targetPlayerUUID).getName() + " can now open this &6Furnce&7!"));
				}
			} else {
				return;
			}
		}
	}
}