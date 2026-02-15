package dev.modev.hydiscordsync.rolesync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LinkStorage {

    private final Gson gson;
    private final Path storagePath;
    private final Map<String, String> discordToPlayer;
    private final Map<String, String> playerToDiscord;

    public LinkStorage(String fileName) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.storagePath = Paths.get(fileName);
        this.discordToPlayer = new ConcurrentHashMap<>();
        this.playerToDiscord = new ConcurrentHashMap<>();
        load();
    }

    private void load() {
        if (!Files.exists(storagePath)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(storagePath)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> data = gson.fromJson(reader, type);
            if (data != null) {
                discordToPlayer.putAll(data);
                data.forEach((discordId, playerName) -> playerToDiscord.put(playerName.toLowerCase(), discordId));
            }
            System.out.println("[RoleSync] Loaded " + discordToPlayer.size() + " linked accounts.");
        } catch (IOException e) {
            System.err.println("[RoleSync] Error loading link storage: " + e.getMessage());
        }
    }

    public void save() {
        try {
            String json = gson.toJson(discordToPlayer);
            Files.write(storagePath, json.getBytes());
        } catch (IOException e) {
            System.err.println("[RoleSync] Error saving link storage: " + e.getMessage());
        }
    }

    public void link(String discordId, String playerName) {
        String oldDiscord = playerToDiscord.get(playerName.toLowerCase());
        if (oldDiscord != null) {
            discordToPlayer.remove(oldDiscord);
        }
        String oldPlayer = discordToPlayer.get(discordId);
        if (oldPlayer != null) {
            playerToDiscord.remove(oldPlayer.toLowerCase());
        }
        discordToPlayer.put(discordId, playerName);
        playerToDiscord.put(playerName.toLowerCase(), discordId);
        save();
    }

    public void unlink(String discordId) {
        String playerName = discordToPlayer.remove(discordId);
        if (playerName != null) {
            playerToDiscord.remove(playerName.toLowerCase());
        }
        save();
    }

    public void unlinkPlayer(String playerName) {
        String discordId = playerToDiscord.remove(playerName.toLowerCase());
        if (discordId != null) {
            discordToPlayer.remove(discordId);
        }
        save();
    }

    public Optional<String> getPlayerByDiscord(String discordId) {
        return Optional.ofNullable(discordToPlayer.get(discordId));
    }

    public Optional<String> getDiscordByPlayer(String playerName) {
        return Optional.ofNullable(playerToDiscord.get(playerName.toLowerCase()));
    }

    public boolean isDiscordLinked(String discordId) {
        return discordToPlayer.containsKey(discordId);
    }

    public boolean isPlayerLinked(String playerName) {
        return playerToDiscord.containsKey(playerName.toLowerCase());
    }

    public Map<String, String> getAllLinks() {
        return new ConcurrentHashMap<>(discordToPlayer);
    }

    public int getLinkCount() {
        return discordToPlayer.size();
    }
}
