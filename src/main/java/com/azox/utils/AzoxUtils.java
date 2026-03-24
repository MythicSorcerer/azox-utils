package com.azox.utils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class AzoxUtils extends JavaPlugin {

    @Getter
    private static AzoxUtils instance;

    @Getter
    private com.azox.utils.manager.HomeManager homeManager;
    @Getter
    private com.azox.utils.manager.WarpManager warpManager;
    @Getter
    private com.azox.utils.manager.TeleportManager teleportManager;
    @Getter
    private com.azox.utils.manager.FreezeManager freezeManager;
    @Getter
    private com.azox.utils.manager.GuiManager guiManager;
    @Getter
    private com.azox.utils.manager.JailManager jailManager;
    @Getter
    private com.azox.utils.manager.VanishManager vanishManager;
    @Getter
    private com.azox.utils.manager.KitManager kitManager;
    @Getter
    private com.azox.utils.manager.PlayerDataManager playerDataManager;
    @Getter
    private com.azox.utils.manager.ParticleManager particleManager;
    @Getter
    private com.azox.utils.storage.PlayerStorage playerStorage;

    @Override
    public void onEnable() {
        instance = this;

        this.playerDataManager = new com.azox.utils.manager.PlayerDataManager();
        this.playerStorage = new com.azox.utils.storage.PlayerStorage();
        this.homeManager = new com.azox.utils.manager.HomeManager();
        this.warpManager = new com.azox.utils.manager.WarpManager();
        this.teleportManager = new com.azox.utils.manager.TeleportManager();
        this.freezeManager = new com.azox.utils.manager.FreezeManager();
        this.guiManager = new com.azox.utils.manager.GuiManager();
        this.jailManager = new com.azox.utils.manager.JailManager();
        this.vanishManager = new com.azox.utils.manager.VanishManager();
        this.kitManager = new com.azox.utils.manager.KitManager();
        this.particleManager = new com.azox.utils.manager.ParticleManager();

        
        registerCommands();
        registerListeners();
        
        this.getLogger().info("AzoxUtils has been enabled!");
    }

    private void registerCommands() {
        // Home commands
        getCommand("sethome").setExecutor(new com.azox.utils.command.impl.home.SetHomeCommand());
        getCommand("home").setExecutor(new com.azox.utils.command.impl.home.HomeCommand());
        getCommand("delhome").setExecutor(new com.azox.utils.command.impl.home.DelHomeCommand());
        getCommand("homes").setExecutor(new com.azox.utils.command.impl.home.HomesCommand());
        getCommand("edithome").setExecutor(new com.azox.utils.command.impl.home.EditHomeCommand());
        getCommand("phome").setExecutor(new com.azox.utils.command.impl.home.PHomeCommand());
        
        // Warp commands
        getCommand("setwarp").setExecutor(new com.azox.utils.command.impl.warp.SetWarpCommand());
        getCommand("warp").setExecutor(new com.azox.utils.command.impl.warp.WarpCommand());
        
        // Teleport commands
        getCommand("tpa").setExecutor(new com.azox.utils.command.impl.teleport.TpaCommand());
        getCommand("tpahere").setExecutor(new com.azox.utils.command.impl.teleport.TpaHereCommand());
        getCommand("tpaccept").setExecutor(new com.azox.utils.command.impl.teleport.TpAcceptCommand());
        getCommand("tpdecline").setExecutor(new com.azox.utils.command.impl.teleport.TpDeclineCommand());
        getCommand("tpignore").setExecutor(new com.azox.utils.command.impl.teleport.TpIgnoreCommand());
        getCommand("back").setExecutor(new com.azox.utils.command.impl.teleport.BackCommand());
        getCommand("rtp").setExecutor(new com.azox.utils.command.impl.teleport.RtpCommand());
        getCommand("tpo").setExecutor(new com.azox.utils.command.impl.teleport.TpoCommand());
        getCommand("tpohere").setExecutor(new com.azox.utils.command.impl.teleport.TpoCommand());
        getCommand("tpoundo").setExecutor(new com.azox.utils.command.impl.teleport.TpoCommand());
        
        // Inventory util
        getCommand("enderchest").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("enderchest"));
        getCommand("anvil").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("anvil"));
        getCommand("cartographytable").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("cartographytable"));
        getCommand("loom").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("loom"));
        getCommand("trash").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("trash"));
        getCommand("craft").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("craft"));
        getCommand("grindstone").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("grindstone"));
        getCommand("stonecutter").setExecutor(new com.azox.utils.command.impl.util.InventoryUtilCommands("stonecutter"));
        getCommand("utilities").setExecutor(new com.azox.utils.command.impl.util.GuiCommand());
        getCommand("config").setExecutor(new com.azox.utils.command.impl.util.ConfigCommand());
        getCommand("settings").setExecutor(new com.azox.utils.command.impl.util.SettingsCommand());
        
        getCommand("see").setExecutor(new com.azox.utils.command.impl.util.SeeCommand());
        getCommand("si").setExecutor(new com.azox.utils.command.impl.util.SeeCommand());
        getCommand("se").setExecutor(new com.azox.utils.command.impl.util.SeeCommand());
        
        getCommand("jail").setExecutor(new com.azox.utils.command.impl.util.JailCommand());
        getCommand("setjail").setExecutor(new com.azox.utils.command.impl.util.JailCommand());
        getCommand("deljail").setExecutor(new com.azox.utils.command.impl.util.JailCommand());
        getCommand("unjail").setExecutor(new com.azox.utils.command.impl.util.JailCommand());
        
        getCommand("azox").setExecutor(new com.azox.utils.command.impl.util.AdminCommand());
        getCommand("lobby").setExecutor(new com.azox.utils.command.impl.util.LobbyCommand());
        getCommand("remove").setExecutor(new com.azox.utils.command.impl.util.RemoveCommand());
        getCommand("createkit").setExecutor(new com.azox.utils.command.impl.util.KitCommands());
        getCommand("kit").setExecutor(new com.azox.utils.command.impl.util.KitCommands());
        getCommand("delkit").setExecutor(new com.azox.utils.command.impl.util.KitCommands());
        
        // Player util
        getCommand("feed").setExecutor(new com.azox.utils.command.impl.util.PlayerUtilCommands("feed"));
        getCommand("heal").setExecutor(new com.azox.utils.command.impl.util.PlayerUtilCommands("heal"));
        getCommand("fly").setExecutor(new com.azox.utils.command.impl.util.PlayerUtilCommands("fly"));
        getCommand("god").setExecutor(new com.azox.utils.command.impl.util.PlayerUtilCommands("god"));
        
        // Navigation
        getCommand("top").setExecutor(new com.azox.utils.command.impl.util.NavigationCommands("top"));
        getCommand("jumpto").setExecutor(new com.azox.utils.command.impl.util.NavigationCommands("jumpto"));
        getCommand("near").setExecutor(new com.azox.utils.command.impl.util.NavigationCommands("near"));
        getCommand("world").setExecutor(new com.azox.utils.command.impl.util.NavigationCommands("world"));
        
        // Gamemode
        getCommand("gamemode").setExecutor(new com.azox.utils.command.impl.util.GamemodeCommand(null));
        getCommand("gms").setExecutor(new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.SURVIVAL));
        getCommand("gmc").setExecutor(new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.CREATIVE));
        getCommand("gma").setExecutor(new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new com.azox.utils.command.impl.util.GamemodeCommand(org.bukkit.GameMode.SPECTATOR));
        
        getCommand("speed").setExecutor(new com.azox.utils.command.impl.util.SpeedCommand());
        getCommand("weather").setExecutor(new com.azox.utils.command.impl.util.WeatherCommand("weather"));
        getCommand("sun").setExecutor(new com.azox.utils.command.impl.util.WeatherCommand("sun"));
        getCommand("storm").setExecutor(new com.azox.utils.command.impl.util.WeatherCommand("storm"));
        getCommand("setspawn").setExecutor(new com.azox.utils.command.impl.util.SpawnCommand(true));
        
        getCommand("itemname").setExecutor(new com.azox.utils.command.impl.util.ItemModCommands("itemname"));
        getCommand("copyitem").setExecutor(new com.azox.utils.command.impl.util.ItemModCommands("copyitem"));
        getCommand("repair").setExecutor(new com.azox.utils.command.impl.util.ItemModCommands("repair"));
        getCommand("enchant").setExecutor(new com.azox.utils.command.impl.util.ItemModCommands("enchant"));
        getCommand("lore").setExecutor(new com.azox.utils.command.impl.util.ItemModCommands("lore"));
        getCommand("rules").setExecutor(new com.azox.utils.command.impl.util.RulesCommand());
        
        // System
        getCommand("tps").setExecutor(new com.azox.utils.command.impl.util.SystemCommands("tps"));
        getCommand("ping").setExecutor(new com.azox.utils.command.impl.util.SystemCommands("ping"));
        getCommand("uptime").setExecutor(new com.azox.utils.command.impl.util.SystemCommands("uptime"));
        getCommand("stats").setExecutor(new com.azox.utils.command.impl.util.SystemCommands("stats"));
        getCommand("azoxreload").setExecutor(new com.azox.utils.command.impl.util.SystemCommands("azoxreload"));
        
        // Admin
        getCommand("sudo").setExecutor(new com.azox.utils.command.impl.util.AdminUtilCommands("sudo"));
        getCommand("lightning").setExecutor(new com.azox.utils.command.impl.util.AdminUtilCommands("lightning"));
        getCommand("burn").setExecutor(new com.azox.utils.command.impl.util.AdminUtilCommands("burn"));
        getCommand("extinguish").setExecutor(new com.azox.utils.command.impl.util.AdminUtilCommands("extinguish"));
        getCommand("freeze").setExecutor(new com.azox.utils.command.impl.util.AdminUtilCommands("freeze"));
        getCommand("vanish").setExecutor(new com.azox.utils.command.impl.util.VanishCommand());
        getCommand("nightvision").setExecutor(new com.azox.utils.command.impl.util.NightVisionCommand());
        getCommand("nv").setExecutor(new com.azox.utils.command.impl.util.NightVisionCommand());
        getCommand("nvt").setExecutor(new com.azox.utils.command.impl.util.NightVisionCommand());
        getCommand("nightvisiontoggle").setExecutor(new com.azox.utils.command.impl.util.NightVisionCommand());
        
        // Misc
        getCommand("tp").setExecutor(new com.azox.utils.command.impl.util.TpCommand());
        getCommand("condense").setExecutor(new com.azox.utils.command.impl.util.CondenseCommand());
        getCommand("clearinventory").setExecutor(new com.azox.utils.command.impl.util.ClearInventoryCommand());
        getCommand("getpos").setExecutor(new com.azox.utils.command.impl.util.MiscUtilCommands("getpos"));
        getCommand("whois").setExecutor(new com.azox.utils.command.impl.util.MiscUtilCommands("whois"));
        getCommand("broadcast").setExecutor(new com.azox.utils.command.impl.util.MiscUtilCommands("broadcast"));
        getCommand("suicide").setExecutor(new com.azox.utils.command.impl.util.MiscUtilCommands("suicide"));
        getCommand("break").setExecutor(new com.azox.utils.command.impl.util.MiscUtilCommands("break"));
        getCommand("compass").setExecutor(new com.azox.utils.command.impl.util.MiscUtilCommands("compass"));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new com.azox.utils.listener.TeleportListener(), this);
        getServer().getPluginManager().registerEvents(new com.azox.utils.listener.PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new com.azox.utils.listener.InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        // Save data
        this.getLogger().info("AzoxUtils has been disabled!");
    }
}
