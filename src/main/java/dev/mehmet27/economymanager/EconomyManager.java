package dev.mehmet27.economymanager;

import dev.mehmet27.economymanager.managers.CommandManager;
import dev.mehmet27.economymanager.managers.ConfigManager;
import dev.mehmet27.economymanager.managers.LibraryManager;
import dev.mehmet27.economymanager.managers.StorageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyManager extends JavaPlugin {

	private static EconomyManager instance;
	private LibraryManager libraryManager;
	private ConfigManager configManager;
	private CommandManager commandManager;
	private StorageManager storageManager;

	public static final String COMMANDS_PACKAGE = "dev.mehmet27.economymanager.commands";

	private boolean isVaultEnabled = false;

	@Override
	public void onLoad() {
		LibraryManager libraryManager = new LibraryManager(getDescription().getName(), getLogger(), getDataFolder().toPath());
		if (!libraryManager.init()) {
			getLogger().severe("The libraries could not be loaded.");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onEnable() {
		instance = this;
		long startTime = System.currentTimeMillis();
		this.configManager = new ConfigManager(this);
		configManager.setup();
		this.storageManager = new StorageManager();
		this.commandManager = new CommandManager(this);
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().severe("Vault plugin not found! You should install it before!");
		} else {
			isVaultEnabled = true;
			getServer().getServicesManager().register(Economy.class, new EconomyProvider(this), this, ServicePriority.High);
			getLogger().info("Registered as an economy provider in Vault!");
		}
		long enableTime = System.currentTimeMillis() - startTime;
		getLogger().info(String.format("Successfully enabled within %sms", enableTime));
	}

	@Override
	public void onDisable() {
		getLogger().info("Shutdown process initiated...");
		if (storageManager == null) return;
		storageManager.getCore().getDataSource().close();
	}

	public static EconomyManager getInstance() {
		return instance;
	}

	public LibraryManager getLibraryManager() {
		return libraryManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public boolean isVaultEnabled() {
		return isVaultEnabled;
	}
}
