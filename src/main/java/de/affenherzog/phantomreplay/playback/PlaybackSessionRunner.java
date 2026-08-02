package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Frame;
import de.affenherzog.phantomreplay.replay.KeyFrame;
import lombok.Getter;

import java.util.List;

@Getter
public class PlaybackSessionRunner {

    private final PlaybackSessionModel model;

    private int currentTick;
    private int currentFrameIndex;

    public PlaybackSessionRunner(PlaybackSessionModel model) {
        this.model = model;
    }

    public void tick() {
        List<KeyFrame> keyFrames = model.getReplay().keyFrames();

        if (currentTick >= keyFrames.getLast().tick()) {
            currentTick = 0;
            currentFrameIndex = 0;
        }

        while (currentFrameIndex + 1 < keyFrames.size() && currentTick >= keyFrames.get(currentFrameIndex + 1).tick()) {
            currentFrameIndex++;
        }

        Frame currentFrame = keyFrames.get(currentFrameIndex).frame();

        animate(currentFrame);

        currentTick++;
    }

    private void animate(Frame frame) {
        //TODO animate
    }

}
