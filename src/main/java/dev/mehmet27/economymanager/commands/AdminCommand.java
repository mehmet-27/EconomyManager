package dev.mehmet27.economymanager.commands;

/*
@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.admin")
public class AdminCommand extends BaseCommand {

	@Dependency
	private StorageManager storageManager;

	@Dependency
	private PunishManager punishManager;

	@Dependency
	private ConfigManager configManager;

	@CommandCompletion("@players Reason")
	@Description("{@@punishmanager.admin.description}")
	@Subcommand("%admin")
	public void admin(CommandIssuer issuer) {

	}

	@Description("{@@punishmanager.admin.description}")
	@Subcommand("%admin")
	public class AdminSubCommands extends BaseCommand {

		@Subcommand("%import")
		@CommandCompletion("AdvancedBan") //TODO add vanila
		@Description("{@@punishmanager.admin.import.description}")
		public void importC(CommandIssuer issuer, String pluginName) {
			UUID issuerUuid = issuer.isPlayer() ? issuer.getUniqueId() : null;
			SupportedPlugins plugin;
			try {
				plugin = SupportedPlugins.valueOf(pluginName.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException e) {
				Utils.sendText(issuerUuid, "punishmanager.admin.import.unsupportedPlugin");
				return;
			}
			FileConfiguration importConfig = null;
			try {
				File importFile = new File(configManager.getDataFolder() + File.separator + "import.yml");
				if (!importFile.exists()) {
					configManager.createFile(importFile);
					FileConfiguration newImport = new YamlConfiguration();
					newImport.set("host", "localhost");
					newImport.set("port", 3306);
					newImport.set("database", "database");
					newImport.set("username", "username");
					newImport.set("password", "password");
					//if (plugin.equals(SupportedPlugins.LITEBANS)) newImport.set("tablePrefix", "litebans_");
					newImport.save(importFile);
					Utils.sendText(issuerUuid, "punishmanager.admin.import.fileCreated");
					return;
				}
				importConfig = new YamlConfiguration();
				importConfig.load(importFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			if (importConfig == null) {
				Utils.sendText(issuerUuid, "punishmanager.admin.import.nullConfiguration");
				return;
			}

			Utils.sendText(issuerUuid, "punishmanager.admin.import.start");
			long startTime = System.currentTimeMillis();

			if (plugin.equals(SupportedPlugins.ADVANCEDBAN)) {
				Utils.sendText(issuerUuid, "punishmanager.admin.import.connecting");
				DBCore core = new MySQLCore(importConfig.getString("host"),
						importConfig.getString("database"),
						importConfig.getInt("port"),
						importConfig.getString("username"),
						importConfig.getString("password"));
				String query = "SELECT * FROM Punishments";
				List<Punishment> punishments = new ArrayList<>();
				try (Connection connection = core.getDataSource().getConnection()) {
					ResultSet result = connection.createStatement().executeQuery(query);
					while (result.next()) {
						String playerName = result.getString("name");
						AdvancedBanPunishmentType advancedBanPunishmentType;
						Punishment.PunishType punishType;
						try {
							advancedBanPunishmentType = AdvancedBanPunishmentType.valueOf(result.getString("punishmentType"));
						} catch (IllegalArgumentException e) {
							punishManager.getLogger().warning(configManager.getMessage("punishmanager.admin.import.unsupportedPunishType")
									.replace("%type%", result.getString("punishmentType")));
							continue;
						}
						punishType = advancedBanPunishmentType.getType();
						UUID uuid;
						try {
							uuid = UUID.fromString(result.getString("uuid"));
						} catch (IllegalArgumentException e) {
							uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
						}
						String reason = result.getString("reason");
						String operator = result.getString("operator");
						String server = "ALL";
						String ip = null;
						if (punishType.equals(Punishment.PunishType.IPBAN)) {
							ip = result.getString("uuid");
						}
						long start = result.getLong("start");
						long end = result.getLong("end");
						Punishment punishment = new Punishment(playerName, uuid, ip, punishType, reason, operator, null, server, start, end, 0);
						punishments.add(punishment);
					}
					Utils.sendText(issuerUuid, "punishmanager.admin.import.found", message ->
							message.replace("%total%", String.valueOf(punishments.size())));
				} catch (SQLException e) {
					e.printStackTrace();
				}

				int imported = 0;
				for (Punishment punishment : punishments) {
					if (!punishment.isExpired()) {
						storageManager.addPunishToPunishments(punishment);
						imported++;
					}
					storageManager.addPunishToHistory(punishment);
				}
				int finalImported = imported;
				Utils.sendText(issuerUuid, "punishmanager.admin.import.imported", message ->
						message.replace("%total%", String.valueOf(finalImported)));
				core.getDataSource().close();
				long diff = System.currentTimeMillis() - startTime;
				Utils.sendText(issuerUuid, "punishmanager.admin.import.tookMs", message ->
						message.replace("%long%", String.valueOf(diff)));
				Utils.sendText(issuerUuid, "punishmanager.admin.import.end");
			}
			if (plugin.equals(SupportedPlugins.VANILLA)) {
				if (!punishManager.getMethods().getPlatform().equals(Platform.BUKKIT_SPIGOT)) {
					Utils.sendText(issuerUuid, "punishmanager.admin.import.wrong-platform");
					return;
				}
			}

            */
/*if (plugin.equals(SupportedPlugins.LITEBANS)) {
                Utils.sendText(issuerUuid, "punishmanager.admin.import.connecting");
                DBCore core = new MySQLCore(importConfig.getString("host"),
                        importConfig.getString("database"),
                        importConfig.getInt("port"),
                        importConfig.getString("username"),
                        importConfig.getString("password"));
                String tablePrefix = importConfig.getString("tablePrefix", "litebans_");
                String query = "SELECT * FROM " + tablePrefix + "mutes";
                List<Punishment> punishments = new ArrayList<>();
                try (Connection connection = core.getDataSource().getConnection()) {
                    ResultSet result = connection.createStatement().executeQuery(query);
                    while (result.next()) {
                        String playerName = result.getString("name");
                        AdvancedBanPunishmentType advancedBanPunishmentType;
                        Punishment.PunishType punishType;
                        try {
                            advancedBanPunishmentType = AdvancedBanPunishmentType.valueOf(result.getString("punishmentType"));
                        } catch (IllegalArgumentException e) {
                            punishManager.getLogger().warning(configManager.getMessage("punishmanager.admin.import.unsupportedPunishType")
                                    .replace("%type%", result.getString("punishmentType")));
                            continue;
                        }
                        punishType = advancedBanPunishmentType.getType();
                        UUID uuid;
                        try {
                            uuid = UUID.fromString(result.getString("uuid"));
                        } catch (IllegalArgumentException e) {
                            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
                        }
                        String reason = result.getString("reason");
                        String operator = result.getString("operator");
                        String server = "ALL";
                        String ip = null;
                        if (punishType.equals(Punishment.PunishType.IPBAN)) {
                            ip = result.getString("uuid");
                        }
                        long start = result.getLong("start");
                        long end = result.getLong("end");
                        Punishment punishment = new Punishment(playerName, uuid, ip, punishType, reason, operator, null, server, start, end, 0);
                        punishments.add(punishment);
                    }
                    Utils.sendText(issuerUuid, "punishmanager.admin.import.found", message ->
                            message.replace("%total%", String.valueOf(punishments.size())));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int imported = 0;
                for (Punishment punishment : punishments) {
                    if (!punishment.isExpired()) {
                        storageManager.addPunishToPunishments(punishment);
                        imported++;
                    }
                    storageManager.addPunishToHistory(punishment);
                }
                int finalImported = imported;
                Utils.sendText(issuerUuid, "punishmanager.admin.import.imported", message ->
                        message.replace("%total%", String.valueOf(finalImported)));
                core.getDataSource().close();
                long diff = System.currentTimeMillis() - startTime;
                Utils.sendText(issuerUuid, "punishmanager.admin.import.tookMs", message ->
                        message.replace("%long%", String.valueOf(diff)));
                Utils.sendText(issuerUuid, "punishmanager.admin.import.end");
            }*//*

		}

		@Subcommand("%reload")
		@Description("{@@punishmanager.admin.reload.description}")
		public void reload(CommandIssuer sender) {
			punishManager.getConfigManager().setup();
			UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
			punishManager.getMethods().getCommandManager().updateDefaultLocale();
			Utils.sendText(operatorUuid, "punishmanager.admin.reload.done");
		}
	}
}
*/
