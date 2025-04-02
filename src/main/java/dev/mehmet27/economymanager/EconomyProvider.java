package dev.mehmet27.economymanager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EconomyProvider implements Economy {

	private final EconomyManager economyManager;
	private final FileConfiguration config;
	private final HashMap<UUID, Double> balances = new HashMap<>();

	public EconomyProvider(EconomyManager economyManager) {
		this.economyManager = economyManager;
		config = economyManager.getConfigManager().getConfig();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getName() {
		return "EconomyManager";
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public String format(double amount) {
		return "";
	}

	@Override
	public String currencyNamePlural() {
		return config.getString("currency-name-plural", "");
	}

	@Override
	public String currencyNameSingular() {
		return config.getString("currency-name-singular", "");
	}

	@Override
	public boolean hasAccount(String playerName) {
		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return false;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return false;
	}

	@Override
	public double getBalance(String playerName) {
		Player player = economyManager.getServer().getPlayer(playerName);
		if (player == null) return 0;
		return balances.getOrDefault(player.getUniqueId(), BigDecimal.ZERO.doubleValue());
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return balances.getOrDefault(player.getUniqueId(), BigDecimal.ZERO.doubleValue());
	}

	@Override
	public double getBalance(String playerName, String world) {
		return getBalance(playerName);
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}

	@Override
	public boolean has(String playerName, double amount) {
		Player player = economyManager.getServer().getPlayer(playerName);
		if (player == null) {
			return false;
		}
		return getBalance(player) >= amount;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return getBalance(player) >= amount;
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		return has(player, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		Player player = economyManager.getServer().getPlayer(playerName);
		if (player == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found!");
		}
		if (amount < 0) {
			return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Amount cannot be negative!");
		}
		if (getBalance(player) < amount) {
			return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, String.format("%s's balance is insufficient!", player.getName()));
		}
		balances.put(player.getUniqueId(), getBalance(player) - amount);
		return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Amount cannot be negative!");
		}
		if (getBalance(player) < amount) {
			return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, String.format("%s's balance is insufficient!", player.getName()));
		}
		balances.put(player.getUniqueId(), getBalance(player) - amount);
		return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return withdrawPlayer(player, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		Player player = economyManager.getServer().getPlayer(playerName);
		if (player == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found!");
		}
		if (amount < 0) {
			return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Amount cannot be negative!");
		}
		balances.put(player.getUniqueId(), getBalance(player) + amount);
		return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Amount cannot be negative!");
		}
		balances.put(player.getUniqueId(), getBalance(player) + amount);
		return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return depositPlayer(player, amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return null;
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return null;
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return null;
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return null;
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return null;
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return null;
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return null;
	}

	@Override
	public List<String> getBanks() {
		return Collections.emptyList();
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return false;
	}
}
