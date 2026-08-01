package de.affenherzog.phantomreplay.playback;


import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class PlaybackScheduler extends BukkitRunnable {

    private final Map<Integer, PlaybackSession> playbackSessions;

    public PlaybackScheduler(Map<Integer, PlaybackSession> playbackSessions) {
        this.playbackSessions = playbackSessions;
    }

    public void addPlaybackSession(PlaybackSession playbackSession) {
        int id = playbackSession.getReplay().id();
        playbackSessions.put(id, playbackSession);
    }

    public PlaybackSession removePlaybackSession(int id) {
        return playbackSessions.remove(id);
    }

    public boolean isPlaying(int id) {
        return playbackSessions.containsKey(id);
    }

    @Override
    public void run() {
        playbackSessions.forEach((_, playbackSession) -> playbackSession.tick());
    }

}
