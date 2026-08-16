package de.affenherzog.phantomreplay.gui.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GuiSoundUtil {
    public static final float DEFAULT_VOLUME = 1.0f;
    public static final float DEFAULT_PITCH = 1.0f;

    private GuiSoundUtil() {
        /* This utility class should not be instantiated */
    }

    public static void playClick(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, DEFAULT_VOLUME, DEFAULT_PITCH);
    }

    public static void playWarning(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, DEFAULT_VOLUME, 1.0f);
    }

    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, DEFAULT_VOLUME, 1.2f);
    }

}