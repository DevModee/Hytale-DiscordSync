package dev.modev.hydiscordsync;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

    private JDA jda;
    private final String token;

    public DiscordBot(String token) {
        this.token = token;
    }

    public void iniciar() {
        try {
            System.out.println("[DiscordSync] Conectando con Discord...");

            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("Hytale"))
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();

                    jda.awaitReady();

            System.out.println("[DiscordSync] Bot conectado con exito como: " + jda.getSelfUser().getName());
        } catch (InterruptedException e) {
            System.err.println("[DiscordSync] La conexión fue interrumpida.");
        } catch (Exception e) {
            System.err.println("[DiscordSync] Error al conectar con el bot.");
        }
    }

    public void apagar() {
        if (jda != null) {
            jda.shutdown();
            System.out.println("[DiscordSync] El bot se desconectó");
        }
    }

    public JDA getJda() {
        return jda;
    }

}
