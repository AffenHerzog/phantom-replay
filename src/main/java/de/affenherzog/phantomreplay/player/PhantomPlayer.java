package de.affenherzog.phantomreplay.player;

import de.affenherzog.phantomreplay.replay.Replay;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class PhantomPlayer {

    @Getter
    private final UUID uuid;

    private final List<Replay> replays;

    public PhantomPlayer(UUID uuid, List<Replay> replays) {
        this.uuid = uuid;
        this.replays = replays;
    }

    public void addReplay(Replay replay) {
        replays.add(replay);
    }

    public void addReplays(List<Replay> replays) {
        replays.addAll(this.replays);
    }

}
