package de.affenherzog.phantomReplay.replay;

import de.affenherzog.phantomReplay.replay.action.ReplayAction;

import java.util.List;

public record Frame(Position position, List<ReplayAction> replayActions) {

}
