package dev.modev.hydiscordsync;

import dev.modev.hydiscordsync.commands.DiscordCommandListener;
import dev.modev.hydiscordsync.listeners.DiscordChatListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.Color;

public class DiscordBot {

    private JDA jda;
    private final String token;

    public DiscordBot(String token) {
        this.token = token;
    }

    public void start() {
        try {
            System.out.println("[DiscordSync] Connecting to Discord...");

            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new DiscordCommandListener())
                    .addEventListeners(new DiscordChatListener())
                    .build();

                    jda.awaitReady();

                    jda.updateCommands().addCommands(
                            Commands.slash("status", "Check server status")
                    ).queue();

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

    public void sendChatMessage(String channelId, String player, String message) {
        if (jda == null) return;

        try {
            TextChannel canal = jda.getTextChannelById(channelId);
            if (canal != null) {
                if (player.equals("System")) {
                    canal.sendMessage(message).queue();
                } else {
                    canal.sendMessage("**" + player + "**: " + message).queue();
                }
            }
        } catch (Exception e) {
            System.err.println("[DiscordBot] Error sending message: " + e.getMessage());
        }
    }

    public void sendEmbed(String channelId, String colorHex, String title, String description) {
        if (jda == null) {
            try {
                TextChannel channel = jda.getTextChannelById(channelId);
                if (channel != null) {
                    EmbedBuilder eb = new EmbedBuilder();

                    try {
                        eb.setColor(Color.decode(colorHex));
                    } catch (Exception e) {
                        eb.setColor(Color.WHITE);
                    }

                    if (title != null && !title.isEmpty()) eb.setTitle(title);
                    if (description != null && !description.isEmpty()) eb.setDescription(description);

                    channel.sendMessageEmbeds(eb.build()).queue();
                }
            } catch (Exception e) {
                System.err.println("[DiscordBot] Error sending embed: " + e.getMessage());
            }
        }
    }

}
