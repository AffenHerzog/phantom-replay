package de.affenherzog.phantomreplay.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MUtil {

    public static final MiniMessage MM = MiniMessage.builder()
            .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
            .build();

    private MUtil() {
    }

    public static Component parse(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return MM.deserialize(text);
    }

    public static String stripe(String text) {
        return MM.stripTags(text);
    }

}
