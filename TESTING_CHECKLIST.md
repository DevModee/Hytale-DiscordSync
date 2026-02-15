# Testing Checklist - HytaleDiscordSync v2.0

## Pre-requisites
- [ ] Hytale server running with plugin loaded
- [ ] Discord bot token configured
- [ ] Valid channel ID set in config
- [ ] Bot invited to Discord server with proper permissions (Send Messages, Embed Links, Manage Roles, Read Message History)

---

## 1. Core Features (Existing)

### 1.1 Bot Startup
- [ ] Plugin loads without errors in console
- [ ] Bot connects to Discord successfully
- [ ] Server start embed sent to configured channel
- [ ] Status rotation begins cycling

### 1.2 Chat Sync
- [ ] In-game message appears in Discord channel
- [ ] Discord message appears in-game for all players
- [ ] Bot messages are ignored (no loops)
- [ ] Messages from wrong channel are ignored

### 1.3 Join/Leave Embeds
- [ ] Player join embed sent when player connects
- [ ] Player leave embed sent when player disconnects
- [ ] Setting `enabled: false` on join embed disables it
- [ ] Setting `enabled: false` on leave embed disables it
- [ ] Player count updates correctly

### 1.4 Status Command
- [ ] `/status` slash command responds with server info embed
- [ ] Player count is accurate
- [ ] Server icon and banner display correctly

### 1.5 Server Stop
- [ ] Server stop embed sent on shutdown
- [ ] Bot disconnects cleanly

---

## 2. Role Sync Feature

### 2.1 Configuration
- [ ] `roleSync` section auto-generated in config if missing
- [ ] Feature disabled by default (`enabled: false`)
- [ ] Enabling feature works after config change + restart
- [ ] Invalid guild ID handled gracefully

### 2.2 Account Linking
- [ ] `/link` command generates a 6-digit code
- [ ] Code displayed as ephemeral message
- [ ] Entering code in-game chat links the account
- [ ] Success message sent to player in-game
- [ ] Already-linked accounts get appropriate error
- [ ] Codes expire after 5 minutes
- [ ] `/unlink` removes the link
- [ ] `/unlink` when not linked shows error

### 2.3 Role Sync
- [ ] On player join, roles are synced (if `syncOnJoin: true`)
- [ ] Correct permission group assigned based on highest priority mapping
- [ ] Default group assigned when no roles match
- [ ] Default group assigned when player not linked
- [ ] `/syncplayer <name>` triggers manual sync
- [ ] Periodic sync runs at configured interval
- [ ] Discord role changes trigger automatic sync

### 2.4 Data Persistence
- [ ] `discordsync_links.json` created on first run
- [ ] Links persist across server restarts
- [ ] Corrupt file handled gracefully

---

## 3. Remote Commands Feature

### 3.1 Configuration
- [ ] `remoteCommands` section auto-generated if missing
- [ ] Feature disabled by default (`enabled: false`)
- [ ] Custom prefix works (e.g., `!`, `.`, `?`)
- [ ] Commands only work in designated channel

### 3.2 Permission System
- [ ] Only users with authorized roles can execute commands
- [ ] Users without roles get "Insufficient Permissions" embed
- [ ] Multiple roles with different permissions work correctly
- [ ] Wildcard (`*`) grants all commands

### 3.3 Rate Limiting
- [ ] Rate limiter blocks excessive commands
- [ ] Cooldown message shows remaining time
- [ ] Per-user rate limiting works independently
- [ ] Rate limit resets after 60 seconds

### 3.4 Commands

#### `!list`
- [ ] Shows online players list
- [ ] Shows correct player count
- [ ] Empty server shows "No players online"

#### `!whitelist add <player>`
- [ ] Adds player to whitelist file
- [ ] Duplicate add shows warning
- [ ] Missing player name shows usage

#### `!whitelist remove <player>`
- [ ] Removes player from whitelist
- [ ] Non-existent player shows error

#### `!whitelist list`
- [ ] Shows all whitelisted players
- [ ] Empty whitelist handled

#### `!kick <player> [reason]`
- [ ] Kicks online player
- [ ] Reason displayed in kick message
- [ ] Default reason when none provided
- [ ] Offline player shows error
- [ ] Broadcast to remaining players

#### `!broadcast <message>`
- [ ] Message sent to all online players
- [ ] Delivery count shown in response
- [ ] Empty message shows usage

#### `!stop`
- [ ] Requires confirmation
- [ ] `!confirm` executes shutdown
- [ ] `!cancel` cancels the action
- [ ] Confirmation expires after timeout

#### `!help`
- [ ] Shows only commands user has permission for
- [ ] Usage and descriptions displayed correctly

### 3.5 Audit Logging
- [ ] All commands logged to `discordsync_audit.log`
- [ ] Denied commands logged with reason
- [ ] Log includes timestamp, user, command, result
- [ ] Log file size managed (max 10000 lines)

### 3.6 Error Handling
- [ ] Unknown command shows error embed
- [ ] Bot offline handled gracefully
- [ ] Invalid arguments handled per command
- [ ] Command execution errors caught and reported

---

## 4. Config Migration
- [ ] Existing v1.x config auto-updated with new sections
- [ ] No data loss on existing settings
- [ ] Config saved after auto-update

---

## 5. Edge Cases
- [ ] Plugin works with role sync disabled and remote commands enabled
- [ ] Plugin works with remote commands disabled and role sync enabled
- [ ] Both features disabled = plugin works as v1.x
- [ ] Rapid player join/leave doesn't cause issues
- [ ] Server restart with linked accounts persists data
- [ ] Discord API rate limits handled gracefully
