package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class RulesCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        // In a real plugin, these would come from config.
        final List<String> rules = List.of(
                "<gold>1. <white>Be respectful to all players.",
                "<gold>2. <white>No griefing or stealing.",
                "<gold>3. <white>No hacking or cheating.",
                "<gold>4. <white>No spamming or excessive caps.",
                "<gold>5. <white>Follow staff instructions."
        );

        MessageUtil.sendMessage(sender, "<gold><bold>SERVER RULES</bold>");
        for (final String rule : rules) {
            MessageUtil.sendMessage(sender, rule);
        }
    }
}
