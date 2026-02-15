package dev.modev.hydiscordsync.remotecommands.commands;

import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.remotecommands.RemoteCommand;
import dev.modev.hydiscordsync.remotecommands.RemoteCommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class HelpCommand implements RemoteCommand {

    @Override
    public String getName() { return "help"; }

    @Override
    public String getDescription() { return "Show available commands"; }

    @Override
    public String getUsage() { return "help"; }

    @Override
    public void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager) {
        BotConfig.RemoteCommandsConfig config = manager.getPlugin().getConfigData().remoteCommands;
        List<String> permitted = manager.getPermittedCommands(event.getMember(), config);
        String prefix = config.commandPrefix;

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, RemoteCommand> entry : manager.getCommands().entrySet()) {
            if (permitted.contains(entry.getKey()) || entry.getKey().equals("help")) {
                sb.append("`").append(prefix).append(entry.getValue().getUsage())
                        .append("` - ").append(entry.getValue().getDescription()).append("\n");
            }
        }

        if (sb.isEmpty()) {
            sb.append("*You don't have permission for any commands.*");
        }

        sb.append("\n`").append(prefix).append("confirm` - Confirm a pending action\n");
        sb.append("`").append(prefix).append("cancel` - Cancel a pending action");

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#5865F2"))
                .setTitle("Available Commands")
                .setDescription(sb.toString())
                .setFooter("Requested by " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
