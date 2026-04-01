package net.azox.utils.command.impl.util;

import net.azox.utils.AzoxUtils;
import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class ILiveCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        String mode = "nototem";
        int damageReduction = 1;
        boolean foodHeal = true;

        for (String arg : args) {
            String lower = arg.toLowerCase();
            if (lower.equals("totem")) {
                mode = "totem";
            } else if (lower.equals("nototem")) {
                mode = "nototem";
            } else if (lower.equals("nofood")) {
                foodHeal = false;
            } else if (lower.equals("food")) {
                foodHeal = true;
            } else {
                try {
                    int level = Integer.parseInt(lower);
                    if (level >= 0 && level <= 5) {
                        damageReduction = level;
                    } else {
                        MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Damage reduction must be 0-5.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    MessageUtil.sendMessage(sender, "<red>" + MessageUtil.ICON_ERROR + " Invalid argument: " + arg);
                    return;
                }
            }
        }

        if (args.length == 0) {
            if (AzoxUtils.getInstance().getILiveManager().isEnabled(player)) {
                AzoxUtils.getInstance().getILiveManager().disableILive(player);
                MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " ILive disabled.");
                return;
            } else {
                MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " Enabling ILive with defaults...");
            }
        }

        AzoxUtils.getInstance().getILiveManager().enableILive(player, mode, damageReduction, foodHeal);

        String reductionPercent = (damageReduction * 20) + "%";
        String modeDisplay = "totem".equals(mode) ? "Totem Mode" : "No-Totem Mode";
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " ILive enabled! (" + modeDisplay + ", " + reductionPercent + " damage reduction, " + (foodHeal ? "food healing" : "no food healing") + ")");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("totem");
            completions.add("nototem");
            completions.add("0");
            completions.add("1");
            completions.add("2");
            completions.add("3");
            completions.add("4");
            completions.add("5");
            completions.add("food");
            completions.add("nofood");
        } else if (args.length == 2) {
            completions.add("totem");
            completions.add("nototem");
            completions.add("0");
            completions.add("1");
            completions.add("2");
            completions.add("3");
            completions.add("4");
            completions.add("5");
            completions.add("food");
            completions.add("nofood");
        }

        String last = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(last))
                .toList();
    }
}
