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

    public static List<Player> jugadoresConectados = new CopyOnWriteArrayList<>();

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
        System.out.println("[DiscordSync] Iniciando...");

        configManager = new ConfigManager("discordsync_config.json");

        config = configManager.cargar();

        if (config.botToken.contains("CAMBIA_ESTO") || config.botToken.isEmpty()) {
            System.err.println("[DiscordSync] ALERTA: Debes configurar el Token en discordsync_config.json");
            System.err.println("[DiscordSync] El plugin se apagará.");
            return;
        }

        bot = new DiscordBot(config.botToken);

        new Thread(() -> {
            bot.iniciar();
            startStatusCycle();
            System.out.println("[DiscordSync] Mensage configurado: " + config.mensajeInicio);
        }).start();

        System.out.println("[DiscordSync] Registrando eventos de Chat y Entrada...");

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

                        bot.setEstado(statusText);

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
        if (bot != null) {
            bot.apagar();
        }
        System.out.println("[DiscordSync] Bot apagado");
    }
}
