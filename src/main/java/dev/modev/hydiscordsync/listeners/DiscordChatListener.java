package dev.modev.hydiscordsync.listeners;

import com.hypixel.hytale.server.core.Message;
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
        String eventChannelId = event.getChannel().getId();

        if (event.getChannel().getId().equals(configChannelId)) {
            String user = event.getAuthor().getName();
            String message = event.getMessage().getContentDisplay();

            String format = plugin.getConfigData().messages.discordToGameFormat;

            String finalText = format
                    .replace("%user%", user)
                    .replace("%message%", message);

            for (Player player : HytaleDiscordSync.connectedPlayers) {
                try {
                    player.sendMessage(Message.raw(finalText));
                } catch (Exception e) {
                }
            }


        }
    }
}
