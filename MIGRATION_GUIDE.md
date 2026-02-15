# Migration Guide — v1.x to v2.0

## Overview

HytaleDiscordSync v2.0 is a **non-breaking update**. Your existing configuration will continue to work without any changes. New config sections are added automatically on first startup.

---

## Upgrade Steps

### 1. Replace the Plugin JAR
- Remove the old `HytaleDiscordSync-*.jar` from your plugins folder
- Add the new `HytaleDiscordSync-2.0.jar`

### 2. Start the Server
- The plugin will detect your existing `discordsync_config.json`
- Two new sections (`roleSync` and `remoteCommands`) will be added automatically
- A log message will confirm: `[ConfigManager] Configuration file updated automatically.`

### 3. Configure New Features (Optional)
- Both new features are **disabled by default**
- Edit your config file and set `"enabled": true` for features you want
- Restart the server after config changes

---

## Config Changes

### New Sections Added Automatically

```json
{
  "roleSync": {
    "enabled": false,
    "syncOnJoin": true,
    "syncInterval": 300,
    "requireDiscordLink": false,
    "guildId": "",
    "defaultGroup": "member",
    "roleMappings": {
      "000000000000000000": {
        "permissionGroup": "vip",
        "priority": 1
      }
    }
  },
  "remoteCommands": {
    "enabled": false,
    "commandChannelId": "000000000000000000",
    "commandPrefix": "!",
    "authorizedRoles": {
      "000000000000000000": ["list", "whitelist", "kick", "broadcast", "stop"]
    },
    "rateLimit": {
      "enabled": true,
      "maxCommandsPerMinute": 10,
      "perUser": true
    }
  }
}
```

### No Changes to Existing Sections
All existing configuration options (`botToken`, `channelId`, `embeds`, `messages`, `statusMessages`, etc.) remain exactly the same.

---

## New Files Created by the Plugin

| File | Purpose |
|------|---------|
| `discordsync_links.json` | Stores Discord ↔ Player account links (only if role sync is enabled) |
| `discordsync_whitelist.json` | Stores whitelisted players (only if whitelist command is used) |
| `discordsync_audit.log` | Audit log of all remote commands executed |

---

## New Discord Bot Permissions Required

If you enable Role Sync, your bot needs these additional permissions:
- **Server Members Intent** — Enabled in the Discord Developer Portal under Bot settings
- **Manage Roles** — To read member roles for sync

If you only use Remote Commands, no additional permissions are needed beyond what v1.x required.

---

## New Slash Commands

| Command | Requires | Description |
|---------|----------|-------------|
| `/link` | Role Sync enabled | Generate account linking code |
| `/unlink` | Role Sync enabled | Unlink Discord from game account |
| `/syncplayer <player>` | Role Sync enabled | Manually sync a player's roles |

The existing `/status` command is unchanged.

---

## Rollback

If you need to go back to v1.x:
1. Replace the JAR with the old version
2. Your config file will work as-is (the extra sections are ignored by v1.x)
3. No data is lost

---

## FAQ

**Q: Will my existing config break?**
A: No. The plugin auto-detects missing sections and adds them with safe defaults.

**Q: Do I need to enable the new features?**
A: No. With both features disabled, the plugin behaves exactly like v1.x.

**Q: Can I enable just one feature?**
A: Yes. Role Sync and Remote Commands are completely independent.

**Q: What happens to the link data if I disable role sync later?**
A: The `discordsync_links.json` file is preserved. If you re-enable it, all links are restored.
