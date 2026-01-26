package dev.modev.hydiscordsync.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    private final Gson gson;
    private final Path configPath;

    public ConfigManager(String fileName) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configPath = Paths.get(fileName);
    }

    public BotConfig load() {
        if (!Files.exists(configPath)) {
            System.out.println("[ConfigManager] File not found, creating new one...");
            BotConfig defaultConfig = new BotConfig();
            save(defaultConfig);
            return defaultConfig;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            System.out.println("[ConfigManager] Loading Configuration...");

            BotConfig configLoaded = gson.fromJson(reader, BotConfig.class);
            boolean saveNeeded = false;

            if (configLoaded.messages == null) {
                configLoaded.messages = new BotConfig.Messages();
                saveNeeded = true;
            }

            if (configLoaded.embeds == null) {
                System.out.println("[ConfigManager] Updating config: Adding 'embeds' section...");
                configLoaded.embeds = new BotConfig.EmbedConfig();
                saveNeeded = true;
            }

            if (saveNeeded) {
                save(configLoaded);
                System.out.println("[ConfigManager] Configuration file updated automatically.");
            }

            return configLoaded;

        } catch (IOException e) {
            System.err.println("[ConfigManager] Error reading file. Using defaults.");
            e.printStackTrace();
            return new BotConfig();
        }
    }

    public void save(BotConfig config) {
        try {
            String json = gson.toJson(config);
            Files.write(configPath, json.getBytes());
        } catch (IOException e) {
            System.err.println("[ConfigManager] Could not save config file");
            e.printStackTrace();
        }
    }
}