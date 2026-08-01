package de.affenherzog.phantomreplay.record;

import de.affenherzog.phantomreplay.replay.Frame;
import de.affenherzog.phantomreplay.replay.KeyFrame;
import de.affenherzog.phantomreplay.replay.Position;
import de.affenherzog.phantomreplay.replay.action.*;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Recording {

    private final Player player;
    private final int maxFrames;
    private final Runnable onLimitReached;

    @Getter
    private final List<KeyFrame> keyFrames;

    private int frameCount = 0;
    private boolean leftClickedThisTick = false;

    public Recording(Player player, int maxFrames, Runnable onLimitReached) {
        this.player = player;
        this.maxFrames = maxFrames;
        this.onLimitReached = onLimitReached;
        this.keyFrames = new ArrayList<>();
    }

    public void markLeftClick() {
        leftClickedThisTick = true;
    }

    public Frame buildFrame() {
        return new Frame(recordMovement(), recordActions());
    }

    public void recordFrame() {
        frameCount++;

        if (frameCount >= maxFrames) {
            onLimitReached.run();
            return;
        }

        Frame currentFrame = buildFrame();

        if (!keyFrames.isEmpty()) {
            KeyFrame lastKeyFrame = keyFrames.getLast();
            if (lastKeyFrame.frame().equals(currentFrame)) {
                return;
            }
        }
        keyFrames.add(new KeyFrame(frameCount, currentFrame));
    }

    public void addLastFrame() {
        if (keyFrames.isEmpty()) return;

        if (keyFrames.getLast().tick() == frameCount) {
            return;
        }

        Frame lastFrame = buildFrame();
        keyFrames.add(new KeyFrame(++frameCount, lastFrame));
    }

    private Position recordMovement() {
        return Position.fromBukkitLocationRounded(player.getLocation());
    }

    private List<ReplayAction> recordActions() {
        List<ReplayAction> actions = new ArrayList<>();
        addNonNullAction(actions, SneakAction.capture(player));
        addNonNullAction(actions, SprintAction.capture(player));
        addNonNullAction(actions, ShowItemAction.capture(player));
        addNonNullAction(actions, LeftClickAction.capture(leftClickedThisTick));
        leftClickedThisTick = false;
        return actions;
    }

    private void addNonNullAction(List<ReplayAction> actions, ReplayAction action) {
        if (action != null) {
            actions.add(action);
        }
    }

}
