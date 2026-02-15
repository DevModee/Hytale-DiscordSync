package dev.modev.hydiscordsync.remotecommands;

import dev.modev.hydiscordsync.HytaleDiscordSync;
import dev.modev.hydiscordsync.config.BotConfig;
import dev.modev.hydiscordsync.remotecommands.commands.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteCommandManager extends ListenerAdapter {

    private final HytaleDiscordSync plugin;
    private final Map<String, RemoteCommand> commands;
    private final Map<String, PendingConfirmation> pendingConfirmations;
    private RateLimiter rateLimiter;
    private AuditLogger auditLogger;

    public RemoteCommandManager(HytaleDiscordSync plugin) {
        this.plugin = plugin;
        this.commands = new LinkedHashMap<>();
        this.pendingConfirmations = new ConcurrentHashMap<>();
    }

    public void start() {
        BotConfig.RemoteCommandsConfig config = plugin.getConfigData().remoteCommands;
        if (!config.enabled) {
            System.out.println("[RemoteCommands] Feature is disabled.");
            return;
        }

        if (config.rateLimit.enabled) {
            this.rateLimiter = new RateLimiter(config.rateLimit.maxCommandsPerMinute, config.rateLimit.perUser);
        }
        this.auditLogger = new AuditLogger("discordsync_audit.log");

        registerCommand(new ListCommand());
        registerCommand(new WhitelistCommand());
        registerCommand(new KickCommand());
        registerCommand(new BroadcastCommand());
        registerCommand(new StopCommand());
        registerCommand(new HelpCommand());

        System.out.println("[RemoteCommands] Started with " + commands.size() + " commands registered.");
    }

    public void registerCommand(RemoteCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public Map<String, RemoteCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

    public HytaleDiscordSync getPlugin() {
        return plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        BotConfig.RemoteCommandsConfig config = plugin.getConfigData().remoteCommands;
        if (!config.enabled) return;

        String channelId = config.commandChannelId;
        if (!event.getChannel().getId().equals(channelId)) return;

        String message = event.getMessage().getContentRaw().trim();
        String prefix = config.commandPrefix;

        if (!message.startsWith(prefix)) return;

        String content = message.substring(prefix.length()).trim();
        if (content.isEmpty()) return;

        String[] parts = content.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        if (commandName.equals("confirm")) {
            handleConfirmation(event);
            return;
        }

        if (commandName.equals("cancel")) {
            handleCancel(event);
            return;
        }

        RemoteCommand command = commands.get(commandName);
        if (command == null) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Unknown Command")
                    .setDescription("Command `" + commandName + "` not found.\nUse `" + prefix + "help` to see available commands.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        if (!hasPermission(event.getMember(), commandName, config)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Insufficient Permissions")
                    .setDescription("You do not have permission to use this command.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            if (auditLogger != null) {
                auditLogger.log(event.getAuthor().getId(), event.getAuthor().getName(),
                        commandName, String.join(" ", args), "DENIED - No permission");
            }
            return;
        }

        if (rateLimiter != null && config.rateLimit.enabled) {
            if (rateLimiter.isRateLimited(event.getAuthor().getId())) {
                int remaining = rateLimiter.getRemainingCooldown(event.getAuthor().getId());
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.decode("#FEE75C"))
                        .setTitle("Rate Limited")
                        .setDescription("Please wait **" + remaining + "s** before using more commands.");
                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                return;
            }
        }

        try {
            command.execute(event, args, this);
            if (auditLogger != null) {
                auditLogger.log(event.getAuthor().getId(), event.getAuthor().getName(),
                        commandName, String.join(" ", args), "SUCCESS");
            }
        } catch (Exception e) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setTitle("Command Error")
                    .setDescription("An error occurred while executing the command:\n`" + e.getMessage() + "`");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            if (auditLogger != null) {
                auditLogger.log(event.getAuthor().getId(), event.getAuthor().getName(),
                        commandName, String.join(" ", args), "ERROR - " + e.getMessage());
            }
        }
    }

    private boolean hasPermission(Member member, String commandName, BotConfig.RemoteCommandsConfig config) {
        if (member == null) return false;

        for (Role role : member.getRoles()) {
            List<String> allowedCommands = config.authorizedRoles.get(role.getId());
            if (allowedCommands != null) {
                if (allowedCommands.contains("*") || allowedCommands.contains(commandName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void requestConfirmation(String userId, String commandName, Runnable action, MessageReceivedEvent event, int timeoutSeconds) {
        BotConfig.RemoteCommandsConfig config = plugin.getConfigData().remoteCommands;
        pendingConfirmations.put(userId, new PendingConfirmation(commandName, action, System.currentTimeMillis(), timeoutSeconds));

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#FEE75C"))
                .setTitle("Confirmation Required")
                .setDescription("Are you sure you want to execute **" + commandName + "**?\n\n"
                        + "Type `" + config.commandPrefix + "confirm` to proceed or `"
                        + config.commandPrefix + "cancel` to cancel.\n"
                        + "This will expire in **" + timeoutSeconds + " seconds**.");
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    private void handleConfirmation(MessageReceivedEvent event) {
        String userId = event.getAuthor().getId();
        PendingConfirmation pending = pendingConfirmations.remove(userId);

        if (pending == null) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("No pending confirmation found.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        long elapsed = (System.currentTimeMillis() - pending.timestamp) / 1000;
        if (elapsed > pending.timeoutSeconds) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("Confirmation has expired.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        pending.action.run();
        if (auditLogger != null) {
            auditLogger.log(userId, event.getAuthor().getName(),
                    pending.commandName, "CONFIRMED", "SUCCESS");
        }
    }

    private void handleCancel(MessageReceivedEvent event) {
        String userId = event.getAuthor().getId();
        PendingConfirmation pending = pendingConfirmations.remove(userId);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#57F287"))
                .setDescription(pending != null ? "Command **" + pending.commandName + "** cancelled." : "No pending confirmation to cancel.");
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    public List<String> getPermittedCommands(Member member, BotConfig.RemoteCommandsConfig config) {
        Set<String> permitted = new LinkedHashSet<>();
        if (member == null) return new ArrayList<>();

        for (Role role : member.getRoles()) {
            List<String> allowed = config.authorizedRoles.get(role.getId());
            if (allowed != null) {
                if (allowed.contains("*")) return new ArrayList<>(commands.keySet());
                permitted.addAll(allowed);
            }
        }
        return new ArrayList<>(permitted);
    }

    private static class PendingConfirmation {
        final String commandName;
        final Runnable action;
        final long timestamp;
        final int timeoutSeconds;

        PendingConfirmation(String commandName, Runnable action, long timestamp, int timeoutSeconds) {
            this.commandName = commandName;
            this.action = action;
            this.timestamp = timestamp;
            this.timeoutSeconds = timeoutSeconds;
        }
    }
}
