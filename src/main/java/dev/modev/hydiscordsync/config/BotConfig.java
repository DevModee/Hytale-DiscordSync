package dev.modev.hydiscordsync.config;

import java.util.*;

public class BotConfig {

    public String botToken = "BOT_TOKEN";
    public String channelId = "000000000000000000";
    public String startMessage = "The server is online!";

    public String serverName = "Hytale Server";
    public String serverIcon = "https://i.imgur.com/your_logo.png";
    public String serverBanner = "https://i.imgur.com/your_banner.png";

    public List<String> statusMessages = Arrays.asList(
            "Hytale | %online% Online",
            "discord.gg/your-invite",
            "Playing on the Server"
    );

    public int statusInterval = 10;

    public Messages messages = new Messages();
    public EmbedConfig embeds = new EmbedConfig();
    public RoleSyncConfig roleSync = new RoleSyncConfig();
    public RemoteCommandsConfig remoteCommands = new RemoteCommandsConfig();

    public static class Messages {
        public String discordToGameFormat = "[Discord] %user%: %message%";
        public String gameToDiscordFormat = "**%player%**: %message%";
        public String playerJoin = ":green_circle: **%player%** joined the server.";
        public String playerLeave = ":red_circle: **%player%** left the server.";

        public String statusTitle = "Server Status";
        public String statusDescription = "**IP:** play.yourserver.com\n**Version:** Latest";
        public String statusFields = "👥 Players";
        public String statusFooter = "Requested by %user%";
    }

    public static class EmbedConfig {
        public EmbedSettings join = new EmbedSettings(true, "#57F287", "Player Joined", "%player% has connected.");
        public EmbedSettings leave = new EmbedSettings(true, "#ED4245", "Player Left", "%player% has disconnected.");
        public EmbedSettings death = new EmbedSettings(true, "#2F3136", "Player Died", "%player% died.");
        public EmbedSettings serverStart = new EmbedSettings(true, "#5865F2", "Server Status", "The server is now **ONLINE** 🟢");
        public EmbedSettings serverStop = new EmbedSettings(true, "#ED4245", "Server Status", "The server is now **OFFLINE** 🔴");
    }

    public static class EmbedSettings {
        public boolean enabled;
        public String color;
        public String title;
        public String description;

        public EmbedSettings() {
            this.enabled = true;
            this.color = "#FFFFFF";
            this.title = "";
            this.description = "";
        }

        public EmbedSettings(boolean enabled, String color, String title, String description) {
            this.enabled = enabled;
            this.color = color;
            this.title = title;
            this.description = description;
        }
    }

    public static class RoleSyncConfig {
        public boolean enabled = false;
        public boolean syncOnJoin = true;
        public int syncInterval = 300;
        public boolean requireDiscordLink = false;
        public String guildId = "";
        public String defaultGroup = "member";
        public Map<String, RoleMappingEntry> roleMappings = new LinkedHashMap<>();

        public RoleSyncConfig() {
            RoleMappingEntry example = new RoleMappingEntry();
            example.permissionGroup = "vip";
            example.priority = 1;
            roleMappings.put("000000000000000000", example);
        }
    }

    public static class RoleMappingEntry {
        public String permissionGroup = "member";
        public int priority = 0;
    }

    public static class RemoteCommandsConfig {
        public boolean enabled = false;
        public String commandChannelId = "000000000000000000";
        public String commandPrefix = "!";
        public Map<String, List<String>> authorizedRoles = new LinkedHashMap<>();
        public RateLimitConfig rateLimit = new RateLimitConfig();

        public RemoteCommandsConfig() {
            authorizedRoles.put("000000000000000000",
                    Arrays.asList("list", "whitelist", "kick", "broadcast", "stop"));
        }
    }

    public static class RateLimitConfig {
        public boolean enabled = true;
        public int maxCommandsPerMinute = 10;
        public boolean perUser = true;
    }
}