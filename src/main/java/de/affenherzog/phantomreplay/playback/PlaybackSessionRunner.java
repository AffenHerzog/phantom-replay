package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Frame;
import de.affenherzog.phantomreplay.replay.KeyFrame;
import de.affenherzog.phantomreplay.replay.Replay;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class PlaybackSessionRunner {

    @Getter
    private PlaybackSessionModel model;
    private final ReplayAnimator animator;

    private int currentTick;
    private int currentFrameIndex;

    public boolean isActive() {
        return model.active();
    }

    public VisibilityScope getVisibilityScope() {
        return model.visibilityScope();
    }

    public int getReplayId() {
        return model.replay().id();
    }

    public UUID getOwnerUUID() {
        return model.replay().uuid();
    }

    public String getUniqueReplayName() {
        return model.replay().getUniqueName();
    }

    public void updateReplay(Replay replay) {
        this.model = this.model.withReplay(replay);
    }

    public PlaybackSessionRunner(PlaybackSessionModel model, UUID ownerUUID) {
        this.model = model;
        this.animator = new ReplayAnimator(ownerUUID, model.visibilityScope());
    }

    public void modifyActive(boolean active) {
        model = model.withActive(active);

        if (!active) {
            currentTick = 0;
            currentFrameIndex = 0;
            despawn();
        }
    }

    public void modifyVisibility(VisibilityScope scope) {
        model = model.withVisibilityScope(scope);
        animator.setScope(scope);
    }

    public void despawn() {
        animator.despawnAll();
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

        Frame currentFrame = keyFrames.get(currentFrameIndex).frame();

        animator.playFrame(currentFrame);

        currentTick++;
    }
}
