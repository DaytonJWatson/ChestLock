package me.daytonjwatson.ChestLock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.daytonjwatson.ChestLock.Config.Config;
import me.daytonjwatson.ChestLock.Events.chestLock;

public class Main extends JavaPlugin {
	
	public static Main instance;
	
	public File lockFile = new File(getDataFolder(), "locks.yml");
	public YamlConfiguration locks = YamlConfiguration.loadConfiguration(instance.lockFile);
	
	public HashMap<Player, UUID> givePermission = new HashMap<>();
	public List<Player> makePublic = new ArrayList();
	
	
	@Override
	public void onEnable() {
		instance = this;
		
		getLogger().info("Successfully loaded.");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Successfully unloaded.");
	}
	
	public void loadCommands() {
		
	}
	
	public void loadEvents() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new chestLock(), instance);
	}
	
	public void loadFiles() {
		Config c = new Config(instance);
		c.createConfig();
		
		if(!lockFile.exists()) {
			saveLockFile();
			getLogger().warning("locks.yml not found, creating one for you!");
		}
	}
	
	public void saveLockFile() {
		try {
			locks.save(lockFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static FileConfiguration getConfiguration() {
		return getInstance().getConfig();
	}
	
	public static Main getInstance() {
		return instance;
	}
}