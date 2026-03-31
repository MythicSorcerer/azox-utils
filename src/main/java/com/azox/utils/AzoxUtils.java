package com.azox.utils;

import com.azox.utils.command.impl.home.DelHomeCommand;
import com.azox.utils.command.impl.home.EditHomeCommand;
import com.azox.utils.command.impl.home.HomeCommand;
import com.azox.utils.command.impl.home.HomesCommand;
import com.azox.utils.command.impl.home.PHomeCommand;
import com.azox.utils.command.impl.home.SetHomeCommand;
import com.azox.utils.command.impl.teleport.BackCommand;
import com.azox.utils.command.impl.teleport.RtpCommand;
import com.azox.utils.command.impl.teleport.TpaCommand;
import com.azox.utils.command.impl.teleport.TpaHereCommand;
import com.azox.utils.command.impl.teleport.TpAcceptCommand;
import com.azox.utils.command.impl.teleport.TpDeclineCommand;
import com.azox.utils.command.impl.teleport.TpIgnoreCommand;
import com.azox.utils.command.impl.teleport.TpoCommand;
import com.azox.utils.command.impl.util.AdminCommand;
import com.azox.utils.command.impl.util.AdminUtilCommands;
import com.azox.utils.command.impl.util.ClearInventoryCommand;
import com.azox.utils.command.impl.util.ConfigCommand;
import com.azox.utils.command.impl.util.CondenseCommand;
import com.azox.utils.command.impl.util.GamemodeCommand;
import com.azox.utils.command.impl.util.GuiCommand;
import com.azox.utils.command.impl.util.InventoryUtilCommands;
import com.azox.utils.command.impl.util.ItemModCommands;
import com.azox.utils.command.impl.util.JailCommand;
import com.azox.utils.command.impl.util.KitCommands;
import com.azox.utils.command.impl.util.LobbyCommand;
import com.azox.utils.command.impl.util.MiscUtilCommands;
import com.azox.utils.command.impl.util.NavigationCommands;
import com.azox.utils.command.impl.util.NightVisionCommand;
import com.azox.utils.command.impl.util.PlayerUtilCommands;
import com.azox.utils.command.impl.util.RemoveCommand;
import com.azox.utils.command.impl.util.RulesCommand;
import com.azox.utils.command.impl.util.SeeCommand;
import com.azox.utils.command.impl.util.SettingsCommand;
import com.azox.utils.command.impl.util.SpawnCommand;
import com.azox.utils.command.impl.util.SpeedCommand;
import com.azox.utils.command.impl.util.SystemCommands;
import com.azox.utils.command.impl.util.TpCommand;
import com.azox.utils.command.impl.util.VanishCommand;
import com.azox.utils.command.impl.util.WeatherCommand;
import com.azox.utils.command.impl.warp.SetWarpCommand;
import com.azox.utils.command.impl.warp.WarpCommand;
import com.azox.utils.listener.InventoryListener;
import com.azox.utils.listener.PlayerListener;
import com.azox.utils.listener.TeleportListener;
import com.azox.utils.manager.FreezeManager;
import com.azox.utils.manager.GuiManager;
import com.azox.utils.manager.HomeManager;
import com.azox.utils.manager.JailManager;
import com.azox.utils.manager.KitManager;
import com.azox.utils.manager.ParticleManager;
import com.azox.utils.manager.PlayerDataManager;
import com.azox.utils.manager.TeleportManager;
import com.azox.utils.manager.VanishManager;
import com.azox.utils.manager.WarpManager;
import com.azox.utils.storage.PlayerStorage;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@Getter
public final class AzoxUtils extends JavaPlugin {

    private static AzoxUtils instance;

    private PlayerDataManager playerDataManager;
    private PlayerStorage playerStorage;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private TeleportManager teleportManager;
    private FreezeManager freezeManager;
    private GuiManager guiManager;
    private JailManager jailManager;
    private VanishManager vanishManager;
    private KitManager kitManager;
    private ParticleManager particleManager;

    public static AzoxUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        AzoxUtils.instance = this;

        this.playerDataManager = new PlayerDataManager();
        this.playerStorage = new PlayerStorage();
        this.homeManager = new HomeManager();
        this.warpManager = new WarpManager();
        this.teleportManager = new TeleportManager();
        this.freezeManager = new FreezeManager();
        this.guiManager = new GuiManager();
        this.jailManager = new JailManager();
        this.vanishManager = new VanishManager();
        this.kitManager = new KitManager();
        this.particleManager = new ParticleManager();

        this.registerCommands();
        this.registerListeners();

        this.getLogger().info("AzoxUtils has been enabled!");
    }

    private void registerCommands() {
        final Map<String, CommandExecutor> commandMap = new java.util.HashMap<>();

        commandMap.put("sethome", new SetHomeCommand());
        commandMap.put("home", new HomeCommand());
        commandMap.put("delhome", new DelHomeCommand());
        commandMap.put("homes", new HomesCommand());
        commandMap.put("edithome", new EditHomeCommand());
        commandMap.put("phome", new PHomeCommand());

        commandMap.put("setwarp", new SetWarpCommand());
        commandMap.put("warp", new WarpCommand());

        commandMap.put("tpa", new TpaCommand());
        commandMap.put("tpahere", new TpaHereCommand());
        commandMap.put("tpaccept", new TpAcceptCommand());
        commandMap.put("tpdecline", new TpDeclineCommand());
        commandMap.put("tpignore", new TpIgnoreCommand());
        commandMap.put("back", new BackCommand());
        commandMap.put("rtp", new RtpCommand());
        commandMap.put("tpo", new TpoCommand());
        commandMap.put("tpohere", new TpoCommand());
        commandMap.put("tpoundo", new TpoCommand());

        commandMap.put("enderchest", new InventoryUtilCommands("enderchest"));
        commandMap.put("anvil", new InventoryUtilCommands("anvil"));
        commandMap.put("cartographytable", new InventoryUtilCommands("cartographytable"));
        commandMap.put("loom", new InventoryUtilCommands("loom"));
        commandMap.put("trash", new InventoryUtilCommands("trash"));
        commandMap.put("craft", new InventoryUtilCommands("craft"));
        commandMap.put("grindstone", new InventoryUtilCommands("grindstone"));
        commandMap.put("stonecutter", new InventoryUtilCommands("stonecutter"));
        commandMap.put("utilities", new GuiCommand());
        commandMap.put("config", new ConfigCommand());
        commandMap.put("settings", new SettingsCommand());

        commandMap.put("see", new SeeCommand());
        commandMap.put("si", new SeeCommand());
        commandMap.put("se", new SeeCommand());

        commandMap.put("jail", new JailCommand());
        commandMap.put("setjail", new JailCommand());
        commandMap.put("deljail", new JailCommand());
        commandMap.put("unjail", new JailCommand());

        commandMap.put("azox", new AdminCommand());
        commandMap.put("lobby", new LobbyCommand());
        commandMap.put("remove", new RemoveCommand());
        commandMap.put("createkit", new KitCommands());
        commandMap.put("kit", new KitCommands());
        commandMap.put("delkit", new KitCommands());

        commandMap.put("feed", new PlayerUtilCommands("feed"));
        commandMap.put("heal", new PlayerUtilCommands("heal"));
        commandMap.put("fly", new PlayerUtilCommands("fly"));
        commandMap.put("god", new PlayerUtilCommands("god"));

        commandMap.put("top", new NavigationCommands("top"));
        commandMap.put("jumpto", new NavigationCommands("jumpto"));
        commandMap.put("near", new NavigationCommands("near"));
        commandMap.put("world", new NavigationCommands("world"));

        commandMap.put("gamemode", new GamemodeCommand(null));
        commandMap.put("gms", new GamemodeCommand(GameMode.SURVIVAL));
        commandMap.put("gmc", new GamemodeCommand(GameMode.CREATIVE));
        commandMap.put("gma", new GamemodeCommand(GameMode.ADVENTURE));
        commandMap.put("gmsp", new GamemodeCommand(GameMode.SPECTATOR));

        commandMap.put("speed", new SpeedCommand());
        commandMap.put("weather", new WeatherCommand("weather"));
        commandMap.put("sun", new WeatherCommand("sun"));
        commandMap.put("storm", new WeatherCommand("storm"));
        commandMap.put("setspawn", new SpawnCommand(true));

        commandMap.put("itemname", new ItemModCommands("itemname"));
        commandMap.put("copyitem", new ItemModCommands("copyitem"));
        commandMap.put("repair", new ItemModCommands("repair"));
        commandMap.put("enchant", new ItemModCommands("enchant"));
        commandMap.put("lore", new ItemModCommands("lore"));
        commandMap.put("rules", new RulesCommand());

        commandMap.put("tps", new SystemCommands("tps"));
        commandMap.put("ping", new SystemCommands("ping"));
        commandMap.put("uptime", new SystemCommands("uptime"));
        commandMap.put("stats", new SystemCommands("stats"));
        commandMap.put("azoxreload", new SystemCommands("azoxreload"));

        commandMap.put("sudo", new AdminUtilCommands("sudo"));
        commandMap.put("lightning", new AdminUtilCommands("lightning"));
        commandMap.put("burn", new AdminUtilCommands("burn"));
        commandMap.put("extinguish", new AdminUtilCommands("extinguish"));
        commandMap.put("freeze", new AdminUtilCommands("freeze"));
        commandMap.put("vanish", new VanishCommand());
        commandMap.put("nightvision", new NightVisionCommand());
        commandMap.put("nv", new NightVisionCommand());
        commandMap.put("nvt", new NightVisionCommand());
        commandMap.put("nightvisiontoggle", new NightVisionCommand());

        commandMap.put("tp", new TpCommand());
        commandMap.put("condense", new CondenseCommand());
        commandMap.put("clearinventory", new ClearInventoryCommand());
        commandMap.put("getpos", new MiscUtilCommands("getpos"));
        commandMap.put("whois", new MiscUtilCommands("whois"));
        commandMap.put("broadcast", new MiscUtilCommands("broadcast"));
        commandMap.put("suicide", new MiscUtilCommands("suicide"));
        commandMap.put("break", new MiscUtilCommands("break"));
        commandMap.put("compass", new MiscUtilCommands("compass"));

        commandMap.forEach((name, executor) -> {
            final var command = this.getCommand(name);
            if (command != null) {
                command.setExecutor(executor);
            }
        });
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new TeleportListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("AzoxUtils has been disabled!");
    }
}
