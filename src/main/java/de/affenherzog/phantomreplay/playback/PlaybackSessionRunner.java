package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Frame;
import de.affenherzog.phantomreplay.replay.KeyFrame;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
public class PlaybackSessionRunner {

    @Setter
    private PlaybackSessionModel model;
    private final ReplayAnimator animator;

    private int currentTick;
    private int currentFrameIndex;

    private Frame currentFrame;

    public PlaybackSessionRunner(PlaybackSessionModel model, UUID ownerUUID) {
        this.model = model;
        this.animator = new ReplayAnimator(model.visibilityScope(), ownerUUID);
    }

    public void modifyActive(boolean active) {
        model = model.withActive(active);

        if (!active) {
            currentTick = 0;
            currentFrameIndex = 0;
            animator.despawnAll();
        }
    }

    public void modifyVisibility(VisibilityScope scope) {
        model = model.withVisibilityScope(scope);
        animator.setScope(scope);
    }

    public void tick() {
        List<KeyFrame> keyFrames = model.replay().keyFrames();

        if (currentTick >= keyFrames.getLast().tick()) {
            currentTick = 0;
            currentFrameIndex = 0;
        }

        while (currentFrameIndex + 1 < keyFrames.size() && currentTick >= keyFrames.get(currentFrameIndex + 1).tick()) {
            currentFrameIndex++;
        }

        currentFrame = keyFrames.get(currentFrameIndex).frame();

        animator.playFrame(currentFrame);

        currentTick++;
    }
}
