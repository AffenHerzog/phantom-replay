package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.application.ReplayManagementService;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackSessionModel;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@RequiredArgsConstructor
public class PlaybackGuiService {

    private final Plugin plugin;
    private final PlaybackManager playbackManager;
    private final ReplayManagementService replayManagementService;

    public void openPlaybackGui(UUID playerUUID) {
        new PlaybackGui(plugin, playerUUID, playbackManager, this).open();
    }

    public void openPlaybackActiveGui(UUID playerUUID, PlaybackStats playbackStats) {
        new PlaybackActiveGui(plugin, playerUUID, playbackStats, playbackManager, this).open();
    }

    public void openPlaybackVisibilityGui(UUID playerUUID, PlaybackStats playbackStats) {
        new PlaybackVisibilityGui(plugin, playerUUID, playbackStats, playbackManager, this).open();
    }

    public void openPlaybackDetailedGui(UUID playerUUID, PlaybackSessionModel session) {
        new PlaybackDetailedGui(plugin, playerUUID, session, playbackManager, replayManagementService, this).open();
    }

}
