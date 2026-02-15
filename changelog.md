## v2.0

### New Features
- **Role Sync** — Link Discord roles to in-game permission groups
  - Account linking via 6-digit code system (`/link`, `/unlink`)
  - Automatic sync on join, periodic sync, and real-time role change detection
  - Priority-based role mapping with default group fallback
  - Manual sync with `/syncplayer <player>` command
  - Persistent link storage in `discordsync_links.json`
- **Remote Commands** — Execute server commands from Discord
  - `!list` — Show online players
  - `!whitelist add/remove/list` — Manage server whitelist
  - `!kick <player> [reason]` — Kick players from the server
  - `!broadcast <message>` — Send announcements to all players
  - `!stop` — Stop the server (with confirmation)
  - `!help` — Show available commands based on permissions
  - Role-based permission system for command access
  - Rate limiting (configurable per-user)
  - Full audit logging to `discordsync_audit.log`
  - Confirmation system for destructive commands

### Improvements
- Both new features disabled by default, fully toggleable
- Auto-migration: existing configs updated automatically with new sections
- Added GUILD_MEMBERS intent for role sync
- Version bumped to 2.0

## v1.5

- version cambiada a 1.5
- mensajes de log traducidos al ingles
- logica de auto-update al archivo de configuracion añadida
- discord: mensajes de texto plano trasladados a embeds