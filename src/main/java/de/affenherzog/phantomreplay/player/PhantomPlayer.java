package de.affenherzog.phantomreplay.player;

import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.replay.Replay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PhantomPlayer {

    private final UUID uuid;

    private final List<Replay> ownedReplays;

    private final PlaybackManager playbackManager;

    public PhantomPlayer(UUID uuid, PlaybackManager playbackManager) {
        this.uuid = uuid;
        this.playbackManager = playbackManager;
        this.ownedReplays = new ArrayList<>();
    }

    public void addReplay(Replay replay) {
        this.ownedReplays.add(replay);
    }

    public void addReplays(List<Replay> replays) {
        this.ownedReplays.addAll(replays);
    }

}
