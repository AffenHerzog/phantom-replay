package de.affenherzog.phantomReplay.record;

import de.affenherzog.phantomReplay.replay.Frame;
import de.affenherzog.phantomReplay.replay.Position;
import de.affenherzog.phantomReplay.replay.action.ReplayAction;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Recording {

    private final Player player;

    @Getter
    private final List<Frame> frames;

    public Recording(Player player) {
        this.player = player;
        this.frames = new ArrayList<>();
    }

    public void recordFrame() {
        Position position;
        List<ReplayAction> replayActions = new ArrayList<>();

        position = recordMovement();

        frames.add(new Frame(position, replayActions));
    }

    private Position recordMovement() {
        return Position.fromBukkitLocation(player.getLocation());
    }


}
