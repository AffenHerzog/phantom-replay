package de.affenherzog.phantomReplay.record;

import de.affenherzog.phantomReplay.player.PhantomPlayerManager;
import de.affenherzog.phantomReplay.replay.Frame;
import de.affenherzog.phantomReplay.replay.Replay;
import de.affenherzog.phantomReplay.replay.ReplayRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecordingManager {

    private final Plugin plugin;
    private final PhantomPlayerManager phantomPlayerManager;
    private final ReplayRepository replayRepository;

    private final RecordingScheduler recordingScheduler;

    public RecordingManager(Plugin plugin, PhantomPlayerManager phantomPlayerManager, ReplayRepository replayRepository, Map<UUID, Recording> currentActiveRecordings) {
        this.plugin = plugin;
        this.phantomPlayerManager = phantomPlayerManager;
        this.replayRepository = replayRepository;
        this.recordingScheduler = new RecordingScheduler(currentActiveRecordings);
        startScheduler();
    }


    public boolean startRecording(UUID uuid) {

        if (recordingScheduler.isRecording(uuid)) {
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        Recording recording = new Recording(player);
        recordingScheduler.addRecording(uuid, recording);
        return true;
    }


    public boolean saveRecording(UUID uuid) {

        if (!recordingScheduler.isRecording(uuid)) {
            return false;
        }

        Recording recording = recordingScheduler.removeRecording(uuid);
        List<Frame> frames = recording.getFrames();

        Replay replay = new Replay(Replay.DEFAULT_ID, Replay.DEFAULT_NAME, uuid, frames);

        replayRepository.saveReplay(replay).whenComplete((savedReplay, exception) -> {

            if (exception != null) {
                plugin.getSLF4JLogger().error("Fehler beim Speichern des Replays: {}", exception.getMessage());
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> phantomPlayerManager.getPhantomPlayer(uuid).
                    ifPresent(currentPlayer -> currentPlayer.addReplay(savedReplay)));
        });
        return true;
    }

    private void startScheduler() {
        recordingScheduler.runTaskTimer(plugin, 0, 1);
    }

    private void stopScheduler() {
        recordingScheduler.cancel();
    }

}
