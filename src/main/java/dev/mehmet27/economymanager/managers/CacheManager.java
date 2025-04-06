package dev.mehmet27.economymanager.managers;

import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.EconomyPlayer;

import java.math.BigDecimal;
import java.util.HashMap;
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
		economyManager.getServer().getScheduler().runTaskAsynchronously(economyManager, () -> {
			storageManager.updateEconomyPlayer(economyPlayer);
			getPlayerMap().remove(economyPlayer.getUuid());
		});
	}

	public void saveAllPlayers() {
		economyManager.debug(String.format("Data of %s players is being saved...", getPlayerMap().size()));
		storageManager.updatePlayerMap(getPlayerMap());
		economyManager.debug("Players data has been saved.");
	}
}
