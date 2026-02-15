package dev.modev.hydiscordsync;

import dev.modev.hydiscordsync.commands.DiscordCommandListener;
import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.listeners.DiscordChatListener;
import dev.modev.hydiscordsync.remotecommands.RemoteCommandManager;
import dev.modev.hydiscordsync.rolesync.RoleSyncListener;
import dev.modev.hydiscordsync.rolesync.RoleSyncManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class DiscordBot {

    private JDA jda;
    private final String token;
    private final BotConfig config;
    private final RoleSyncManager roleSyncManager;
    private final RemoteCommandManager remoteCommandManager;

    public DiscordBot(String token, BotConfig config, RoleSyncManager roleSyncManager, RemoteCommandManager remoteCommandManager) {
        this.token = token;
        this.config = config;
        this.roleSyncManager = roleSyncManager;
        this.remoteCommandManager = remoteCommandManager;
    }

    public void start() {
        try {
            System.out.println("[DiscordSync] Connecting to Discord...");

            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new DiscordCommandListener())
                    .addEventListeners(new DiscordChatListener());

            if (config.roleSync.enabled) {
                builder.addEventListeners(new RoleSyncListener());
            }

            if (config.remoteCommands.enabled) {
                builder.addEventListeners(remoteCommandManager);
            }

            jda = builder.build();
            jda.awaitReady();

            List<net.dv8tion.jda.api.interactions.commands.build.CommandData> slashCommands = new ArrayList<>();
            slashCommands.add(Commands.slash("status", "Check server status"));

            if (config.roleSync.enabled) {
                slashCommands.add(Commands.slash("link", "Link your Discord account to your game account"));
                slashCommands.add(Commands.slash("unlink", "Unlink your Discord account from your game account"));
                slashCommands.add(Commands.slash("syncplayer", "Sync a player's roles (admin)")
                        .addOption(OptionType.STRING, "player", "Player name to sync", true));
            }

            jda.updateCommands().addCommands(slashCommands).queue();

            System.out.println("[DiscordSync] Bot connected as: " + jda.getSelfUser().getName());
        } catch (Exception e) {
            System.err.println("[DiscordSync] Error connecting bot: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            System.out.println("[DiscordSync] Bot disconnected");
        }
    }

    public JDA getJda() {
        return jda;
    }

    public void setStatus(String text) {
        if (jda != null) {
            jda.getPresence().setActivity(Activity.playing(text));
        }
    }

    public void sendChatMessage(String channelId, String author, String message) {
        if (jda == null) return;
        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                if (!author.equalsIgnoreCase("System") && !author.equalsIgnoreCase("Sistema")) {
                    channel.sendMessage("**" + author + "**: " + message).queue();
                }
            }
        } catch (Exception e) {
            System.err.println("[DiscordBot] Error sending message: " + e.getMessage());
        }
    }

    public void sendEmbed(String channelId, String colorHex, String title, String description) {
        if (jda == null) {
            System.err.println("[DiscordBot] Error: JDA is null, cannot send embed.");
            return;
        }
        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                EmbedBuilder eb = new EmbedBuilder();

                try {
                    eb.setColor(Color.decode(colorHex));
                } catch (Exception e) {
                    eb.setColor(Color.GRAY);
                }

                if (title != null && !title.isEmpty()) eb.setTitle(title);
                if (description != null && !description.isEmpty()) eb.setDescription(description);

                channel.sendMessageEmbeds(eb.build()).queue();
            } else {
                System.err.println("[DiscordBot] Error: Channel ID " + channelId + " not found.");
            }
        } catch (Exception e) {
            System.err.println("[DiscordBot] Error sending embed: " + e.getMessage());
        }
    }

}
