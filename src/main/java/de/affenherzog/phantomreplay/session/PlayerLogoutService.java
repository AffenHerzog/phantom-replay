package de.affenherzog.phantomreplay.session;

import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.record.RecordingManager;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerLogoutService {

    private final PhantomPlayerManager phantomPlayerManager;
    private final RecordingManager recordingManager;
    private final PlaybackManager playbackManager;

    public void logout(UUID uuid) {
        recordingManager.stopRecording(uuid);
        playbackManager.removeAll(uuid);
        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(
                (_ -> phantomPlayerManager.removePhantomPlayer(uuid)));
    }

}
