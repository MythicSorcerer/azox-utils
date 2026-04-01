package net.azox.utils;

import net.azox.utils.command.impl.home.DelHomeCommand;
import net.azox.utils.command.impl.home.EditHomeCommand;
import net.azox.utils.command.impl.home.HomeCommand;
import net.azox.utils.command.impl.home.HomesCommand;
import net.azox.utils.command.impl.home.PHomeCommand;
import net.azox.utils.command.impl.home.SetHomeCommand;
import net.azox.utils.command.impl.teleport.BackCommand;
import net.azox.utils.command.impl.teleport.RtpCommand;
import net.azox.utils.command.impl.teleport.TpaCommand;
import net.azox.utils.command.impl.teleport.TpaHereCommand;
import net.azox.utils.command.impl.teleport.TpAcceptCommand;
import net.azox.utils.command.impl.teleport.TpDeclineCommand;
import net.azox.utils.command.impl.teleport.TpIgnoreCommand;
import net.azox.utils.command.impl.teleport.TpoCommand;
import net.azox.utils.command.impl.util.AdminCommand;
import net.azox.utils.command.impl.util.AdminUtilCommands;
import net.azox.utils.command.impl.util.ClearInventoryCommand;
import net.azox.utils.command.impl.util.ConfigCommand;
import net.azox.utils.command.impl.util.CondenseCommand;
import net.azox.utils.command.impl.util.GamemodeCommand;
import net.azox.utils.command.impl.util.GuiCommand;
import net.azox.utils.command.impl.util.ILiveCommand;
import net.azox.utils.command.impl.util.InventoryUtilCommands;
import net.azox.utils.command.impl.util.ItemModCommands;
import net.azox.utils.command.impl.util.JailCommand;
import net.azox.utils.command.impl.util.KitCommands;
import net.azox.utils.command.impl.util.LobbyCommand;
import net.azox.utils.command.impl.util.MiscUtilCommands;
import net.azox.utils.command.impl.util.NavigationCommands;
import net.azox.utils.command.impl.util.NightVisionCommand;
import net.azox.utils.command.impl.util.PermEffectCommand;
import net.azox.utils.command.impl.util.PlayerUtilCommands;
import net.azox.utils.command.impl.util.PotionCommands;
import net.azox.utils.command.impl.util.RemoveCommand;
import net.azox.utils.command.impl.util.RulesCommand;
import net.azox.utils.command.impl.util.SeeCommand;
import net.azox.utils.command.impl.util.SettingsCommand;
import net.azox.utils.command.impl.util.SilenceCommand;
import net.azox.utils.command.impl.util.SpawnCommand;
import net.azox.utils.command.impl.util.SpeedCommand;
import net.azox.utils.command.impl.util.SystemCommands;
import net.azox.utils.command.impl.util.TpCommand;
import net.azox.utils.command.impl.util.VanishCommand;
import net.azox.utils.command.impl.util.WeatherCommand;
import net.azox.utils.command.impl.warp.SetWarpCommand;
import net.azox.utils.command.impl.warp.WarpCommand;
import net.azox.utils.listener.InventoryListener;
import net.azox.utils.listener.PlayerListener;
import net.azox.utils.listener.TeleportListener;
import net.azox.utils.manager.FillPotManager;
import net.azox.utils.manager.FreezeManager;
import net.azox.utils.manager.GuiManager;
import net.azox.utils.manager.HomeManager;
import net.azox.utils.manager.ILiveManager;
import net.azox.utils.manager.JailManager;
import net.azox.utils.manager.KitManager;
import net.azox.utils.manager.ParticleManager;
import net.azox.utils.manager.PlayerDataManager;
import net.azox.utils.manager.TeleportManager;
import net.azox.utils.manager.VanishManager;
import net.azox.utils.manager.WarpManager;
import net.azox.utils.storage.PlayerStorage;
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
    private ILiveManager iliveManager;
    private FillPotManager fillPotManager;

    public static AzoxUtils getInstance() {
        return instance;
    }

    public ILiveManager getILiveManager() {
        return iliveManager;
    }

    public FillPotManager getFillPotManager() {
        return fillPotManager;
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
        this.iliveManager = new ILiveManager();
        this.fillPotManager = new FillPotManager();

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

        commandMap.put("permeffect", new PermEffectCommand());
        commandMap.put("ilive", new ILiveCommand());
        commandMap.put("fillpotsave", new PotionCommands());
        commandMap.put("fillpot", new PotionCommands());
        commandMap.put("unfillpot", new PotionCommands());
        commandMap.put("silence", new SilenceCommand());

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
