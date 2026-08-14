package de.affenherzog.phantomreplay.gui.util;

import de.affenherzog.phantomreplay.gui.item.PhantomGuiItem;

import java.util.HashMap;
import java.util.Map;

public class GuiFillerUtil {

    private GuiFillerUtil() {
        /* This utility class should not be instantiated */
    }

    public static Map<Integer, PhantomGuiItem> fillBorder(int size, PhantomGuiItem fillerItem) {
        Map<Integer, PhantomGuiItem> filler = new HashMap<>();

        for (int i = 0; i < size; i++) {
            if (i < 9 || i % 9 == 0 || (i + 1) % 9 == 0 || i > (size - 9)) {
                filler.put(i, fillerItem);
            }
        }

        return filler;
    }

    public static Map<Integer, PhantomGuiItem> fill(int size, PhantomGuiItem fillerItem) {
        Map<Integer, PhantomGuiItem> filler = new HashMap<>();

        for (int i = 0; i < size; i++) {
            filler.put(i, fillerItem);
        }

        return filler;
    }

}
