package net.azox.utils.command.impl.teleport;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public final class RtpCommand extends BaseCommand {

    private final Random random = new Random();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            MessageUtil.sendMessage(player, "<red>RTP is only available in the overworld!");
            return;
        }

        final int range = 5000;
        final int x = random.nextInt(range * 2) - range;
        final int z = random.nextInt(range * 2) - range;
        
        final Block block = world.getHighestBlockAt(x, z);
        if (block.getType() == Material.WATER || block.getType() == Material.LAVA) {
            // Recurse once if liquid, but for simplicity in this example just one attempt
            execute(sender, label, args);
            return;
        }

        final Location target = block.getLocation().add(0.5, 1, 0.5);
        plugin.getTeleportManager().teleportWithDelay(player, target);
        MessageUtil.sendMessage(player, "<green>Finding a safe location...");
    }
}
