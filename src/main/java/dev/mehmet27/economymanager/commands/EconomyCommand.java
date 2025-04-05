package dev.mehmet27.economymanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.managers.CacheManager;
import dev.mehmet27.economymanager.utils.Utils;

import java.util.UUID;

@CommandAlias("%economymanager")
@Description("The main command of the plugin.")
public class EconomyCommand extends BaseCommand {

	@Dependency
	private EconomyManager economyManager;

	@Dependency
	private CacheManager cacheManager;


	@Default
	@Description("{@@economymanager.description}")
	public void economy(CommandIssuer sender) {
		//Main GUI
		if (!sender.isPlayer()) {
			return;
		}
		Object player = economyManager.getServer().getPlayer(sender.getUniqueId());
		//TODO economyManager.getMethods().openMainInventory(player);
	}

	@Subcommand("%about")
	public void about(CommandIssuer sender) {
		UUID uuid = sender.isPlayer() ? sender.getUniqueId() : null;
		Utils.sendMessage(uuid, "&a&m+                                                        +");
		Utils.sendMessage(uuid, String.format("&6&l%s", EconomyManager.getInstance().getDescription().getName()));
		Utils.sendMessage(uuid, "&eThe best economy plugin for your server.");
		Utils.sendMessage(uuid, "&e");
		Utils.sendMessage(uuid, "&eDeveloper: &bMehmet27");
		Utils.sendMessage(uuid, "&eVersion: &b" + EconomyManager.getInstance().getDescription().getVersion());
		Utils.sendMessage(uuid, "&eStorage Provider: &b" + EconomyManager.getInstance().getStorageManager().getStorageProvider());
		Utils.sendMessage(uuid, "&a&m+                                                        +");
		Utils.sendMessage(uuid, "&eUse &a/economy help &efor help.");
	}

	@Subcommand("%help")
	@Description("{@@help.description}")
	public void help(CommandIssuer sender, CommandHelp help) {
		help.showHelp();
	}
}
