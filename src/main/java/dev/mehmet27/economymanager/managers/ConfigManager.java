package dev.mehmet27.economymanager.managers;

import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.utils.FileUtils;
import dev.mehmet27.economymanager.utils.Utils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfigManager {
	private final EconomyManager plugin;
	private FileConfiguration config;
	private FileConfiguration defaultMessages;
	private final Map<Locale, FileConfiguration> locales = new HashMap<>();
	private Locale defaultLocale;
	private Path dataFolder;

	public ConfigManager(EconomyManager plugin) {
		this.plugin = plugin;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public Map<Locale, Object> getLocales() {
		Map<Locale, Object> locales = new HashMap<>();
		for (File file : getLocaleFiles()) {
			Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);

			try {
				FileConfiguration fileConfiguration = new YamlConfiguration();
				fileConfiguration.load(file);
				locales.put(locale, fileConfiguration);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		plugin.getLogger().info("Found " + locales.size() + " language files.");
		return locales;
	}

	public List<Locale> getAvailableLocales() {
		List<Locale> locales = new ArrayList<>();
		for (Map.Entry<Locale, FileConfiguration> locale : this.locales.entrySet()) {
			locales.add(locale.getKey());
		}
		return locales;
	}

	public List<File> getLocaleFiles() {
		List<File> files = new ArrayList<>();
		File directoryPath = new File(dataFolder + File.separator + "locales");
		FilenameFilter ymlFilter = (dir, name) -> {
			String lowercaseName = name.toLowerCase();
			return lowercaseName.endsWith(".yml");
		};
		File[] filesList = directoryPath.listFiles(ymlFilter);
		Objects.requireNonNull(filesList, "Locales folder not found!");
		Collections.addAll(files, filesList);
		return files;
	}

	public String getMessage(String path) {
		String message = null;
		if (locales.containsKey(defaultLocale)) {
			message = locales.get(defaultLocale).getString(path);
		}
		if (message == null) {
			message = defaultMessages.getString(path);
		}
		if (message == null) {
			message = path;
		}
		return Utils.color(message);
	}

	public String getMessage(String path, UUID uuid) {
		Locale locale = Locale.forLanguageTag(EconomyManager.getInstance().getServer().getPlayer(uuid).getLocale());
		String message;
		String prefix;
		if (locales.containsKey(locale)) {
			message = locales.get(locale).getString(path);
			if (message == null) {
				message = getMessage(path);
			}
		} else {
			message = getMessage(path);
		}
		if (message == null) {
			message = path;
		}
		prefix = locales.get(locale).getString("main.prefix", locales.get(defaultLocale).getString("main.prefix"));
		return Utils.color(message.replace("%prefix%", prefix));
	}

	public String getMessage(String path, UUID uuid, Function<String, String> placeholders) {
		String message = getMessage(path, uuid);
		return Utils.color(placeholders.apply(message));
	}

	public List<String> getStringList(String path) {
		if (locales.containsKey(defaultLocale)) {
			List<String> stringList = locales.get(defaultLocale).getStringList(path);
			if (!stringList.isEmpty()) {
				return stringList.stream().map(Utils::color).collect(Collectors.toList());
			}
		}
		plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
		return new ArrayList<>();
	}

	public List<String> getStringList(String path, UUID uuid) {
		Locale locale = Locale.forLanguageTag(EconomyManager.getInstance().getServer().getPlayer(uuid).getLocale());
		List<String> stringList;
		if (locales.containsKey(locale)) {
			stringList = locales.get(locale).getStringList(path);
		} else {
			stringList = getStringList(path);
		}
		return stringList.stream().map(Utils::color).collect(Collectors.toList());
	}

	public String fileToString(File file, String charset) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			buf.write((byte) result);
			result = bis.read();
		}
		return buf.toString(charset);
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setup() {
		dataFolder = plugin.getDataFolder().toPath();
		copyFileFromResources(new File("config.yml"), Paths.get(dataFolder + File.separator + "config.yml"));
		try {
			config = new YamlConfiguration();
			config.load(new File(dataFolder + File.separator + "config.yml"));
			InputStream stream = EconomyManager.getInstance().getResource("locales/en_US.yml");
			defaultMessages = new YamlConfiguration();
			defaultMessages.loadFromString(inputStreamToString(stream));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		// Copies all locale files.
		copyFilesFromFolder("locales");
		for (Map.Entry<Locale, Object> entry : getLocales().entrySet()) {
			locales.put(entry.getKey(), (FileConfiguration) entry.getValue());
		}
		defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language", "en_US"));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void copyFileFromResources(File file, Path target) {
		if (!target.toFile().getParentFile().exists()) {
			target.toFile().getParentFile().mkdirs();
		}
		if (!target.toFile().exists() && !target.toFile().isDirectory()) {
			try {
				InputStream inputStream = EconomyManager.getInstance().getResource(file.toString());
				Files.copy(inputStream, target);
			} catch (IOException e) {
				plugin.getLogger().severe(String.format("Error while trying to load file %s.", file.getName()));
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void copyFilesFromFolder(String folder) {
		Predicate<? super Path> filter = entry -> {
			String path = entry.getFileName().toString();
			if (folder.equals("locales")) {
				return path.endsWith(".yml");
			}
			if (folder.equals("embeds")) {
				return path.endsWith(".json");
			}
			return false;
		};
		FileUtils.getFilesIn(folder, filter).forEach(file -> {
			File destination = new File(plugin.getConfigManager().getDataFolder() + File.separator + folder + File.separator + file.getName());
			if (!destination.getParentFile().exists()) {
				destination.getParentFile().mkdirs();
			}
			if (!destination.exists() && !destination.isDirectory()) {
				try {
					String fileString = folder + "/" + file.getName();
					InputStream inputStream = EconomyManager.getInstance().getResource(fileString);
					//TODO EconomyManager.getInstance().debug("File copy operation. \nInputStream: " + inputStream + "\nDestination Path: " + destination);
					Files.copy(inputStream, destination.toPath());
				} catch (IOException e) {
					plugin.getLogger().severe(String.format("Error while trying to load file %s.", file.getName()));
					throw new RuntimeException(e);
				}
			}
		});
	}

	public Path getDataFolder() {
		return dataFolder;
	}

	public void createFile(File file) {
		try {
			if (file.createNewFile()) {
				plugin.getLogger().info("File created: " + file.getName());
			} else {
				plugin.getLogger().info("File already exists.");
			}
		} catch (IOException e) {
			plugin.getLogger().severe("An error occurred while creating the file: " + file.getName());
			e.printStackTrace();
		}
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	private String inputStreamToString(InputStream stream) {
		Reader reader = null;
		BufferedReader bufferedReader = null;
		try {
			reader = new InputStreamReader(stream);
			bufferedReader = new BufferedReader(reader);
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
