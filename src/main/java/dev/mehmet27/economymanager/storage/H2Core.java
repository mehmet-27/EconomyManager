package dev.mehmet27.economymanager.storage;

import com.zaxxer.hikari.HikariDataSource;
import dev.mehmet27.economymanager.EconomyManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2Core implements DBCore {

	private final EconomyManager economyManager = EconomyManager.getInstance();
	private final HikariDataSource dataSource = new HikariDataSource();

	public H2Core() {
		initialize();
	}

	private void initialize() {
		String pluginName = economyManager.getDescription().getName();
		dataSource.setPoolName("[" + pluginName + "]" + " Hikari");
		economyManager.getLogger().info("Loading storage provider: H2");
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setJdbcUrl("jdbc:h2:./plugins/" + pluginName + "/" + pluginName);
	}

	@Override
	public HikariDataSource getDataSource() {
		return dataSource;
	}

	@Override
	public ResultSet select(String query) {
		try (Connection connection = dataSource.getConnection()) {
			return connection.createStatement().executeQuery(query);
		} catch (SQLException e) {
			economyManager.getLogger().severe("Error: " + e.getMessage());
			economyManager.getLogger().severe("Query: " + query);
		}
		return null;
	}

	@Override
	public void execute(String query) {
		try (Connection connection = dataSource.getConnection()) {
			connection.createStatement().execute(query);
		} catch (SQLException e) {
			economyManager.getLogger().severe("Error: " + e.getMessage());
			economyManager.getLogger().severe("Query: " + query);
		}
	}

	@Override
	public void executeUpdateAsync(String query) {
		economyManager.getServer().getScheduler().runTaskAsynchronously(economyManager, () -> {
			try (Connection connection = dataSource.getConnection()) {
				connection.createStatement().executeUpdate(query);
			} catch (SQLException e) {
				economyManager.getLogger().severe("Error: " + e.getMessage());
				economyManager.getLogger().severe("Query: " + query);
			}
		});
	}

	@Override
	public Boolean existsTable(String table) {
		try (Connection connection = dataSource.getConnection()) {
			ResultSet tables = connection.getMetaData().getTables(null, null, table, null);
			return tables.next();
		} catch (SQLException e) {
			economyManager.getLogger().severe("Failed to check if table " + table + " exists: " + e.getMessage());
			return false;
		}
	}

	@Override
	public Boolean existsColumn(String table, String column) {
		try (Connection connection = dataSource.getConnection()) {
			ResultSet col = connection.getMetaData().getColumns(null, null, table, column);
			return col.next();
		} catch (Exception e) {
			economyManager.getLogger().severe("Failed to check if column " + column + " exists in table " + table + ": " + e.getMessage());
			return false;
		}
	}
}
