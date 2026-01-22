package dev.modev.hydiscordsync;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.config.ConfigManager;

public class HytaleDiscordSync extends JavaPlugin {

    private DiscordBot bot;
    private BotConfig config;
    private ConfigManager configManager;

    public HytaleDiscordSync(JavaPluginInit init) {
        super(init);
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
            System.out.println("[DiscordSync] Mensage configurado: " + config.mensajeInicio);
        }).start();
    }

    @Override
    public void shutdown() {
        if (bot != null) {
            bot.apagar();
        }
        System.out.println("[DiscordSync] Bot apagado");
    }
}
