package de.affenherzog.phantomreplay.replay;

import com.google.gson.annotations.SerializedName;

public record KeyFrame(@SerializedName("t") int tick, @SerializedName("f") Frame frame) {
}
