package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Replay;

public record PlaybackSessionModel(int id, Replay replay, boolean active, VisibilityScope visibilityScope) {

    public PlaybackSessionModel withActive(boolean active) {
        return new PlaybackSessionModel(id, replay, active, visibilityScope);
    }

    public PlaybackSessionModel withVisibilityScope(VisibilityScope scope) {
        return new PlaybackSessionModel(id, replay, active, scope);
    }

    public PlaybackSessionModel withReplay(Replay replay) {
        return new PlaybackSessionModel(id, replay, active, visibilityScope);
    }

    public static boolean ACTIVE_DEFAULT = true;
    public static VisibilityScope VISIBILITY_SCOPE_DEFAULT = VisibilityScope.GLOBAL;

}