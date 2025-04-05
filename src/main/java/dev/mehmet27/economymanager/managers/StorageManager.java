package dev.mehmet27.economymanager.managers;

import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.EconomyPlayer;
import dev.mehmet27.economymanager.storage.DBCore;
import dev.mehmet27.economymanager.storage.H2Core;
import dev.mehmet27.economymanager.storage.MySQLCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StorageManager {

	private final EconomyManager economyManager = EconomyManager.getInstance();
	private final FileConfiguration config = economyManager.getConfigManager().getConfig();

	private DBCore core;

	public StorageManager() {
		initializeTables();
	}

	public void initializeTables() {
		if (config.getBoolean("mysql.enable")) {
			core = new MySQLCore(config.getString("mysql.host"),
					config.getString("mysql.database"),
					config.getInt("mysql.port"),
					config.getString("mysql.username"),
					config.getString("mysql.password"));
		} else {
			core = new H2Core();
		}
		if (!core.existsTable(playersTable())) {
			economyManager.getLogger().info("Creating table: " + playersTable());
			String query = String.format("CREATE TABLE IF NOT EXISTS %s (" +
					" uuid VARCHAR(72) NOT NULL," +
					" balance DECIMAL(65,30) DEFAULT 0," +
					" PRIMARY KEY (uuid))", playersTable());
			core.execute(query);
		}
	}

	public String getStorageProvider() {
		return economyManager.getConfigManager().getConfig().getBoolean("mysql.enable") ? "MySQL" : "H2";
	}

	public void addPlayer(EconomyPlayer player) {
		String query = String.format("INSERT INTO %s (uuid, balance) VALUES ('%s','%s')",
				playersTable(),
				player.getUuid().toString(),
				player.getBalance());
		core.executeUpdateAsync(query);
	}

	public EconomyPlayer getEconomyPlayer(UUID uuid) {
		String query = String.format("SELECT * FROM %s WHERE uuid = '%s'", playersTable(), uuid);
		try (Connection connection = core.getDataSource().getConnection()) {
			ResultSet result = connection.createStatement().executeQuery(query);
			if (result.next()) {
				BigDecimal balance = result.getBigDecimal("balance");
				return new EconomyPlayer(uuid, balance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateEconomyPlayer(EconomyPlayer economyPlayer) {
		String query = String.format("UPDATE %s SET `balance` = `%s` WHERE `uuid` = `%s`",
				playersTable(),
				economyPlayer.getBalance(),
				economyPlayer.getUuid());
		try {
			getCore().execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DBCore getCore() {
		return core;
	}

	private String playersTable() {
		return config.getString("mysql.table-prefix", "em_") + "players";
	}
}
