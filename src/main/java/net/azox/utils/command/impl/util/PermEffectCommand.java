package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class PermEffectCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;
        Player target = player;
        boolean isOtherPlayer = false;

        if (args.length >= 2 && sender.hasPermission("azox.util.permeffect.others")) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Player not found!");
                return;
            }
            isOtherPlayer = true;
        } else if (args.length >= 1 && sender.hasPermission("azox.util.permeffect.others")) {
            Player possibleTarget = Bukkit.getPlayer(args[0]);
            if (possibleTarget != null) {
                target = possibleTarget;
                isOtherPlayer = true;
            }
        }

        final Player finalTarget = target;

        if (args.length == 0) {
            if (plugin.getPlayerStorage().hasPermEffects(finalTarget)) {
                plugin.getPlayerStorage().clearPermEffects(finalTarget);
                finalTarget.getActivePotionEffects().forEach(effect -> {
                    if (plugin.getPlayerStorage().getPermEffectLevel(finalTarget, effect.getType().getName()) != null) {
                        finalTarget.removePotionEffect(effect.getType());
                    }
                });
                MessageUtil.sendMessage(sender, "<yellow>" + MessageUtil.ICON_INFO + " Disabled permanent effects for " + finalTarget.getName() + ".");
            } else {
                MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " No permanent effects to disable.");
            }
            return;
        }

        final String effectName = args[0].toUpperCase();
        PotionEffectType effectType;
        try {
            effectType = PotionEffectType.getByName(effectName);
        } catch (IllegalArgumentException e) {
            effectType = null;
        }

        if (effectType == null) {
            MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Invalid effect type. Use a valid effect name like: SPEED, HASTE, STRENGTH, etc.");
            return;
        }

        int level = 0;
        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]) - 1;
                if (level < 0 || level > 254) {
                    MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Effect level must be between 1 and 255.");
                    return;
                }
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Invalid level. Use a number between 1 and 255.");
                return;
            }
        } else {
            level = 0;
        }

        if (!sender.equals(target) && !sender.hasPermission("azox.util.permeffect.others")) {
            MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " You don't have permission to set effects for others.");
            return;
        }

        plugin.getPlayerStorage().setPermEffect(target, effectType.getName(), level);
        target.removePotionEffect(effectType);
        target.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, level, false, false));

        String levelDisplay = String.valueOf(level + 1);
        MessageUtil.sendMessage(target, "<green>" + MessageUtil.ICON_SUCCESS + " Permanent " + effectType.getName().toLowerCase() + " " + levelDisplay + " enabled!");
        if (!sender.equals(target)) {
            MessageUtil.sendMessage(sender, "<green>" + MessageUtil.ICON_SUCCESS + " Set permanent " + effectType.getName().toLowerCase() + " " + levelDisplay + " for " + target.getName() + ".");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("azox.util.permeffect.others")) {
                completions.addAll(Arrays.stream(PotionEffectType.values())
                        .map(type -> type.getName().toLowerCase())
                        .collect(Collectors.toList()));
            } else {
                completions.addAll(Arrays.stream(PotionEffectType.values())
                        .map(type -> type.getName().toLowerCase())
                        .collect(Collectors.toList()));
            }
        } else if (args.length == 2) {
            String effectName = args[0].toUpperCase();
            PotionEffectType effectType = PotionEffectType.getByName(effectName);
            if (effectType != null) {
                for (int i = 1; i <= 5; i++) {
                    completions.add(String.valueOf(i));
                }
            }
        } else if (args.length == 3 && sender.hasPermission("azox.util.permeffect.others")) {
            completions.addAll(getVisiblePlayerNames(sender, args[2]));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
