package dev.modev.hydiscordsync.remotecommands.commands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.remotecommands.RemoteCommand;
import dev.modev.hydiscordsync.remotecommands.RemoteCommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.stream.Collectors;

public class ListCommand implements RemoteCommand {

    @Override
    public String getName() { return "list"; }

    @Override
    public String getDescription() { return "Show online players"; }

    @Override
    public String getUsage() { return "list"; }

    @Override
    public void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager) {
        int count = HytaleDiscordSync.playerCount;
        if (count < 0) count = 0;

        String playerList;
        if (HytaleDiscordSync.connectedPlayers.isEmpty()) {
            playerList = "*No players online*";
        } else {
            playerList = HytaleDiscordSync.connectedPlayers.stream()
                    .map(Player::getDisplayName)
                    .sorted()
                    .collect(Collectors.joining("\n• ", "• ", ""));
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#5865F2"))
                .setTitle("Online Players")
                .setDescription(playerList)
                .addField("Total", String.valueOf(count), true)
                .setFooter("Requested by " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
