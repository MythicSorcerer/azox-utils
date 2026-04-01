package net.azox.utils.command.impl.teleport;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.TeleportRequest;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class TpDeclineCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final Optional<TeleportRequest> requestOpt = plugin.getTeleportManager().getRequest(player);
        if (requestOpt.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>You have no pending teleport requests!");
            return;
        }

        final TeleportRequest request = requestOpt.get();
        if (args.length > 0 && !request.getRequester().getName().equalsIgnoreCase(args[0])) {
            MessageUtil.sendMessage(player, "<red>You have no pending request from " + args[0] + "!");
            return;
        }

        plugin.getTeleportManager().removeRequest(player);
        MessageUtil.sendMessage(player, "<green>Teleport request declined!");
        if (request.getRequester().isOnline()) {
            MessageUtil.sendMessage(request.getRequester(), "<red>" + player.getName() + " declined your teleport request.");
        }
    }
}
