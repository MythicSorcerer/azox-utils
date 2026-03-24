# Development Plan

## ✅ Completed Tasks

### 🧭 Teleport Menu System
- [x] Create teleport menu with the following structure:
  - [x] List of all dimensions first
  - [x] List of online players (playerheads, click to teleport)
  - [x] Offline players (heads, also click to teleport)
  - [x] Next page navigation

### 🛡️ Admin Config Menu
- [x] Clean up azox admin config to only contain:
  - [x] Vanish settings
  - [x] Teleport menu access
- [x] Redesign admin config menu layout:
  - [x] Remove concrete blocks from menu
  - [x] Layout: `--V---T--` (V = vanish settings, T = teleport)

### 🐛 Bug Fixes
- [x] Fix `/v tipu` not working (item pickup toggle in vanish config menu works, but command didn't persist to config)

### ⚙️ Configuration System
- [x] Move GUI toggle to `/config` menu where more configurables can be added later
- [x] Added `/config` command with aliases `/cfg`, `/configuration`
- [x] Config GUI includes: GUI Mode, Particles, Vanish Settings

### 🌙 Night Vision System
- [x] Add `/nv` command with aliases: `/nvt`, `/nightvision`, `/nightvisiontoggle`
- [x] Command applies night vision effect (infinite, amplifier 0, hidden particles)
- [x] Re-apply effect on player respawn
- [x] Store preference in config so it persists after re-login

### 🔧 Utilities
- [x] Add loom to utilities menu
- [x] Added `/loom` command

### 👑 Permission System
- [x] Move permission nodes for player-use commands to `azox.user.*`
- [x] Move advanced permissions to `azox.rank.*` (enderchest pages, vanish levels, particles, ranks)
- [x] Move admin-only commands to `azox.admin.*`
- [x] Set `azox.admin.*` default to op

---

## 📋 Remaining Tasks

### 📝 Documentation
- [ ] Update `README.md` with new features

### 🔮 Future Enhancements
- [ ] Implement ranks system
- [ ] Add more configurables to `/config` menu
