package de.affenherzog.phantomReplay.replay.action;

import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;

public record SprintAction(@SerializedName("sp") boolean isSprinting) implements ReplayAction {

    public static SprintAction capture(Player player) {
        return new SprintAction(player.isSprinting());
    }

}
