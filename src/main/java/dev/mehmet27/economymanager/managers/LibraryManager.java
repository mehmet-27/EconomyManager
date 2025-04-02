package dev.mehmet27.economymanager.managers;

import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.relocation.RelocationRule;
import io.github.slimjar.resolver.data.Dependency;
import io.github.slimjar.resolver.data.DependencyData;
import io.github.slimjar.resolver.data.Repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryManager {

	private final String pluginName;
	private final Logger logger;
	private final Path datafolder;

	public LibraryManager(String pluginName, Logger logger, Path datafolder) {
		this.pluginName = pluginName;
		this.logger = logger;
		this.datafolder = datafolder;
	}

	public boolean init() {
		try {
			DependencyData dependencyData = getDependencyData();
			logger.info(String.format("Downloading %s libraries...", dependencyData.getDependencies().size()));
			ApplicationBuilder
					.appending(pluginName)
					.downloadDirectoryPath(datafolder.resolve("libraries"))
					.dataProviderFactory((url) -> () -> dependencyData)
					.internalRepositories(getRepositories())
					.build();
			logger.info("All libraries have been downloaded successfully.");
			return true;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "The library could not be loaded.", ex);
		}
		return false;
	}

	private List<Repository> getRepositories() throws MalformedURLException {
		return Arrays.asList(
				new Repository(new URL("https://repo.aikar.co/content/groups/aikar/")),
				new Repository(new URL("https://repo1.maven.org/maven2/")),
				new Repository(new URL("https://jitpack.io/"))
		);
	}

	public DependencyData getDependencyData() throws MalformedURLException {
		ArrayList<Dependency> dependencies = new ArrayList<>();
		ArrayList<RelocationRule> relocations = new ArrayList<>();
		relocations.add(new RelocationRule(filter("com{}zaxxer{}hikari"), getPackageName() + ".libraries.hikari"));
		relocations.add(new RelocationRule(filter("org{}slf4j{}slf4j-api"), getPackageName() + ".libraries.slf4j-api"));
		relocations.add(new RelocationRule(filter("org{}yaml{}snakeyaml"), getPackageName() + ".libraries.snakeyaml"));
		relocations.add(new RelocationRule(filter("org{}h2"), getPackageName() + ".libraries.h2"));
		relocations.add(new RelocationRule(filter("org{}bstats"), getPackageName() + ".libraries.bstats"));
		relocations.add(new RelocationRule(filter("co{}aikar{}commands"), getPackageName() + ".libraries.acf-commands"));
		relocations.add(new RelocationRule(filter("co{}aikar{}locales"), getPackageName() + ".libraries.acf-locales"));
		relocations.add(new RelocationRule(filter("org{}apache{}commons{}lang3"), getPackageName() + ".libraries.commons-lang3"));
		relocations.add(new RelocationRule(filter("org{}apache{}commons{}collections4"), getPackageName() + ".libraries.commons-collections4"));
		dependencies.add(new Dependency(filter("co{}aikar"), "acf-paper", "0.5.1-SNAPSHOT", "20211221.183217-1", Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}bstats"), "bstats-base", "3.0.0", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}bstats"), "bstats-bukkit", "3.0.0", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("com{}h2database"), "h2", "1.4.200", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("com{}zaxxer"), "HikariCP", "4.0.3", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}slf4j"), "slf4j-api", "1.6.2", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("com{}h2database"), "h2", "1.4.200", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}yaml"), "snakeyaml", "1.27", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("mysql"), "mysql-connector-java", "8.0.28", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("com{}github{}cryptomorin"), "XSeries", "9.2.0", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}jetbrains"), "annotations", "23.1.0", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("commons-io"), "commons-io", "2.7", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}apache{}commons"), "commons-lang3", "3.12.0", null, Collections.emptyList()));
		dependencies.add(new Dependency(filter("org{}apache{}commons"), "commons-collections4", "4.4", null, Collections.emptyList()));

		return new DependencyData(
				Collections.emptySet(),
				getRepositories(),
				dependencies,
				relocations
		);
	}

	private static String filter(String str) {
		return str.replace("{}", ".");
	}

	public String getPackageName() {
		return "dev.mehmet27.economymanager";
	}
}