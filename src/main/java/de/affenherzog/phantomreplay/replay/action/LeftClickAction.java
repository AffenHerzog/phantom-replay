package de.affenherzog.phantomreplay.replay.action;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public record LeftClickAction(@SerializedName("c") boolean clicked) implements ReplayAction {

    public static @Nullable LeftClickAction capture(boolean isLeftClick) {
        if (isLeftClick) return new LeftClickAction(true);
        return null;
    }

}
