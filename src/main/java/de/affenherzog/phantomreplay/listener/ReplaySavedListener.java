package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.events.ReplaySavedEvent;
import de.affenherzog.phantomreplay.playback.*;
import de.affenherzog.phantomreplay.replay.Replay;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ReplaySavedListener implements Listener {

    private final Plugin plugin;
    private final PlaybackRepository playbackRepository;
    private final PlaybackManager playbackManager;

    public ReplaySavedListener(Plugin plugin, PlaybackRepository playbackRepository, PlaybackManager playbackManager) {
        this.plugin = plugin;
        this.playbackRepository = playbackRepository;
        this.playbackManager = playbackManager;
    }

    @EventHandler
    public void onReplaySaved(ReplaySavedEvent event) {
        final Replay replay = event.getReplay();

        PlaybackSessionModel defaultSession = new PlaybackSessionModel(
                -1,
                replay,
                PlaybackSessionModel.ACTIVE_DEFAULT,
                PlaybackSessionModel.VISIBILITY_SCOPE_DEFAULT
        );

        playbackRepository.savePlaybackSession(defaultSession).thenAccept(savedModel ->
                Bukkit.getScheduler().runTask(plugin, () -> playbackManager.addSession(new PlaybackSessionRunner(savedModel, event.getPlayerUuid()))));
    }

}
