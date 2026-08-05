package de.affenherzog.phantomreplay.replay.action;

import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;

public record SneakAction(@SerializedName("s") boolean sneaking) implements ReplayAction {

    public static SneakAction capture(Player player) {
        return new SneakAction(player.isSneaking());
    }

}
