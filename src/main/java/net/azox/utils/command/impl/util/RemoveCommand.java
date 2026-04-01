package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class RemoveCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "<red>Usage: /remove <all|mobs|animals|monsters|items|xp> [radius]");
            return;
        }

        String type = args[0].toLowerCase();
        int radius = -1;
        if (args.length > 1) {
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }

        int count = 0;
        List<Entity> entities;
        if (radius > 0) {
            entities = player.getNearbyEntities(radius, radius, radius);
        } else {
            entities = player.getWorld().getEntities();
        }

        for (Entity entity : entities) {
            if (entity instanceof Player) continue; // Never remove players
            
            boolean remove = false;
            switch (type) {
                case "all":
                    remove = true;
                    break;
                case "mobs":
                    remove = entity instanceof Mob;
                    break;
                case "animals":
                    remove = entity instanceof Animals;
                    break;
                case "monsters":
                    remove = entity instanceof Monster;
                    break;
                case "items":
                case "drops":
                    remove = entity instanceof Item;
                    break;
                case "xp":
                    remove = entity instanceof ExperienceOrb;
                    break;
            }

            if (remove) {
                entity.remove();
                count++;
            }
        }

        MessageUtil.sendMessage(player, "<green>Removed " + count + " entities!");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("all", "mobs", "animals", "monsters", "items", "xp");
        }
        return super.complete(sender, args);
    }
}
