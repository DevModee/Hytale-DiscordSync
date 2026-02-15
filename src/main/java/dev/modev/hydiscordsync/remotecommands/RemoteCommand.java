package dev.modev.hydiscordsync.remotecommands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface RemoteCommand {

    String getName();

    String getDescription();

    String getUsage();

    void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager);
}
