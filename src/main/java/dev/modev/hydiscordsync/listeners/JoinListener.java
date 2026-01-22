package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.modev.hydiscordsync.HytaleDiscordSync;

public class JoinListener {

    public static void onPlayerJoin(PlayerReadyEvent event) {
        HytaleDiscordSync.contadorJugadores++;
        HytaleDiscordSync.jugadoresConectados.add(event.getPlayer());

        String nombre = event.getPlayer().getDisplayName();

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
        if (plugin != null) {
            String msg = plugin.getConfigData().messages.playerJoin
                    .replace("%player%", nombre);
            enviarAlerta(msg);
        }
    }

    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        HytaleDiscordSync.contadorJugadores--;
        String nombreSalida = event.getPlayerRef().getUsername();

        HytaleDiscordSync.jugadoresConectados.removeIf(p -> p.getDisplayName().equals(nombreSalida));

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
        if (plugin != null) {
            String msg = plugin.getConfigData().messages.playerLeave
                    .replace("%player%", nombreSalida);
            enviarAlerta(msg);
        }
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
