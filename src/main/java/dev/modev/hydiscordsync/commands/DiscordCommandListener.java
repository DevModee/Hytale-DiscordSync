package dev.modev.hydiscordsync.commands;

import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.time.Instant;

public class DiscordCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("status")) {

            HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
            if (plugin == null) return;

            BotConfig config = plugin.getConfigData();
            BotConfig.Messages msgs = config.messages;

            int players = HytaleDiscordSync.playerCount;
            if (players < 0) players = 0;
            String maxPlayers = "100";

            EmbedBuilder embed = new EmbedBuilder();

            embed.setColor(Color.decode("#57F287"));

            String iconUrl = (config.serverIcon != null && config.serverIcon.startsWith("http")) ? config.serverIcon : null;
            embed.setAuthor(config.serverName, null, iconUrl);
            embed.setTitle(msgs.statusTitle);
            embed.setDescription(msgs.statusDescription);

            if (iconUrl != null) {
                embed.setThumbnail(iconUrl);
            }

            embed.addField("🟢 Status", "Online", true);
            embed.addField(msgs.statusFields, "**" + players + "** / " + maxPlayers, true);
            embed.addField("📡 Region", "LATAM", true);

            if (config.serverBanner != null && config.serverBanner.startsWith("http")) {
                embed.setImage(config.serverBanner);
            }

            String footerText = msgs.statusFooter.replace("%user%", event.getUser().getName());
            embed.setFooter(footerText, event.getUser().getEffectiveAvatarUrl());
            embed.setTimestamp(Instant.now());

            event.replyEmbeds(embed.build()).queue();
        }
    }
}