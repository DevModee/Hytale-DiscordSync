package dev.modev.hydiscordsync.remotecommands.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.modev.hydiscordsync.remotecommands.RemoteCommand;
import dev.modev.hydiscordsync.remotecommands.RemoteCommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WhitelistCommand implements RemoteCommand {

    private static final Path WHITELIST_PATH = Paths.get("discordsync_whitelist.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String getName() { return "whitelist"; }

    @Override
    public String getDescription() { return "Manage the server whitelist"; }

    @Override
    public String getUsage() { return "whitelist <add|remove|list> [player]"; }

    @Override
    public void execute(MessageReceivedEvent event, String[] args, RemoteCommandManager manager) {
        if (args.length == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#FEE75C"))
                    .setTitle("Whitelist Usage")
                    .setDescription("**Usage:**\n"
                            + "`whitelist add <player>` - Add a player\n"
                            + "`whitelist remove <player>` - Remove a player\n"
                            + "`whitelist list` - Show all whitelisted players");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add" -> handleAdd(event, args);
            case "remove" -> handleRemove(event, args);
            case "list" -> handleList(event);
            default -> {
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.decode("#ED4245"))
                        .setDescription("Invalid action. Use `add`, `remove`, or `list`.");
                event.getChannel().sendMessageEmbeds(eb.build()).queue();
            }
        }
    }

    private void handleAdd(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("Please specify a player name: `whitelist add <player>`");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String playerName = args[1];
        List<String> whitelist = loadWhitelist();

        if (whitelist.stream().anyMatch(p -> p.equalsIgnoreCase(playerName))) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#FEE75C"))
                    .setDescription("**" + playerName + "** is already whitelisted.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        whitelist.add(playerName);
        saveWhitelist(whitelist);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#57F287"))
                .setTitle("Player Whitelisted")
                .setDescription("**" + playerName + "** has been added to the whitelist.")
                .setFooter("By " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    private void handleRemove(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("Please specify a player name: `whitelist remove <player>`");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String playerName = args[1];
        List<String> whitelist = loadWhitelist();

        boolean removed = whitelist.removeIf(p -> p.equalsIgnoreCase(playerName));

        if (!removed) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.decode("#ED4245"))
                    .setDescription("**" + playerName + "** is not on the whitelist.");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        saveWhitelist(whitelist);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#57F287"))
                .setTitle("Player Removed")
                .setDescription("**" + playerName + "** has been removed from the whitelist.")
                .setFooter("By " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    private void handleList(MessageReceivedEvent event) {
        List<String> whitelist = loadWhitelist();

        String list = whitelist.isEmpty()
                ? "*Whitelist is empty*"
                : String.join("\n• ", "• " + String.join("\n• ", whitelist));

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.decode("#5865F2"))
                .setTitle("Whitelisted Players (" + whitelist.size() + ")")
                .setDescription(list)
                .setTimestamp(Instant.now());
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    private List<String> loadWhitelist() {
        if (!Files.exists(WHITELIST_PATH)) return new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(WHITELIST_PATH)) {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> list = gson.fromJson(reader, type);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("[Whitelist] Error loading: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveWhitelist(List<String> whitelist) {
        try {
            Files.write(WHITELIST_PATH, gson.toJson(whitelist).getBytes());
        } catch (IOException e) {
            System.err.println("[Whitelist] Error saving: " + e.getMessage());
        }
    }
}
