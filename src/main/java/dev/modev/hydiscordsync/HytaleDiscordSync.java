package dev.modev.hydiscordsync;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.config.ConfigManager;

public class HytaleDiscordSync extends JavaPlugin {

    private DiscordBot bot;
    private BotConfig config;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
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
            System.out.println("[DiscordSync] Mensage configurado: " + config.messageInicio);
        }).start();
    }

    @Override
    public void onDisable() {
        if (bot != null) {
            bot.apagar();
        }
        System.out.println("[DiscordSync] Bot apagado");
    }
}
