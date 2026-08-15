package de.affenherzog.phantomreplay.gui.playback;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.affenherzog.phantomreplay.application.ReplayManagementService;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackSessionModel;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PlaybackGuiService {

    private final Plugin plugin;
    private final PlaybackManager playbackManager;
    private final ReplayManagementService replayManagementService;

    private static final int COOLDOWN_TIME_MILLIS = 750;
    public static final int COOLDOWN_TIME_TICKS = COOLDOWN_TIME_MILLIS / 50;

    private final Cache<UUID, Boolean> clickCooldown = CacheBuilder.newBuilder()
            .expireAfterWrite(COOLDOWN_TIME_MILLIS, TimeUnit.MILLISECONDS)
            .build();

    public boolean isOnCooldown(UUID uuid) {
        return clickCooldown.getIfPresent(uuid) != null;
    }

    public void setCooldown(UUID uuid) {
        clickCooldown.put(uuid, true);
    }

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
