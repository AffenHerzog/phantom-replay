package de.affenherzog.phantomReplay.replay;

import com.google.gson.annotations.SerializedName;
import de.affenherzog.phantomReplay.replay.action.ReplayAction;

import java.util.List;

public record Frame(@SerializedName("p") Position position, @SerializedName("r") List<ReplayAction> replayActions) {

}
