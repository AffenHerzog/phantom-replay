package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Replay;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@RequiredArgsConstructor
public class PlaybackSessionService {

    private final Plugin plugin;
    private final PlaybackRepository playbackRepository;
    private final PlaybackManager playbackManager;

    public void createAndStartPlayback(Replay replay, UUID uuid) {
        PlaybackSessionModel defaultSession = new PlaybackSessionModel(
                -1,
                replay,
                PlaybackSessionModel.ACTIVE_DEFAULT,
                PlaybackSessionModel.VISIBILITY_SCOPE_DEFAULT
        );

        playbackRepository.savePlaybackSession(defaultSession)
                .thenAccept(savedModel -> startPlaybackSession(savedModel, uuid));
    }

    private void startPlaybackSession(PlaybackSessionModel savedModel, UUID uuid) {
        Bukkit.getScheduler().runTask(plugin,
                () -> playbackManager.registerSession(new PlaybackSessionRunner(savedModel, uuid)));
    }

}
