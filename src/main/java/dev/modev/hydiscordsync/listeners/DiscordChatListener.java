package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.modev.hydiscordsync.HytaleDiscordSync;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordChatListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
        if (plugin == null) return;

        String configChannelId = plugin.getConfigData().channelId;

        if (event.getChannel().getId().equals(configChannelId)) {

            String usuario = event.getAuthor().getName();
            String mensaje = event.getMessage().getContentDisplay();
            String mensajeFinal = "§9[Discord] §f" + usuario + ": " + mensaje;

            for (Player player : HytaleDiscordSync.jugadoresConectados) {
                try {
                    player.sendMessage(mensajeFinal);
                } catch (Exception e) {

                }
            }


        }
    }
}
