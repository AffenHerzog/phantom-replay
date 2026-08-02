package de.affenherzog.phantomreplay.playback;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class PlaybackScheduler extends BukkitRunnable {

    private final Map<Integer, PlaybackSessionRunner> playbackSessions;

    public PlaybackScheduler(Map<Integer, PlaybackSessionRunner> playbackSessions) {
        this.playbackSessions = playbackSessions;
    }

    public void addPlaybackSession(PlaybackSessionRunner playbackSessionRunner) {
        int id = playbackSessionRunner.getModel().getReplay().id();
        playbackSessions.put(id, playbackSessionRunner);
    }

    public PlaybackSessionRunner removePlaybackSession(int id) {
        return playbackSessions.remove(id);
    }

    public boolean isPlaying(int id) {
        return playbackSessions.containsKey(id);
    }

    public PlaybackSessionRunner getPlaybackSession(int id) {
        return playbackSessions.get(id);
    }

    @Override
    public void run() {
        playbackSessions.forEach((_, playbackSessionRunner) -> playbackSessionRunner.tick());
    }

}
