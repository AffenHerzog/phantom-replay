package de.affenherzog.phantomreplay.player;

import de.affenherzog.phantomreplay.replay.Replay;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class PhantomPlayer {

    @EqualsAndHashCode.Include
    private final UUID uuid;

    private final List<Replay> ownedReplays = new ArrayList<>();

    public void addReplay(Replay replay) {
        this.ownedReplays.add(replay);
    }

    public void addReplays(List<Replay> replays) {
        this.ownedReplays.addAll(replays);
    }
}