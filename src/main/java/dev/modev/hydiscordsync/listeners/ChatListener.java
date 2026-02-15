package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.modev.hydiscordsync.HytaleDiscordSync;

public class ChatListener {

    public static void onPlayerChat(PlayerChatEvent event) {

        PlayerRef sender = event.getSender();
        String playerName = sender.getUsername();
        String message = event.getContent();

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();

        if (plugin != null && plugin.getRoleSyncManager() != null
                && plugin.getConfigData().roleSync.enabled) {
            String trimmed = message.trim();
            if (trimmed.matches("\\d{6}")) {
                boolean success = plugin.getRoleSyncManager().verifyLinkCode(playerName, trimmed);
                if (success) {
                    for (Player p : HytaleDiscordSync.connectedPlayers) {
                        if (p.getDisplayName().equalsIgnoreCase(playerName)) {
                            try {
                                p.sendMessage(Message.raw("[DiscordSync] Account linked successfully!"));
                            } catch (Exception ignored) {}
                            break;
                        }
                    }
                    plugin.getRoleSyncManager().syncPlayer(playerName);
                    return;
                }
            }
        }

        if (plugin != null && plugin.getBot() != null) {
            String channelId = plugin.getConfigData().channelId;

            if (!channelId.equals("000000000000000000")) {
                plugin.getBot().sendChatMessage(channelId, playerName, message);
            }
        }
    }
}