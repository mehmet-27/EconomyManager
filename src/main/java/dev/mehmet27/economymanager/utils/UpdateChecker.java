package dev.mehmet27.economymanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.mehmet27.economymanager.EconomyManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

	private final EconomyManager economyManager;
	private final FileConfiguration config;
	private final String currentVersion;
	private String latestVersion;

	public UpdateChecker(EconomyManager economyManager) {
		this.economyManager = economyManager;
		this.config = economyManager.getConfigManager().getConfig();
		this.currentVersion = economyManager.getDescription().getVersion();
	}

	public void start() {
		if (config.getBoolean("updateChecker", true)) {
			economyManager.getServer().getScheduler().runTaskTimerAsynchronously(economyManager, this::check, 0, 72000);
		}
	}

	public void check() {
		economyManager.getLogger().info("Checking for new updates...");
		try {
			//TODO change plugin id
			URL url = new URL("https://api.spiget.org/v2/resources/0/versions/latest");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStreamReader reader = new InputStreamReader(connection.getInputStream());

			JsonElement parse = new JsonParser().parse(reader);
			if (parse.isJsonObject()) {
				latestVersion = parse.getAsJsonObject().get("name").getAsString();
			}

			if (currentVersion.equals(latestVersion)) {
				economyManager.getLogger().info("No new update found");
			} else {
				economyManager.getLogger().info("New version found: " + latestVersion);
				economyManager.getLogger().info("Download here: https://www.spigotmc.org/resources/0/");
			}

			reader.close();
		} catch (IOException ex) {
			economyManager.getLogger().warning("There was a problem searching for updates!");
		}
	}

}
