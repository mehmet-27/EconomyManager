package dev.mehmet27.economymanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.EconomyPlayer;
import dev.mehmet27.economymanager.managers.CacheManager;
import dev.mehmet27.economymanager.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

@CommandAlias("%economymanager")
@CommandPermission("economymanager.command.reset")
public class ResetCommand extends BaseCommand {

	@Dependency
	private EconomyManager economyManager;

	@Dependency
	private CacheManager cacheManager;

	@CommandAlias("%reset")
	@Description("{@@command.reset.description}")
	public void reset(CommandIssuer sender, OfflinePlayer player) {
		EconomyPlayer economyPlayer = cacheManager.getEconomyPlayer(player.getUniqueId());
		economyPlayer.setBalance(BigDecimal.ZERO);
		Utils.sendMessage(sender.getUniqueId(), "command.reset.done", message -> message
				.replace("%player%", player.getName()));
		String senderName = sender.isPlayer() ? economyManager.getServer().getPlayer(sender.getUniqueId()).getName() : "CONSOLE";
		Utils.sendMessage(player.getUniqueId(), "command.reset.message", message -> message
				.replace("%sender%", senderName));
	}
}