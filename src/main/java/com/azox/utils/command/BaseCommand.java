package com.azox.utils.command;

import com.azox.utils.AzoxUtils;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final AzoxUtils plugin = AzoxUtils.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        execute(sender, label, args);
        return true;
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    protected boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }
        MessageUtil.sendMessage(sender, "<red>Only players can use this command!");
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return complete(sender, args);
    }

    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    protected List<String> getVisiblePlayerNames(CommandSender sender, String partial) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !(sender instanceof Player) || plugin.getVanishManager().canSee((Player) sender, p))
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    protected boolean permissionDenied(CommandSender sender, String permission) {
        if (!hasPermission(sender, permission)) {
            MessageUtil.sendMessage(sender, "<red>You don't have permission to use this command!");
            return true;
        }
        return false;
    }
}
