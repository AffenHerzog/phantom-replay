package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Replay;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaybackSession {

    private Replay replay;
    private VisibilityScope visibilityScope;
    private boolean active;

    private int currentTick;

    public PlaybackSession(Replay replay, VisibilityScope visibilityScope, boolean active) {
        this.replay = replay;
        this.visibilityScope = visibilityScope;
        this.active = active;
    }

    public void tick() {
        currentTick++;

        if (currentTick >= replay.keyFrames().getLast().tick()) {
            currentTick = 0;
        }

        animate();
    }

    private void animate() {

    }

}
