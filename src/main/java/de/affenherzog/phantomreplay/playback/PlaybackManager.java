package de.affenherzog.phantomreplay.playback;

import org.bukkit.plugin.Plugin;

import java.util.Map;

public class PlaybackManager {

    private final Plugin plugin;
    private final PlaybackScheduler playbackScheduler;

    public PlaybackManager(Plugin plugin, Map<Integer, PlaybackSession> playbackSessions) {
        this.plugin = plugin;
        this.playbackScheduler = new PlaybackScheduler(playbackSessions);
    }

    public void play(PlaybackSession playbackSession) {
        playbackScheduler.addPlaybackSession(playbackSession);
    }

    public void stop(int id) {
        playbackScheduler.removePlaybackSession(id);
    }

    public void startScheduler() {
        playbackScheduler.runTaskTimer(plugin, 0, 1);
    }

    public void stopScheduler() {
        playbackScheduler.cancel();
    }


}
