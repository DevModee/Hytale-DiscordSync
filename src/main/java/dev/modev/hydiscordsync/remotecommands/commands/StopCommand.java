package dev.modev.hydiscordsync.remotecommands.commands;

import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.remotecommands.RemoteCommand;
import dev.modev.hydiscordsync.remotecommands.RemoteCommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class StopCommand implements RemoteCommand {

    @Override
    public String getName() { return "stop"; }

    @Override
    public String getDescription() { return "Stop the server (requires confirmation)"; }

    @Override
    public String getUsage() { return "stop"; }

    @Override
    public void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager) {
        manager.requestConfirmation(
                event.getAuthor().getId(),
                "stop",
                () -> {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.decode("#ED4245"))
                            .setTitle("Server Stopping")
                            .setDescription("The server is shutting down...");
                    event.getChannel().sendMessageEmbeds(eb.build()).queue();

                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {}

                        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
                        if (plugin != null) {
                            plugin.shutdown();
                        }
                    }).start();
                },
                event,
                30
        );
    }
}
