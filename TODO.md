# Development TODO

## ✅ Completed

### Teleport Menu System
- [x] Create teleport menu with dimensions, online/offline players, and pagination
- [x] Add back button navigation

### Admin Configuration
- [x] Clean up admin config menu (Vanish Settings + Teleport Menu only)
- [x] Create `/config` command for general settings

### Night Vision
- [x] Add `/nv` command with aliases
- [x] Persist night vision across sessions and respawns

### Utilities
- [x] Add loom to utilities menu

### Permissions
- [x] Structure permissions as `azox.user.*`, `azox.rank.*`, `azox.admin.*`

### Code Quality
- [x] Add null safety throughout
- [x] Use `final` modifiers
- [x] Use static `AzoxUtils.getInstance()` pattern
- [x] Use fully qualified variable names

---

## 📋 Pending Tasks

### Jail System Improvements
- [ ] Update jail messages:
  - [ ] Change dramatic jail message to "You have been sentenced to solitary confinement"
  - [ ] Make "Player has been jailed/solitary confinement" message global
  - [ ] Add escape message visible only to admins and in logs
- [ ] Add time component to `/jail` command:
  - [ ] Default: forever ("You have been jailed indefinitely")
  - [ ] Timed: "28d12h32m1s" → "You will be released in 28.5 days, unless you escape before then"
- [ ] Inescapable jail enhancements:
  - [ ] Apply Blindness I (infinite) while in jail
  - [ ] Apply Slowness XXV (infinite) while in jail
  - [ ] Apply Mining Fatigue XXV (infinite) while in jail
  - [ ] Teleport back if they leave the jail area

### Bug Fixes
- [ ] Fix Vanish Settings back button (currently doesn't work)
- [ ] Move vanish config out of `/config` menu (should be separate)

### Permissions
- [ ] If no permission manager plugin, grant all players `azox.user.*` by default

---

## 📝 Notes
- Keep this file updated as tasks are completed
- Use checkboxes `[ ]` for pending, `[x]` for completed
