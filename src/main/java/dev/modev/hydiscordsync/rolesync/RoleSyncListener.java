package dev.modev.hydiscordsync.rolesync;

import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Optional;

public class RoleSyncListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("link") && !event.getName().equals("unlink") && !event.getName().equals("syncplayer")) {
            return;
        }

        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
        if (plugin == null || plugin.getRoleSyncManager() == null) return;

        BotConfig.RoleSyncConfig config = plugin.getConfigData().roleSync;
        if (!config.enabled) {
            event.reply("Role sync is currently disabled.").setEphemeral(true).queue();
            return;
        }

        switch (event.getName()) {
            case "link" -> handleLink(event, plugin);
            case "unlink" -> handleUnlink(event, plugin);
            case "syncplayer" -> handleSync(event, plugin);
        }
    }

    private void handleLink(SlashCommandInteractionEvent event, HytaleDiscordSync plugin) {
        RoleSyncManager manager = plugin.getRoleSyncManager();
        String discordId = event.getUser().getId();

        if (manager.getLinkStorage().isDiscordLinked(discordId)) {
            String linked = manager.getLinkStorage().getPlayerByDiscord(discordId).orElse("Unknown");
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Already Linked")
                    .setDescription("Your account is already linked to **" + linked + "**.\nUse `/unlink` first to unlink.");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        String code = manager.generateLinkCode(discordId);
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#5865F2"))
                .setTitle("Link Your Account")
                .setDescription("Join the Hytale server and type this code in chat:\n\n"
                        + "**Code:** `" + code + "`\n\n"
                        + "This code expires in **5 minutes**.");
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    private void handleUnlink(SlashCommandInteractionEvent event, HytaleDiscordSync plugin) {
        RoleSyncManager manager = plugin.getRoleSyncManager();
        String discordId = event.getUser().getId();

        if (!manager.getLinkStorage().isDiscordLinked(discordId)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Not Linked")
                    .setDescription("Your account is not linked to any player.");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        String playerName = manager.getLinkStorage().getPlayerByDiscord(discordId).orElse("Unknown");
        manager.getLinkStorage().unlink(discordId);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#57F287"))
                .setTitle("Account Unlinked")
                .setDescription("Your account has been unlinked from **" + playerName + "**.");
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    private void handleSync(SlashCommandInteractionEvent event, HytaleDiscordSync plugin) {
        if (event.getOption("player") == null) {
            event.reply("Please specify a player name.").setEphemeral(true).queue();
            return;
        }

        String playerName = event.getOption("player").getAsString();
        RoleSyncManager manager = plugin.getRoleSyncManager();

        if (!manager.getLinkStorage().isPlayerLinked(playerName)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Player Not Linked")
                    .setDescription("**" + playerName + "** does not have a linked Discord account.");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        manager.syncPlayer(playerName);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#57F287"))
                .setTitle("Sync Triggered")
                .setDescription("Role sync has been triggered for **" + playerName + "**.");
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        handleRoleChange(event.getMember().getId());
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        handleRoleChange(event.getMember().getId());
    }

    private void handleRoleChange(String discordId) {
        HytaleDiscordSync plugin = HytaleDiscordSync.getInstance();
        if (plugin == null || plugin.getRoleSyncManager() == null) return;

        BotConfig.RoleSyncConfig config = plugin.getConfigData().roleSync;
        if (!config.enabled) return;

        RoleSyncManager manager = plugin.getRoleSyncManager();
        Optional<String> playerName = manager.getLinkStorage().getPlayerByDiscord(discordId);
        playerName.ifPresent(manager::syncPlayer);
    }
}
