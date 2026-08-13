package de.affenherzog.phantomreplay.application;

import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.player.PhantomPlayer;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ReplayManagementService {

    private final PhantomPlayerManager phantomPlayerManager;
    private final PlaybackManager playbackManager;
    private final ReplayRepository replayRepository;

    public Optional<Replay> renameReplay(Replay replay, UUID uuid, String newName) {
        String replayName = replay.name();
        PhantomPlayer player = phantomPlayerManager.getPhantomPlayer(uuid).orElseThrow();
        Replay newReplay = replay.withName(newName);

        boolean nameExists = player.getOwnedReplays().stream().anyMatch(it ->
                it.getUniqueName().equals(newReplay.getUniqueName()));

        if (nameExists) {
            return Optional.empty();
        }

        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(it -> {
            it.getOwnedReplays().removeIf(old -> old.getUniqueName().equals(replayName));
            it.getOwnedReplays().add(newReplay);
        });
        playbackManager.loadReplayIntoSession(newReplay.id(), newReplay);
        replayRepository.updateReplayName(newReplay.id(), newReplay.name());

        return Optional.of(newReplay);
    }


    public void deleteReplay(Replay replay, UUID uuid) {
        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(it ->
                it.getOwnedReplays().removeIf(old -> old.id() == replay.id())
        );
        playbackManager.removeSessionByReplayId(replay.id());
        replayRepository.deleteReplay(replay.id());
    }
}
