package dev.modev.hydiscordsync.commands;

import com.hypixel.hytale.server.core.HytaleServer;
import dev.modev.hydiscordsync.HytaleDiscordSync;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class DiscordCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("status")) {

            int jugadores = HytaleDiscordSync.contadorJugadores;
            if (jugadores < 0) jugadores = 0;

            String maxJugadores = "100";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("📊 Estado del Servidor");
            embed.setColor(Color.GREEN);
            embed.setDescription("El servidor está **EN LÍNEA**.");

            embed.addField("Jugadores", jugadores + " / " + maxJugadores, true);

            embed.setFooter("Solicitado por " + event.getUser().getName());

            event.replyEmbeds(embed.build()).queue();
        }
    }
}
