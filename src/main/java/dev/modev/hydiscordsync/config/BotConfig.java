package dev.modev.hydiscordsync.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotConfig {

    public String botToken = "BOT_TOKEN";
    public String channelId = "000000000000000000";
    public String mensajeInicio = "The server is online!";

    public List<String> statusMessages = Arrays.asList(
            "Hytale | %online% Online",
            "discord.gg/your-invite",
            "Playing on the Server"
    );

    public int statusInterval = 10;

    public Messages messages = new Messages();

    public static class Messages {
        public String discordToGameFormat = "§9[Discord] §f%user%: %message%";

        public String gameToDiscordFormat = "**%player%**: %message%";
        public String playerJoin = ":green_circle: **%player%** joined the server.";
        public String playerLeave = ":red_circle: **%player%** left the server.";

        public String statusTitle = "📊 Server Status";
        public String statusDescription = "The server is **ONLINE**.";
        public String statusFields = "Players";
        public String statusFooter = "Requested by %user%";
    }
}

