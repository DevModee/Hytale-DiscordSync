package dev.modev.hydiscordsync;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.config.ConfigManager;
import dev.modev.hydiscordsync.listeners.ChatListener;
import dev.modev.hydiscordsync.listeners.JoinListener;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class HytaleDiscordSync extends JavaPlugin {

    private static HytaleDiscordSync instance;

    private DiscordBot bot;
    private BotConfig config;
    private ConfigManager configManager;
    private boolean active = true;

    public static int playerCount = 0;

    public static List<Player> connectedPlayers = new CopyOnWriteArrayList<>();

    public HytaleDiscordSync(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static HytaleDiscordSync getInstance() {
        return instance;
    }

    public DiscordBot getBot() {
        return bot;
    }
    public BotConfig getConfigData() {
        return config;
    }

    @Override
    public void setup() {
        System.out.println("[DiscordSync] Starting...");

        configManager = new ConfigManager("discordsync_config.json");
        config = configManager.load();

        if (config.botToken.contains("BOT_TOKEN") || config.botToken.isEmpty()) {
            System.err.println("[DiscordSync] ALERTA: Please configure the token.");
            return;
        }

        bot = new DiscordBot(config.botToken);

        new Thread(() -> {
            bot.start();

            if (bot.getJda() != null && config.embeds != null && config.embeds.serverStart.enabled) {
                System.out.println("[DiscordSync] Sending Start Embed...");
                bot.sendEmbed(
                        config.channelId,
                        config.embeds.serverStart.color,
                        config.embeds.serverStart.title,
                        config.embeds.serverStart.description
                );
            } else {
                System.out.println("[DiscordSync] Start Embed skipped (disabled or bot null).");
            }

            startStatusCycle();
        }).start();

        System.out.println("[DiscordSync] Registering events...");

        this.getEventRegistry().registerGlobal(PlayerChatEvent.class, ChatListener::onPlayerChat);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, JoinListener::onPlayerJoin);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, JoinListener::onPlayerDisconnect);
    }

    private void startStatusCycle() {
        new Thread(() -> {
            int index = 0;
            while (active) {
                try {
                    if (bot.getJda() != null && config.statusMessages != null && !config.statusMessages.isEmpty()) {

                        int online = playerCount;
                        if (online < 0) online = 0;

                        if(index >= config.statusMessages.size()) {
                            index = 0;
                        }

                        String statusText = config.statusMessages.get(index);
                        statusText = statusText.replace("%online%", String.valueOf(online));

                        bot.setStatus(statusText);

                        index++;
                    }
                    long sleepTime = config.statusInterval > 0 ? config.statusInterval * 1000L : 5000L;
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("[DiscordSync] Error in status cycle: " + e.getMessage());
                }
            }
        }).start();
    }


    @Override
    public void shutdown() {
        active = false;

        if (bot != null && config != null && config.embeds != null) {
            if (config.embeds.serverStop.enabled) {
                System.out.println("[DiscordSync] Sending Stop Embed...");
                bot.sendEmbed(
                        config.channelId,
                        config.embeds.serverStop.color,
                        config.embeds.serverStop.title,
                        config.embeds.serverStop.description
                );
            }

            try { Thread.sleep(2000); } catch (Exception e) {}

            bot.shutdown();
        }
        System.out.println("[DiscordSync] Plugin Stopped.");
    }
}
