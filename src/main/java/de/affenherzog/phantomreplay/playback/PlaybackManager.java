package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.util.MUtil;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
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
        sessions.put(sessionToAdd.getModel().id(), sessionToAdd);

        if (sessionToAdd.getModel().active()) {
            play(sessionToAdd);
        }
    }

    private PlaybackSessionRunner findSessionByReplayId(int replayId) {
        return sessions.values().stream()
                .filter(session -> session.getModel().replay().id() == replayId)
                .findFirst()
                .orElse(null);
    }

    public Optional<PlaybackSessionModel> getSessionModelByReplayId(int replayId) {
        return Optional.ofNullable(findSessionByReplayId(replayId))
                .map(PlaybackSessionRunner::getModel);
    }

    public void removeAll(UUID ownerUuid) {
        List<Integer> idsToRemove = new ArrayList<>();

        for (PlaybackSessionRunner session : sessions.values()) {
            if (session.getModel().replay().uuid().equals(ownerUuid)) {
                idsToRemove.add(session.getModel().id());
                session.getAnimator().despawnAll();
                stop(session.getModel().replay().id());
            }
        }

        for (Integer id : idsToRemove) {
            sessions.remove(id);
        }
    }

    public void loadReplayIntoSession(int id, Replay replay) {
        PlaybackSessionRunner runner = findSessionByReplayId(id);
        if (runner == null) return;
        runner.setModel(runner.getModel().withReplay(replay));
    }

    public boolean updateActiveSession(int replayId, boolean active) {
        PlaybackSessionRunner runner = findSessionByReplayId(replayId);

        if (runner == null) return false;
        if (active == runner.getModel().active()) return false;

        runner.modifyActive(active);

        if (active) {
            play(runner);
        } else {
            stop(replayId);
        }

        updatePlaybackModelInDatabase(runner.getModel());

        return true;
    }

    public boolean updateVisibilitySession(int replayId, VisibilityScope scope) {
        PlaybackSessionRunner runner = findSessionByReplayId(replayId);

        if (runner == null) return false;
        if (scope == runner.getModel().visibilityScope()) return false;

        runner.modifyVisibility(scope);
        updatePlaybackModelInDatabase(runner.getModel());

        return true;
    }

    public void play(PlaybackSessionRunner playbackSessionRunner) {
        playbackScheduler.addPlaybackSession(playbackSessionRunner);
    }

    public void stop(int id) {
        playbackScheduler.removePlaybackSession(id);
    }

    public void removeSessionByReplayId(int replayId) {
        PlaybackSessionRunner runner = findSessionByReplayId(replayId);
        if (runner == null) return;

        runner.getAnimator().despawnAll();
        stop(replayId);
        sessions.remove(runner.getModel().id());
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
                .map(it -> MUtil.stripe(it.getModel().replay().getUniqueName()))
                .toList();
    }
}
