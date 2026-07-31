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

    @Getter
    private final List<KeyFrame> keyFrames;

    private int frameCount = 0;

    public Recording(Player player) {
        this.player = player;
        this.keyFrames = new ArrayList<>();
    }

    public void recordFrame() {
        frameCount++;

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
        return Position.fromBukkitLocation(player.getLocation());
    }


}
