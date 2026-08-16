package de.affenherzog.phantomreplay.playback;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

@RequiredArgsConstructor
public class PlaybackScheduler extends BukkitRunnable {

    private final Map<Integer, PlaybackSessionRunner> playbackSessions;

    public void addPlaybackSession(PlaybackSessionRunner playbackSessionRunner) {
        int id = playbackSessionRunner.getReplayId();
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
