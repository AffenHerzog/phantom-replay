package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.util.MUtil;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaybackManager {

    private final Plugin plugin;
    private final PlaybackScheduler playbackScheduler;
    private final PlaybackRepository playbackRepository;

    @Getter
    private final Map<Integer, PlaybackSessionRunner> sessions = new ConcurrentHashMap<>();

    public PlaybackManager(Plugin plugin, PlaybackRepository playbackRepository) {
        this.plugin = plugin;
        this.playbackRepository = playbackRepository;
        this.playbackScheduler = new PlaybackScheduler(new ConcurrentHashMap<>());

        startScheduler();
    }

    public void addSessions(List<PlaybackSessionRunner> sessionsToAdd) {
        sessionsToAdd.forEach(this::addSession);
    }

    public void addSession(PlaybackSessionRunner sessionToAdd) {
        sessions.put(sessionToAdd.getModel().getId(), sessionToAdd);

        if (sessionToAdd.getModel().isActive()) {
            play(sessionToAdd);
        }
    }

    public void removeAll(UUID ownerUuid) {
        sessions.values().removeIf(session -> {
            boolean belongsToPlayer = session.getModel().getReplay().uuid().equals(ownerUuid);
            if (belongsToPlayer) {
                stop(session.getModel().getId());
            }
            return belongsToPlayer;
        });
    }

    public void updateActiveSession(int id, boolean active) {
        // Statt Schleife: Direkter Zugriff!
        PlaybackSessionRunner runner = sessions.get(id);

        if (runner != null) {
            runner.getModel().setActive(active);

            if (active) {
                play(runner);
            } else {
                stop(id);
            }

            updatePlaybackModelInDatabase(runner.getModel());
        }
    }

    public void updateVisibilitySession(int id, VisibilityScope scope) {
        PlaybackSessionRunner runner = sessions.get(id);

        if (runner != null) {
            runner.getModel().setVisibilityScope(scope);
            updatePlaybackModelInDatabase(runner.getModel());
        }
    }

    public void play(PlaybackSessionRunner playbackSessionRunner) {
        playbackScheduler.addPlaybackSession(playbackSessionRunner);
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

    public void updatePlaybackModelInDatabase(PlaybackSessionModel model) {
        playbackRepository.updatePlaybackSession(model);
    }

    public List<String> getUniqueSessionNames() {
        return sessions.values().stream()
                .map(it -> MUtil.stripe(it.getModel().getReplay().getUniqueName()))
                .toList();
    }
}