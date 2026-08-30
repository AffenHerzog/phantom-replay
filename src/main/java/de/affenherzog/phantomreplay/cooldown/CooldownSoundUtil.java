package de.affenherzog.phantomreplay.cooldown;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;

public class CooldownSoundUtil {
    private CooldownSoundUtil() {
        /* This utility class should not be instantiated */
    }

    private static final Sound TICK = Sound.sound(
            org.bukkit.Sound.BLOCK_NOTE_BLOCK_HAT,
            Sound.Source.MASTER,
            0.5f,
            1.5f
    );

    private static final Sound FINISHED = Sound.sound(
            org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL,
            Sound.Source.MASTER,
            0.8f,
            1.0f
    );

    public static void playCooldownTickSound(Audience audience) {
        audience.playSound(TICK);
    }

    public static void playCooldownFinishSound(Audience audience) {
        audience.playSound(FINISHED);
    }

}
