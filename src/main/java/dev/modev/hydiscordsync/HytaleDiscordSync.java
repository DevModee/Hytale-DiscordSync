package dev.modev.hydiscordsync;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.config.ConfigManager;
import dev.modev.hydiscordsync.listeners.ChatListener;
import dev.modev.hydiscordsync.listeners.JoinListener;

public class HytaleDiscordSync extends JavaPlugin {

    private static HytaleDiscordSync instance;

    private DiscordBot bot;
    private BotConfig config;
    private ConfigManager configManager;
    private boolean activo = true;

    public static int contadorJugadores = 0;

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
            iniciarCicloEstado();
            System.out.println("[DiscordSync] Mensage configurado: " + config.mensajeInicio);
        }).start();

        System.out.println("[DiscordSync] Registrando eventos de Chat y Entrada...");

        this.getEventRegistry().registerGlobal(PlayerChatEvent.class, ChatListener::onPlayerChat);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, JoinListener::onPlayerJoin);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, JoinListener::onPlayerDisconnect);
    }

    private void iniciarCicloEstado() {
        new Thread(() -> {
            int fase = 0;
            while (activo) {
                try {
                    if (bot.getJda() != null) {
                        int online = contadorJugadores;
                        if (online < 0) online = 0;

                        if (fase == 0) {
                            bot.setEstado("Hytale | " + online + " Online");
                            fase = 1;
                        } else {
                            bot.setEstado("discord.gg/lapampa");
                            fase = 0;
                        }
                    }
                    Thread.sleep(10000); // 10 segundos
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }


    @Override
    public void shutdown() {
        activo = false;
        if (bot != null) {
            bot.apagar();
        }
        System.out.println("[DiscordSync] Bot apagado");
    }
}
