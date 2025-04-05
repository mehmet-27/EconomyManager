package dev.mehmet27.economymanager.managers;

import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.EconomyPlayer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheManager {

	private final EconomyManager economyManager;
	private final StorageManager storageManager;
	private final HashMap<UUID, EconomyPlayer> playerMap = new HashMap<>();

	public CacheManager(EconomyManager economyManager) {
		this.economyManager = economyManager;
		storageManager = economyManager.getStorageManager();
	}

	public EconomyPlayer getEconomyPlayer(UUID uuid) {
		if (playerMap.containsKey(uuid)) {
			return playerMap.get(uuid);
		} else {
			EconomyPlayer economyPlayer = storageManager.getEconomyPlayer(uuid);
			if (economyPlayer == null) {
				economyPlayer = new EconomyPlayer(uuid, BigDecimal.ZERO);
			}
			getPlayerMap().put(uuid, economyPlayer);
			return economyPlayer;
		}
	}

	public HashMap<UUID, EconomyPlayer> getPlayerMap() {
		return playerMap;
	}

	public void savePlayer(EconomyPlayer economyPlayer) {
		storageManager.updateEconomyPlayer(economyPlayer);
		getPlayerMap().remove(economyPlayer.getUuid());
	}

	public void saveAllPlayers() {
		economyManager.getLogger().info("Data of 250 players is being saved...");
		for (Map.Entry<UUID, EconomyPlayer> playerEntry : playerMap.entrySet()) {
			savePlayer(playerEntry.getValue());
		}
		economyManager.getLogger().info("Players data has been saved.");
	}
}
