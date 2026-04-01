package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WeatherCommand extends BaseCommand {

    private final String type;

    public WeatherCommand(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        World world = sender instanceof Player ? ((Player) sender).getWorld() : plugin.getServer().getWorlds().get(0);
        
        switch (type.toLowerCase()) {
            case "sun":
                world.setStorm(false);
                world.setThundering(false);
                MessageUtil.sendMessage(sender, "<green>Weather set to sun!");
                break;
            case "storm":
                world.setStorm(true);
                world.setThundering(true);
                MessageUtil.sendMessage(sender, "<green>Weather set to storm!");
                break;
            case "weather":
                if (args.length == 0) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /weather <sun|storm>");
                    return;
                }
                if (args[0].equalsIgnoreCase("sun")) {
                    world.setStorm(false);
                    world.setThundering(false);
                    MessageUtil.sendMessage(sender, "<green>Weather set to sun!");
                } else if (args[0].equalsIgnoreCase("storm")) {
                    world.setStorm(true);
                    world.setThundering(true);
                    MessageUtil.sendMessage(sender, "<green>Weather set to storm!");
                }
                break;
        }
    }
}
