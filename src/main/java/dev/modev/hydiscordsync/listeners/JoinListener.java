package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.modev.hydiscordsync.HytaleDiscordSync;

public class JoinListener {

    public static void onPlayerJoin(PlayerReadyEvent event) {
        System.out.println("[DiscordSync-Debug] Un Jugador entró al servidor.");

        try {
            String nombre = event.getPlayer().getDisplayName();
            HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();

            if (plugin != null && plugin.getBot() != null) {
                String channelId = plugin.getConfigData().channelId;
                if (!channelId.equals("000000000000000000")) {
                    plugin.getBot().enviarMensajeChat(channelId, "Sistema", ":green_circle: **" + nombre + "** entró al servidor.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
