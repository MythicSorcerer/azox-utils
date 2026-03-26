# AzoxUtils Documentation

A comprehensive utility plugin for Paper 1.21.11, featuring a modern Home system, Warp management, Teleportation requests, enhanced Jail system, and essential server utilities.

## 🏗️ Storage System
AzoxUtils uses a unified player data storage system. Each player has their own dedicated file located at `plugins/AzoxUtils/playerdata/username_uuid.yml`. This ensures that all settings, homes, and preferences are neatly organized and easy to manage.

## 🖥️ GUI & Admin Systems
AzoxUtils features a comprehensive GUI system for players and admins.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/utilities` | `/utilities` | `azox.utils.gui` | Opens the Utility Hub (Crafting, Enderchest, etc.). |
| `/config` | `/config` | `azox.utils.config` | Opens personal configuration (GUI toggle, particles). |
| `/azox` | `/azox` | `azox.utils.admin` | Opens the Admin Configuration GUI (Vanish, Teleport menu). |
| `/lobby` | `/lobby` | `azox.utils.lobby` | Teleports to the Hub/Lobby world. |

**Features:**
- **Admin Config:** Access Vanish settings and Teleport menu with dimension/player navigation.
- **Personal Config:** Toggle GUI menus and particle effects.
- **World Selector:** Automatically given a Compass in the Hub world to navigate servers.
- **Dynamic Utilities:** The `/utilities` menu only shows tools you have permission to use.
- **Ender Chest Pages:** Rank-based access to up to 5 ender chest pages. Access via `/enderchest` or `/ec`.
- **Night Vision:** Toggle with `/nv` - persists across sessions and respawns.

---

## 🚀 Core Systems

### 🏠 Home System
Manage multiple homes with interactive chat menus and public sharing capabilities.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/sethome` | `/sethome [name]` | `azox.utils.sethome` | Sets a home at your current location. |
| `/home` | `/home [name]` | `azox.utils.home` | Teleports to a home (3s delay). |
| `/delhome` | `/delhome <name\|all>` | `azox.utils.delhome` | Deletes a home. `/delhome all` requires confirmation. |
| `/homes` | `/homes [page]` | `azox.utils.home` | Lists your homes with interactive hover/click info. |
| `/phome` | `/phome [player:home]` | `azox.utils.phome` | Access public homes of other players. |
| `/edithome` | `/edithome <name>` | `azox.utils.edithome` | Opens an interactive chat menu to manage your home. |

**Home Limits:**
- Default: 4 homes.
- Permission-based: `azox.utils.homes.<number>` (e.g., `azox.utils.homes.10`).
- Unlimited: `azox.utils.sethome.unlimited`.

---

### 🌀 Warp System
Server-wide locations with access levels (1-10).

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/setwarp` | `/setwarp <name> [1-10]` | `azox.utils.setwarp` | Creates a warp with a specific access level. |
| `/warp` | `/warp <name>` | `azox.utils.warp.<level>` | Teleports to a warp if you have the required level. |

---

### ✈️ Teleportation (TPA & TPO)
Modern request system and advanced administrative teleportation.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/tpa` | `/tpa <player>` | `azox.utils.tpa` | Request to teleport to a player. |
| `/tpahere` | `/tpahere <player>` | `azox.utils.tpahere` | Request a player to teleport to you. |
| `/tpaccept` | `/tpaccept [player]` | `azox.utils.tpa` | Accepts the latest or specific TP request. |
| `/tpdecline` | `/tpdecline` | `azox.utils.tpa` | Declines a pending request. |
| `/tpignore` | `/tpignore` | `azox.utils.tpignore` | Toggle ignoring all incoming requests. |
| `/back` | `/back` | `azox.utils.back` | Return to your last location or death point. |
| `/rtp` | `/rtp` | `azox.utils.rtp` | Randomly teleport within 5000 blocks. |
| `/tpo` | `/tpo <player>` | `azox.utils.tpo` | Teleport to an online or offline player. |
| `/tpohere` | `/tpohere <player>` | `azox.utils.tpo` | Teleport an online or offline player to you. |
| `/tpoundo` | `/tpoundo <player>` | `azox.utils.tpo` | Undo the last `/tpohere` operation. |

---

### ⛓️ Jail System
Advanced jail system with timed sentences and inescapable confinement.

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/jail` | `/jail <player> <jail> [not] [dramatic] [time]` | `azox.utils.jail` | Jail a player with optional time (e.g., `1d12h30m`). |
| `/setjail` | `/setjail <name>` | `azox.utils.setjail` | Set a jail location. |
| `/deljail` | `/deljail <name>` | `azox.utils.deljail` | Delete a jail. |
| `/unjail` | `/unjail <player>` | `azox.utils.jail` | Release a player from jail. |

**Features:**
- **Timed Sentences:** Use formats like `1d`, `12h`, `30m`, `45s` or combinations (`1d12h30m`).
- **Indefinite:** No time specified = indefinite sentence.
- **Inescapable Jails:** Apply Blindness I, Slowness XXV, Mining Fatigue XXV. Auto-teleport back if escaped.
- **Auto-Release:** Players are freed automatically when their sentence expires.
- **Global Broadcast:** All players are notified when someone is sentenced.
- **Dramatic Mode:** Lightning strike and levitation effect before jailing.

---

### 🛡️ Admin & Miscellaneous

| Command | Usage | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/remove` | `/remove <type> [radius]` | `azox.utils.remove` | Remove entities (items, mobs, etc.) to reduce lag. |
| `/createkit` | `/createkit <name> [cooldown]` | `azox.utils.createkit` | Create a kit from your current inventory. |
| `/kit` | `/kit <name>` | `azox.utils.kit` | Claim a kit. |
| `/delkit` | `/delkit <name>` | `azox.utils.delkit` | Delete a kit. |
| `/vanish` | `/vanish [gui\|tipu\|fakejoin\|fakeleave]` | `azox.utils.vanish` | Advanced vanish system with stealth features. |
| `/freeze` | `/freeze <player>` | `azox.utils.freeze` | Prevent a player from moving or interacting. |
| `/nv` | `/nv` | `azox.utils.nightvision` | Toggle night vision (persists across sessions). |

---

## 👑 Permission System

AzoxUtils uses a structured permission system with automatic defaults.

### Default Permissions (`azox.user.*`)
If no permission manager is detected (LuckPerms, PermissionsEx, etc.), all players automatically receive `azox.user.*` permissions including:
- Teleport commands (`/tpa`, `/home`, `/warp`, `/rtp`, `/back`)
- Utility commands (`/craft`, `/anvil`, `/enderchest`, `/loom`)
- Personal commands (`/fly`, `/god`, `/heal`, `/feed`, `/nv`)
- Information commands (`/ping`, `/stats`, `/whois`, `/seen`)

### Rank Permissions (`azox.rank.*`)
- **Prefixes:** `azox.utils.rank.<name>` (owner, admin, mod, vip).
- **Particles:** `azox.utils.particles.<effect>`.
- **Ender Chest Pages:** `azox.utils.enderchest.pages.<1-5>`.
- **Vanish Levels:** `azox.utils.vanish.level.<number>`.

### Admin Permissions (`azox.admin.*`)
All admin commands default to ops. Includes:
- Jail management, vanish, freeze, sudo
- World editing (`/lightning`, `/burn`, `/weather`)
- Player management (`/gamemode`, `/speed`, `/tpo`)

---

## 🔧 Development Workflow

### Quick Testing Without Server Restarts

| Method | Description | Setup |
| :--- | :--- | :--- |
| **Config Reload** | Reload warps, jails, kits, homes without restart | `/azox reload` |
| **Paper Watchdog** | Auto-reload on file change (dev only) | Use [Paperwatch](https://github.com/PaperMC/paperwatch) |
| **Docker Hot-Reload** | Mount plugin folder, copy JAR on build | See docker-compose below |
| **IDE Remote Debug** | Debug running server remotely | Add `-agentlib:jdwp` to startup |

### Recommended: Docker Development Setup

```yaml
# docker-compose.yml
services:
  paper:
    image: ghcr.io/papermc/paper:1.21.11
    volumes:
      - ./plugins:/plugins
      - ./world:/world
    ports:
      - "25565:25565"
    environment:
      - EULA=TRUE
```

```bash
# Build and auto-deploy script
#!/bin/bash
mvn clean package -DskipTests
cp target/AzoxUtils-1.0.0.jar ../test-server/plugins/
ssh user@server "docker cp AzoxUtils-1.0.0.jar paper:/plugins/ && docker exec paper /paper reload"
```

### Remote Debug Setup

Add to server startup script:
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar paper.jar
```

Then connect IDE to `localhost:5005` for breakpoints.

### Plugin Format Note

This plugin uses **Bukkit API** (`plugin.yml`) which is fully compatible with Paper servers.
The newer "Paper plugin" format (`paper-plugin.yml`) requires programmatic command registration
and offers no benefit for this plugin's use case.

---

## 🎨 Design Features
- **MiniMessage Support:** All messages use modern `<color>` tags and support hover/click events.
- **Geyser Compatible:** Avoids complex GUI containers where possible, using interactive chat components that work perfectly for Bedrock players.
- **Smart Completion:** Tab-completion for homes, warps, players, and coordinates, respecting vanish levels.
- **Null Safety:** Comprehensive null checks throughout to prevent crashes.
- **Clean UI:** Status indicators show "✔ Enabled" / "✘ Disabled" instead of raw material names.
