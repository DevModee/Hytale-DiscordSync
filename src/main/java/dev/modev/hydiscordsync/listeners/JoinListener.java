package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.config.BotConfig;

public class JoinListener {

    public static void onPlayerJoin(PlayerReadyEvent event) {
        HytaleDiscordSync.playerCount++;
        HytaleDiscordSync.connectedPlayers.add(event.getPlayer());

        String name = event.getPlayer().getDisplayName();
        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();

        if (plugin != null && plugin.getBot() != null) {
            BotConfig.EmbedSettings settings = plugin.getConfigData().embeds.join;

            if (settings.enabled) {
                plugin.getBot().sendEmbed(
                        plugin.getConfigData().channelId,
                        settings.color,
                        settings.title.replace("%player%", name),
                        settings.description.replace("%player%", name)
                );
            }

            if (plugin.getRoleSyncManager() != null && plugin.getConfigData().roleSync.enabled
                    && plugin.getConfigData().roleSync.syncOnJoin) {
                plugin.getRoleSyncManager().syncPlayer(name);
            }
        }
    }

    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        HytaleDiscordSync.playerCount--;
        String quitUsername = event.getPlayerRef().getUsername();
        HytaleDiscordSync.connectedPlayers.removeIf(p -> p.getDisplayName().equals(quitUsername));

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
        if (plugin != null && plugin.getBot() != null) {
            BotConfig.EmbedSettings settings = plugin.getConfigData().embeds.leave;

            if (settings.enabled) {
                plugin.getBot().sendEmbed(
                        plugin.getConfigData().channelId,
                        settings.color,
                        settings.title.replace("%player%", quitUsername),
                        settings.description.replace("%player%", quitUsername)
                );
            }
        }
    }
}