package net.azox.utils.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public static final String ICON_HOME = "⧈";
    public static final String ICON_WARP = "☀";
    public static final String ICON_TP = "⚡";
    public static final String ICON_SUCCESS = "✔";
    public static final String ICON_ERROR = "❌";
    public static final String ICON_INFO = "ℹ";
    public static final String ICON_WARNING = "⚠";
    public static final String ICON_DEATH = "☠";
    public static final String ICON_UTILITY = "⛏";
    public static final String ICON_NEXT = "▶";
    public static final String ICON_PREV = "◀";
    public static final String ICON_ARROW = "→";
    public static final String ICON_STAR = "⭐";
    public static final String ICON_HEART = "❤";

    public static Component parse(final String message) {
        if (message == null) return Component.empty();
        return MINI_MESSAGE.deserialize(message);
    }

    public static List<Component> parse(final List<String> messages) {
        return messages.stream().map(MessageUtil::parse).collect(Collectors.toList());
    }

    public static void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(parse(message));
    }

    public static void sendMessage(final Player player, final String message) {
        player.sendMessage(parse(message));
    }

    public static String serialize(final Component component) {
        return MINI_MESSAGE.serialize(component);
    }
    
    public static String toLegacy(final String message) {
        return LEGACY_SERIALIZER.serialize(parse(message));
    }
}
