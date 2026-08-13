package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.playback.PlaybackSessionService;
import de.affenherzog.phantomreplay.replay.ReplaySavedEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class PlaybackReplaySavedListener implements Listener {

    private final PlaybackSessionService playbackSessionService;

    @EventHandler
    public void onReplaySaved(ReplaySavedEvent event) {
        playbackSessionService.createAndStartPlayback(event.getReplay(), event.getPlayerUuid());
    }

}
