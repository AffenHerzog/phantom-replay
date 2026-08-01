package de.affenherzog.phantomreplay.replay;

import com.google.gson.annotations.SerializedName;
import de.affenherzog.phantomreplay.replay.action.ReplayAction;

import java.util.List;

public record Frame(@SerializedName("p") Position position, @SerializedName("r") List<ReplayAction> replayActions) {

}
