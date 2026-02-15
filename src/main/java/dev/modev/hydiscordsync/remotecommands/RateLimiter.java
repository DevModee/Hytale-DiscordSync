package dev.modev.hydiscordsync.remotecommands;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private final int maxPerMinute;
    private final boolean perUser;
    private final Map<String, CommandRecord> records;

    public RateLimiter(int maxPerMinute, boolean perUser) {
        this.maxPerMinute = maxPerMinute;
        this.perUser = perUser;
        this.records = new ConcurrentHashMap<>();
    }

    public boolean isRateLimited(String userId) {
        String key = perUser ? userId : "global";
        long now = System.currentTimeMillis();

        records.entrySet().removeIf(e -> now - e.getValue().windowStart > 60000);

        CommandRecord record = records.computeIfAbsent(key, k -> new CommandRecord(now, 0));

        if (now - record.windowStart > 60000) {
            record.windowStart = now;
            record.count = 0;
        }

        if (record.count >= maxPerMinute) {
            return true;
        }

        record.count++;
        return false;
    }

    public int getRemainingCooldown(String userId) {
        String key = perUser ? userId : "global";
        CommandRecord record = records.get(key);
        if (record == null) return 0;

        long elapsed = System.currentTimeMillis() - record.windowStart;
        if (elapsed > 60000) return 0;
        if (record.count < maxPerMinute) return 0;

        return (int) ((60000 - elapsed) / 1000);
    }

    private static class CommandRecord {
        long windowStart;
        int count;

        CommandRecord(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
