package dev.mehmet27.economymanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.EconomyPlayer;
import dev.mehmet27.economymanager.managers.CacheManager;
import dev.mehmet27.economymanager.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

@CommandAlias("%economymanager")
@CommandPermission("economymanager.command.take")
public class TakeCommand extends BaseCommand {

	@Dependency
	private EconomyManager economyManager;

	@Dependency
	private CacheManager cacheManager;

	@CommandAlias("%take")
	@Description("{@@command.take.description}")
	public void take(CommandIssuer sender, OfflinePlayer player, BigDecimal amount) {
		EconomyPlayer economyPlayer = cacheManager.getEconomyPlayer(player.getUniqueId());
		economyPlayer.giveBalance(amount);
		Utils.sendMessage(sender.getUniqueId(), "command.take.done", message -> message
				.replace("%player%", player.getName())
				.replace("%amount%", amount.toPlainString()));
		String senderName = sender.isPlayer() ? economyManager.getServer().getPlayer(sender.getUniqueId()).getName() : "CONSOLE";
		Utils.sendMessage(player.getUniqueId(), "command.take.message", message -> message
				.replace("%sender%", senderName)
				.replace("%amount%", amount.toPlainString()));
	}
}