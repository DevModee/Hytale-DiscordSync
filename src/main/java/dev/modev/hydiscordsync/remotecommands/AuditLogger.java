package dev.modev.hydiscordsync.remotecommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLogger {

    private final Path logPath;
    private final DateTimeFormatter formatter;

    public AuditLogger(String fileName) {
        this.logPath = Paths.get(fileName);
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }
        } catch (IOException e) {
            System.err.println("[RemoteCommands] Error creating audit log: " + e.getMessage());
        }
    }

    public void log(String userId, String userName, String command, String arguments, String result) {
        String timestamp = LocalDateTime.now().format(formatter);
        String entry = String.format("[%s] User: %s (%s) | Command: %s | Args: %s | Result: %s",
                timestamp, userName, userId, command, arguments, result);

        try {
            List<String> lines = new ArrayList<>();
            if (Files.exists(logPath)) {
                lines.addAll(Files.readAllLines(logPath));
            }
            lines.add(entry);

            if (lines.size() > 10000) {
                lines = lines.subList(lines.size() - 5000, lines.size());
            }

            Files.write(logPath, lines);
        } catch (IOException e) {
            System.err.println("[RemoteCommands] Error writing audit log: " + e.getMessage());
        }

        System.out.println("[RemoteCommands] " + entry);
    }
}
