package dev.mehmet27.economymanager.listeners;

import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.EconomyPlayer;
import dev.mehmet27.economymanager.managers.CacheManager;
import dev.mehmet27.economymanager.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

	private final EconomyManager economyManager;
	private final CacheManager cacheManager;

	public PlayerJoinListener(EconomyManager economyManager) {
		this.economyManager = economyManager;
		cacheManager = economyManager.getCacheManager();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		EconomyPlayer economyPlayer;
		try {
			economyPlayer = cacheManager.getEconomyPlayer(player.getUniqueId());
		} catch (Exception e) {
			Utils.sendMessage(player.getUniqueId(), "errors.join-error");
			e.printStackTrace();
		}
	}
}
