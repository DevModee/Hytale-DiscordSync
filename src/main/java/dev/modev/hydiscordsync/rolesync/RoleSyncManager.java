package dev.modev.hydiscordsync.rolesync;

import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.config.BotConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RoleSyncManager {

    private final HytaleDiscordSync plugin;
    private final LinkStorage linkStorage;
    private final Map<String, String> playerGroups;
    private final Map<String, Long> pendingCodes;
    private ScheduledExecutorService scheduler;

    public RoleSyncManager(HytaleDiscordSync plugin) {
        this.plugin = plugin;
        this.linkStorage = new LinkStorage("discordsync_links.json");
        this.playerGroups = new ConcurrentHashMap<>();
        this.pendingCodes = new ConcurrentHashMap<>();
    }

    public void start() {
        BotConfig.RoleSyncConfig config = plugin.getConfigData().roleSync;
        if (!config.enabled) {
            System.out.println("[RoleSync] Feature is disabled.");
            return;
        }

        System.out.println("[RoleSync] Starting with " + config.roleMappings.size() + " role mappings.");

        if (config.syncInterval > 0) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(this::syncAllOnlinePlayers,
                    config.syncInterval, config.syncInterval, TimeUnit.SECONDS);
            System.out.println("[RoleSync] Periodic sync enabled every " + config.syncInterval + " seconds.");
        }
    }

    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public String generateLinkCode(String discordId) {
        pendingCodes.entrySet().removeIf(e ->
                System.currentTimeMillis() - e.getValue() > 300000);

        String existing = pendingCodes.entrySet().stream()
                .filter(e -> e.getKey().startsWith(discordId + ":"))
                .map(e -> e.getKey().split(":")[1])
                .findFirst().orElse(null);
        if (existing != null) return existing;

        String code = String.valueOf(100000 + new Random().nextInt(900000));
        pendingCodes.put(discordId + ":" + code, System.currentTimeMillis());
        return code;
    }

    public boolean verifyLinkCode(String playerName, String code) {
        String matchKey = pendingCodes.keySet().stream()
                .filter(k -> k.endsWith(":" + code))
                .findFirst().orElse(null);

        if (matchKey == null) return false;

        String discordId = matchKey.split(":")[0];
        pendingCodes.remove(matchKey);
        linkStorage.link(discordId, playerName);
        System.out.println("[RoleSync] Linked " + playerName + " to Discord ID " + discordId);
        return true;
    }

    public void syncPlayer(String playerName) {
        BotConfig.RoleSyncConfig config = plugin.getConfigData().roleSync;
        if (!config.enabled) return;

        Optional<String> discordId = linkStorage.getDiscordByPlayer(playerName);
        if (discordId.isEmpty()) {
            setPlayerGroup(playerName, config.defaultGroup);
            return;
        }

        JDA jda = plugin.getBot().getJda();
        if (jda == null) return;

        String guildId = config.guildId;
        if (guildId == null || guildId.isEmpty()) {
            List<Guild> guilds = jda.getGuilds();
            if (guilds.isEmpty()) return;
            guildId = guilds.get(0).getId();
        }

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        guild.retrieveMemberById(discordId.get()).queue(member -> {
            if (member == null) {
                setPlayerGroup(playerName, config.defaultGroup);
                return;
            }

            String bestGroup = config.defaultGroup;
            int bestPriority = -1;

            for (Role role : member.getRoles()) {
                BotConfig.RoleMappingEntry mapping = config.roleMappings.get(role.getId());
                if (mapping != null && mapping.priority > bestPriority) {
                    bestGroup = mapping.permissionGroup;
                    bestPriority = mapping.priority;
                }
            }

            setPlayerGroup(playerName, bestGroup);
            System.out.println("[RoleSync] Synced " + playerName + " -> group: " + bestGroup);
        }, error -> {
            System.err.println("[RoleSync] Failed to retrieve member for " + playerName + ": " + error.getMessage());
            setPlayerGroup(playerName, config.defaultGroup);
        });
    }

    public void syncAllOnlinePlayers() {
        for (Player player : HytaleDiscordSync.connectedPlayers) {
            try {
                syncPlayer(player.getDisplayName());
            } catch (Exception e) {
                System.err.println("[RoleSync] Error syncing " + player.getDisplayName() + ": " + e.getMessage());
            }
        }
    }

    public void setPlayerGroup(String playerName, String group) {
        playerGroups.put(playerName.toLowerCase(), group);
    }

    public String getPlayerGroup(String playerName) {
        BotConfig.RoleSyncConfig config = plugin.getConfigData().roleSync;
        return playerGroups.getOrDefault(playerName.toLowerCase(), config.defaultGroup);
    }

    public LinkStorage getLinkStorage() {
        return linkStorage;
    }

    public Map<String, String> getPlayerGroups() {
        return Collections.unmodifiableMap(playerGroups);
    }
}
