package dev.mehmet27.economymanager.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.managers.ConfigManager;
import dev.mehmet27.economymanager.managers.StorageManager;
import dev.mehmet27.economymanager.utils.Utils;

@CommandAlias("%economymanager")
@CommandPermission("economymanager.command.admin")
public class AdminCommand extends BaseCommand {

	@Dependency
	private StorageManager storageManager;

	@Dependency
	private EconomyManager economyManager;

	@Dependency
	private ConfigManager configManager;

	@Description("{@@economymanager.admin.description}")
	@Subcommand("%admin")
	public void admin(CommandIssuer issuer) {

	}

	@Description("{@@economymanager.admin.description}")
	@Subcommand("%admin")
	public class AdminSubCommands extends BaseCommand {

		@Subcommand("%reload")
		@Description("{@@economymanager.admin.reload.description}")
		public void reload(CommandIssuer sender) {
			economyManager.getConfigManager().setup();
			Utils.sendMessage(sender.getUniqueId(), "command.economymanager.admin.reload.done");
		}
	}
}
