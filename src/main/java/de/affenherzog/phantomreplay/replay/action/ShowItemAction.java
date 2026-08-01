package de.affenherzog.phantomreplay.replay.action;

import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record ShowItemAction(@SerializedName("m") String material) implements ReplayAction {

    public static @Nullable ShowItemAction capture(Player player) {
        if (player.getInventory().getItemInMainHand().getType().isAir()) return null;
        return new ShowItemAction(player.getInventory().getItemInMainHand().getType().name());
    }

}
