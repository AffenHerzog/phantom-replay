package de.affenherzog.phantomreplay.gui.util;


import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;

public class GuiSoundUtil {

    public static final float DEFAULT_VOLUME = 1.0f;
    public static final float DEFAULT_PITCH = 1.0f;

    private GuiSoundUtil() {
        /* This utility class should not be instantiated */
    }


    private static final Sound CLICK = Sound.sound(
            org.bukkit.Sound.UI_BUTTON_CLICK,
            Sound.Source.MASTER,
            DEFAULT_VOLUME,
            DEFAULT_PITCH
    );

    private static final Sound WARNING = Sound.sound(
            org.bukkit.Sound.ENTITY_VILLAGER_NO,
            Sound.Source.MASTER,
            DEFAULT_VOLUME,
            DEFAULT_PITCH
    );

    private static final Sound SUCCESS = Sound.sound(
            org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
            Sound.Source.MASTER,
            DEFAULT_VOLUME,
            1.2f
    );

    public static void playClick(Audience audience) {
        audience.playSound(CLICK);
    }

    public static void playWarning(Audience audience) {
        audience.playSound(WARNING);
    }

    public static void playSuccess(Audience audience) {
        audience.playSound(SUCCESS);
    }

}