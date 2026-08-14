package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.util.MUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class PlaybackManager {

    private final PlaybackScheduler playbackScheduler;
    private final PlaybackRepository playbackRepository;

    private final Map<Integer, PlaybackSessionRunner> sessions = new ConcurrentHashMap<>();

    public void registerSessions(List<PlaybackSessionRunner> sessionsToAdd) {
        sessionsToAdd.forEach(this::registerSession);
    }

    public void registerSession(PlaybackSessionRunner sessionToAdd) {
        sessions.put(sessionToAdd.getReplayId(), sessionToAdd);

        if (sessionToAdd.isActive()) {
            playbackScheduler.addPlaybackSession(sessionToAdd);
        }
    }

    public List<PlaybackSessionModel> getSessions(UUID ownerUuid) {
        return sessions.values().stream()
                .filter(it -> it.getOwnerUUID().equals(ownerUuid))
                .map(PlaybackSessionRunner::getModel)
                .toList();
    }

    public Optional<PlaybackSessionModel> findSessionModelByReplayId(int replayId) {
        return Optional.ofNullable(findSessionByReplayId(replayId)).map(PlaybackSessionRunner::getModel);
    }

    private PlaybackSessionRunner findSessionByReplayId(int replayId) {
        return sessions.get(replayId);
    }

    public void removeAllSessions(UUID ownerUuid) {
        List<Integer> idsToRemove = new ArrayList<>();

        sessions.values().forEach(session -> {
            if (session.getOwnerUUID().equals(ownerUuid)) {
                idsToRemove.add(session.getReplayId());
                session.despawn();
                playbackScheduler.removePlaybackSession(session.getReplayId());
            }
        });

        idsToRemove.forEach(sessions::remove);
    }

    public void loadReplayIntoSession(int id, Replay replay) {
        PlaybackSessionRunner runner = findSessionByReplayId(id);
        if (runner == null) return;
        runner.updateReplay(replay);
    }

    public boolean updateActiveSession(UUID ownerUuid, boolean active) {
        return sessions.values().stream()
                .filter(it -> it.getOwnerUUID().equals(ownerUuid))
                .map(PlaybackSessionRunner::getModel)
                .map(model -> updateActiveSession(model.replay().id(), active))
                .reduce(false, (a, b) -> a || b);
    }

    public boolean updateActiveSession(int replayId, boolean active) {
        PlaybackSessionRunner runner = findSessionByReplayId(replayId);

        if (runner == null) return false;
        if (active == runner.isActive()) return false;

        runner.modifyActive(active);

        if (active) {
            playbackScheduler.addPlaybackSession(runner);
        } else {
            playbackScheduler.removePlaybackSession(replayId);
        }

        updatePlaybackModelInDatabase(runner.getModel());

        return true;
    }

    public boolean updateVisibilitySession(UUID ownerUuid, VisibilityScope scope) {
        return sessions.values().stream()
                .filter(it -> it.getOwnerUUID().equals(ownerUuid))
                .map(PlaybackSessionRunner::getModel)
                .map(model -> updateVisibilitySession(model.replay().id(), scope))
                .reduce(false, (a, b) -> a || b);
    }

    public boolean updateVisibilitySession(int replayId, VisibilityScope scope) {
        PlaybackSessionRunner runner = findSessionByReplayId(replayId);

        if (runner == null) return false;
        if (scope == runner.getVisibilityScope()) return false;

        runner.modifyVisibility(scope);
        updatePlaybackModelInDatabase(runner.getModel());

        return true;
    }

    public void removeSessionByReplayId(int replayId) {
        PlaybackSessionRunner runner = findSessionByReplayId(replayId);
        if (runner == null) return;

        runner.despawn();
        playbackScheduler.removePlaybackSession(replayId);
        sessions.remove(runner.getReplayId());
    }

    public void updatePlaybackModelInDatabase(PlaybackSessionModel model) {
        playbackRepository.updatePlaybackSession(model);
    }

    public List<String> getUniqueSessionNames() {
        return sessions.values().stream()
                .map(it -> MUtil.stripe(it.getUniqueReplayName()))
                .toList();
    }

    public PlaybackStats getPlayerPlaybackStats(UUID ownerUuid) {
        int totalCount = 0;
        int privateCount = 0;
        int globalCount = 0;
        int activeCount = 0;
        int inactiveCount = 0;

        for (PlaybackSessionRunner session : sessions.values()) {
            if (session.getOwnerUUID().equals(ownerUuid)) {
                totalCount++;
                if (session.getModel().visibilityScope() == VisibilityScope.PRIVAT) {
                    privateCount++;
                } else {
                    globalCount++;
                }
                if (session.isActive()) {
                    activeCount++;
                } else {
                    inactiveCount++;
                }
            }
        }
        return new PlaybackStats(totalCount, privateCount, globalCount, activeCount, inactiveCount);
    }
}
