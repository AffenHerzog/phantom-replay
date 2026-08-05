package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.record.RecordingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final PhantomPlayerManager phantomPlayerManager;
    private final RecordingManager recordingManager;
    private final PlaybackManager playbackManager;

    public PlayerQuitListener(PhantomPlayerManager phantomPlayerManager, RecordingManager recordingManager, PlaybackManager playbackManager) {
        this.phantomPlayerManager = phantomPlayerManager;
        this.recordingManager = recordingManager;
        this.playbackManager = playbackManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        phantomPlayerManager.getPhantomPlayer(uuid)
                .ifPresent((_ -> phantomPlayerManager.removePhantomPlayer(uuid)));
        recordingManager.stopRecording(uuid);
        playbackManager.removeAll(uuid);
    }


}
