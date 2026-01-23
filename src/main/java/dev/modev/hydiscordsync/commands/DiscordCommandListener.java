package dev.modev.hydiscordsync.commands;

import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class DiscordCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("status")) {

            HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
            if (plugin == null) return;

            BotConfig.Messages msgs = plugin.getConfigData().messages;

            int players = HytaleDiscordSync.playerCount;
            if (players < 0) players = 0;
            String maxPlayers = "100";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(msgs.statusTitle);
            embed.setColor(Color.GREEN);
            embed.setDescription(msgs.statusDescription);

            embed.addField(msgs.statusFields, players + " / " + maxPlayers, true);

            String footer = msgs.statusFooter.replace("%user%", event.getUser().getName());
            embed.setFooter(footer);

            event.replyEmbeds(embed.build()).queue();
        }
    }
}
