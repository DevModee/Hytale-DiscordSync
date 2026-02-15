# 🚀 HytaleDiscordSync v2.0 — Major Update

## Two Powerful New Features

### ⭐ Role Sync — Link Discord Roles to In-Game Groups
Automatically synchronize your Discord server roles with in-game permission groups. Players link their accounts with a simple code system, and the plugin handles the rest.

**Highlights:**
- 🔗 Simple account linking via `/link` command — generates a 6-digit code
- ⚡ Automatic sync on player join
- 🔄 Periodic background sync at configurable intervals
- 🎯 Priority-based role mapping — highest priority role wins
- 📡 Real-time sync when Discord roles change
- 🛡️ Default group fallback for unlinked players
- `/unlink` and `/syncplayer` commands for full control

### 🎮 Remote Commands — Control Your Server from Discord
Execute server commands directly from a designated Discord channel. Full permission system, rate limiting, audit logging, and beautiful embed responses.

**Available Commands:**
| Command | Description |
|---------|-------------|
| `!list` | View online players with a clean embed |
| `!whitelist add/remove/list` | Manage the server whitelist |
| `!kick <player> [reason]` | Kick players with broadcast notification |
| `!broadcast <message>` | Send announcements to all players |
| `!stop` | Stop the server (with confirmation) |
| `!help` | View available commands based on your permissions |

**Security Features:**
- 🔐 Role-based permission system — map Discord roles to allowed commands
- ⏱️ Rate limiting — prevent command spam (configurable per-user)
- 📝 Full audit logging — every command is tracked with timestamps
- ✅ Confirmation system for destructive commands (stop)
- 🚫 Command channel restriction — only works in the designated channel

---

## What's Included (All Features)

| Feature | v1.x | v2.0 |
|---------|------|------|
| Bidirectional Chat Sync | ✅ | ✅ |
| Join/Leave Embeds | ✅ | ✅ |
| Server Start/Stop Embeds | ✅ | ✅ |
| `/status` Command | ✅ | ✅ |
| Rotating Status Messages | ✅ | ✅ |
| Toggle Join/Leave Messages | ✅ | ✅ |
| **Role Sync** | ❌ | ✅ |
| **Remote Commands** | ❌ | ✅ |
| **Audit Logging** | ❌ | ✅ |
| **Rate Limiting** | ❌ | ✅ |
| **Whitelist Management** | ❌ | ✅ |

---

## Configuration

Both new features are **disabled by default** — enable them when ready:

```json
{
  "roleSync": {
    "enabled": true,
    "syncOnJoin": true,
    "syncInterval": 300,
    "guildId": "YOUR_GUILD_ID",
    "defaultGroup": "member",
    "roleMappings": {
      "DISCORD_ROLE_ID": {
        "permissionGroup": "vip",
        "priority": 1
      }
    }
  },
  "remoteCommands": {
    "enabled": true,
    "commandChannelId": "YOUR_CHANNEL_ID",
    "commandPrefix": "!",
    "authorizedRoles": {
      "ADMIN_ROLE_ID": ["list", "whitelist", "kick", "broadcast", "stop"]
    },
    "rateLimit": {
      "enabled": true,
      "maxCommandsPerMinute": 10,
      "perUser": true
    }
  }
}
```

---

## Setup Instructions

1. Replace the old `.jar` with the new `HytaleDiscordSync-2.0.jar`
2. Start the server — new config sections are added automatically
3. Stop the server and edit `discordsync_config.json`
4. Set `enabled: true` for the features you want
5. Configure role mappings and authorized roles with your Discord IDs
6. Restart the server

**Required Bot Permissions:** Send Messages, Embed Links, Read Message History, **Manage Roles** (for role sync), **Server Members Intent** (enabled automatically)

---

## Support
Join our Discord for help and updates: discord.gg/your-invite
