package de.affenherzog.phantomReplay.record;

import de.affenherzog.phantomReplay.replay.Frame;
import de.affenherzog.phantomReplay.replay.KeyFrame;
import de.affenherzog.phantomReplay.replay.Position;
import de.affenherzog.phantomReplay.replay.action.ReplayAction;
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

    public Recording(Player player, int maxFrames, Runnable onLimitReached) {
        this.player = player;
        this.maxFrames = maxFrames;
        this.onLimitReached = onLimitReached;
        this.keyFrames = new ArrayList<>();
    }

    public void recordFrame() {
        frameCount++;

        if (frameCount >= maxFrames) {
            onLimitReached.run();
            return;
        }

        Position position;
        List<ReplayAction> replayActions = new ArrayList<>();

        position = recordMovement();

        Frame currentFrame = new Frame(position, replayActions);

        if (!keyFrames.isEmpty()) {
            KeyFrame lastKeyFrame = keyFrames.getLast();
            if (lastKeyFrame.frame().equals(currentFrame)) {
                return;
            }
        }
        keyFrames.add(new KeyFrame(frameCount, currentFrame));
    }

    private Position recordMovement() {
        return Position.fromBukkitLocationRounded(player.getLocation());
    }


}
