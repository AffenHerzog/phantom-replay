package de.affenherzog.phantomreplay.playback;

import de.affenherzog.phantomreplay.replay.Replay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlaybackSessionModel {
    private final int id;
    private Replay replay;
    private boolean active;
    private VisibilityScope visibilityScope;

    public static boolean ACTIVE_DEFAULT = true;
    public static VisibilityScope VISIBILITY_SCOPE_DEFAULT = VisibilityScope.GLOBAL;

}