package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.modev.hydiscordsync.HytaleDiscordSync;

public class JoinListener {

    public static void onPlayerJoin(PlayerReadyEvent event) {
        HytaleDiscordSync.contadorJugadores++;
        String nombre = event.getPlayer().getDisplayName();
        enviarAlerta(":green_circle: **" + nombre + "** entró al servidor.");

    }

    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        HytaleDiscordSync.contadorJugadores--;
        String nombre = event.getPlayerRef().getUsername();
        enviarAlerta(":red_circle: **" + nombre + "** salió del servidor.");
    }

    private static void enviarAlerta(String mensaje) {
        try {
            HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
            if (plugin != null && plugin.getBot() != null) {
                String channelId = plugin.getConfigData().channelId;
                if (!channelId.equals("000000000000000000")) {
                    plugin.getBot().enviarMensajeChat(channelId, "Sistema", mensaje);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
