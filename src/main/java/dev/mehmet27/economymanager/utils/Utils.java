package dev.mehmet27.economymanager.utils;

import co.aikar.commands.InvalidCommandArgument;
import dev.mehmet27.economymanager.EconomyManager;
import dev.mehmet27.economymanager.managers.ConfigManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	private static final ConfigManager configManager = EconomyManager.getInstance().getConfigManager();
	public static final char COLOR_CHAR = '§';
	public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
	public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-ORX]");

	public static void sendMessage(@Nullable UUID sender, String path) {
		OfflinePlayer player = EconomyManager.getInstance().getServer().getOfflinePlayer(sender);
		sendMessage(sender, path, message -> message.replace("%player%", player != null ? player.getName() : "CONSOLE"));
	}

	public static void sendMessage(@Nullable UUID sender, String playerName, String path) {
		sendMessage(sender, path, message -> message.replace("%player%", playerName));
	}

	public static void sendMessage(@Nullable UUID sender, String path, Function<String, String> placeholders) {
		String message = EconomyManager.getInstance().getConfigManager().getMessage(path, sender);

		message = placeholders.apply(message);

		Player senderPlayer = EconomyManager.getInstance().getServer().getPlayer(sender);
		if (senderPlayer != null) {
			senderPlayer.sendMessage(message);
		} else {
			EconomyManager.getInstance().getServer().getConsoleSender().sendMessage(message);
		}
	}

	public static long convertToMillis(String time) {
		Pattern YEARS = Pattern.compile("([0-9]+y)", Pattern.CASE_INSENSITIVE);
		Pattern MONTHS = Pattern.compile("([0-9]+mo)", Pattern.CASE_INSENSITIVE);
		Pattern WEEKS = Pattern.compile("([0-9]+w)", Pattern.CASE_INSENSITIVE);
		Pattern DAYS = Pattern.compile("([0-9]+d)", Pattern.CASE_INSENSITIVE);
		Pattern HOURS = Pattern.compile("([0-9]+h)", Pattern.CASE_INSENSITIVE);
		Pattern MINUTES = Pattern.compile("([0-9]+m)", Pattern.CASE_INSENSITIVE);
		Pattern SECONDS = Pattern.compile("([0-9]+s)", Pattern.CASE_INSENSITIVE);

		Matcher yearsMatcher = YEARS.matcher(time);
		Matcher monthsMatcher = MONTHS.matcher(time);
		Matcher weeksMatcher = WEEKS.matcher(time);
		Matcher daysMatcher = DAYS.matcher(time);
		Matcher hoursMatcher = HOURS.matcher(time);
		Matcher minutesMatcher = MINUTES.matcher(time);
		Matcher secondsMatcher = SECONDS.matcher(time);

		boolean foundYears = yearsMatcher.find();
		boolean foundMonths = monthsMatcher.find();
		boolean foundWeeks = weeksMatcher.find();
		boolean foundDays = daysMatcher.find();
		boolean foundHours = hoursMatcher.find();
		boolean foundMinutes = minutesMatcher.find();
		boolean foundSeconds = secondsMatcher.find();

		if (!foundYears && !foundMonths && !foundWeeks && !foundDays && !foundHours && !foundMinutes && !foundSeconds) {
			throw new InvalidCommandArgument();
		}

		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		if (foundYears) {
			years = Integer.parseInt(yearsMatcher.group(1).substring(0, yearsMatcher.group(1).length() - 1));
		}
		if (foundMonths) {
			months = Integer.parseInt(monthsMatcher.group(1).substring(0, monthsMatcher.group(1).length() - 2));
		}
		if (foundWeeks) {
			weeks = Integer.parseInt(weeksMatcher.group(1).substring(0, weeksMatcher.group(1).length() - 1));
		}
		if (foundDays) {
			days = Integer.parseInt(daysMatcher.group(1).substring(0, daysMatcher.group(1).length() - 1));
		}
		if (foundHours) {
			hours = Integer.parseInt(hoursMatcher.group(1).substring(0, hoursMatcher.group(1).length() - 1));
		}
		if (foundMinutes) {
			minutes = Integer.parseInt(minutesMatcher.group(1).substring(0, minutesMatcher.group(1).length() - 1));
		}
		if (foundSeconds) {
			seconds = Integer.parseInt(secondsMatcher.group(1).substring(0, secondsMatcher.group(1).length() - 1));
		}
		return (years * 31536000000L) + (months * 2592000000L) + (weeks * 604800000L) + (days * 86400000L) + (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L);
	}

	public static Locale stringToLocale(String loc) {
		String[] localeStr = loc.split("_");
		return new Locale(localeStr[0], localeStr[1]);
	}

	public static String color(String message) {
		return translateAlternateColorCodes('&', message);
	}

	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
				b[i] = COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	public static String stripColor(final String input) {
		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
