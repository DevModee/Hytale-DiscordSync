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

    public BotConfig cargar() {
        if (!Files.exists(configPath)) {
            System.out.println("[ConfigManager] Archivo no encontrado, creando uno nuevo...");
            BotConfig defaultConfig = new BotConfig();
            guardar(defaultConfig);
            return defaultConfig;
        }

        try (Reader reader = Files newBuffedReader(configPath) {
            System.out.println("[ConfigManager] Cargando configuración...");
            return gson.fromJson(reader, BotConfig.class);
        } catch (IOException e) {
            System.err.println("[ConfigManager] Error al leer el archivo. Usando valores por defecto.");
            e.printStackTrace();
            return new BotConfig();
        }
    }

    public void guardar(BotConfig config) {
        try {
            String json = gson.toJson(config);
            Files.write(configPath, json.getBytes());
        } catch (IOException e) {
            System.err.println("[ConfigManager] No se pudo guardar el archivo de configuración");
            e.printStackTrace();
        }
    }



}
