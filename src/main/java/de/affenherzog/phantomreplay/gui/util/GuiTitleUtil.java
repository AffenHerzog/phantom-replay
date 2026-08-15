package de.affenherzog.phantomreplay.gui.util;

import net.kyori.adventure.text.Component;

public class GuiTitleUtil {

    private GuiTitleUtil() {
        /* This utility class should not be instantiated */
    }

    public static Component centerTitle(Component titleComponent, String rawText) {
        int textPixelWidth = rawText.length() * 7;
        int paddingPixels = 80 - (textPixelWidth / 2);
        int spacesNeeded = Math.max(0, paddingPixels / 4);
        String spaces = " ".repeat(spacesNeeded);
        return Component.text(spaces).append(titleComponent);
    }
}