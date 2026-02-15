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

public class KickCommand implements RemoteCommand {

    @Override
    public String getName() { return "kick"; }

    @Override
    public String getDescription() { return "Kick a player from the server"; }

    @Override
    public String getUsage() { return "kick <player> [reason]"; }

    @Override
    public void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager) {
        if (args.length == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("Please specify a player: `kick <player> [reason]`");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String targetName = args[0];
        String reason = args.length > 1
                ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length))
                : "Kicked by an administrator";

        Player target = null;
        for (Player p : HytaleDiscordSync.connectedPlayers) {
            if (p.getDisplayName().equalsIgnoreCase(targetName)) {
                target = p;
                break;
            }
        }

        if (target == null) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Player Not Found")
                    .setDescription("**" + targetName + "** is not online.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        try {
            target.sendMessage(Message.raw("You have been kicked: " + reason));
            target.remove();

            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#57F287"))
                    .setTitle("Player Kicked")
                    .addField("Player", targetName, true)
                    .addField("Reason", reason, true)
                    .setFooter("By " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now());
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

            for (Player p : HytaleDiscordSync.connectedPlayers) {
                try {
                    p.sendMessage(Message.raw("[Server] " + targetName + " has been kicked: " + reason));
                } catch (Exception ignored) {}
            }

        } catch (Exception e) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Kick Failed")
                    .setDescription("Could not kick **" + targetName + "**: " + e.getMessage());
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
}
