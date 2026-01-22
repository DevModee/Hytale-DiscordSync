package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.modev.hydiscordsync.HytaleDiscordSync;

public class ChatListener {

    public static void onPlayerChat(PlayerChatEvent event) {

        PlayerRef sender = event.getSender();
        String nombreJugador = sender.getUsername();
        String mensaje = event.getContent();

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();

        if (plugin != null && plugin.getBot() != null) {
            String channelId = plugin.getConfigData().channelId;
            if (!channelId.equals("000000000000000000")) {

                String formato = plugin.getConfigData().messages.gameToDiscordFormat;
                String mensajeFinal = formato
                        .replace("%player%", nombreJugador)
                        .replace("%message%", mensaje);

                plugin.getBot().enviarMensajeChat(channelId, "Sistema", mensajeFinal);
            }
        }
    }
}
