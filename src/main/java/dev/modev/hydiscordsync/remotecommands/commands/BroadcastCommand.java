package dev.modev.hydiscordsync.remotecommands.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.remotecommands.RemoteCommand;
import dev.modev.hydiscordsync.remotecommands.RemoteCommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;

public class BroadcastCommand implements RemoteCommand {

    @Override
    public String getName() { return "broadcast"; }

    @Override
    public String getDescription() { return "Send a message to all players"; }

    @Override
    public String getUsage() { return "broadcast <message>"; }

    @Override
    public void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager) {
        if (args.length == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("Please specify a message: `broadcast <message>`");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String message = String.join(" ", args);
        String prefix = "[Discord]";

        int sent = 0;
        for (Player player : HytaleDiscordSync.connectedPlayers) {
            try {
                player.sendMessage(Message.raw(prefix + " " + message));
                sent++;
            } catch (Exception ignored) {}
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#57F287"))
                .setTitle("Broadcast Sent")
                .setDescription("**Message:** " + message)
                .addField("Delivered to", sent + " player(s)", true)
                .setFooter("By " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
