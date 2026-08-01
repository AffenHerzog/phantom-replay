package de.affenherzog.phantomreplay.record;

import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.replay.KeyFrame;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import de.affenherzog.phantomreplay.util.MUtil;
import de.affenherzog.phantomreplay.util.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RecordingManager {

    private final Plugin plugin;
    private final PluginSettings pluginSettings;
    private final PhantomPlayerManager phantomPlayerManager;
    private final ReplayRepository replayRepository;

    private final RecordingScheduler recordingScheduler;

    public RecordingManager(Plugin plugin, PluginSettings pluginSettings, PhantomPlayerManager phantomPlayerManager, ReplayRepository replayRepository, Map<UUID, Recording> currentActiveRecordings) {
        this.plugin = plugin;
        this.pluginSettings = pluginSettings;
        this.phantomPlayerManager = phantomPlayerManager;
        this.replayRepository = replayRepository;
        this.recordingScheduler = new RecordingScheduler(currentActiveRecordings);
        startScheduler();
    }

    public boolean isRecording(UUID uuid) {
        return recordingScheduler.isRecording(uuid);
    }

    public void markLeftClick(UUID uuid) {
        Recording recording = recordingScheduler.getRecording(uuid);
        if (recording != null) {
            recording.markLeftClick();
        }
    }

    public boolean startRecording(UUID uuid) {

        if (recordingScheduler.isRecording(uuid)) {
            return false;
        }

        Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        Recording recording = new Recording(player, pluginSettings.getMaxTicksRecord(), () -> {
            player.sendMessage(MUtil.parse("<red>Maximale Aufnahmezeit erreicht, Aufnahme wird gespeichert!"));
            saveRecording(uuid);
        });

        recordingScheduler.addRecording(uuid, recording);
        return true;
    }


    public boolean saveRecording(UUID uuid) {

        if (!recordingScheduler.isRecording(uuid)) {
            return false;
        }

        Recording recording = recordingScheduler.removeRecording(uuid);
        recording.addLastFrame();
        List<KeyFrame> keyFrames = recording.getKeyFrames();

        Replay replay = new Replay(Replay.DEFAULT_ID, Replay.DEFAULT_NAME, uuid, keyFrames);

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

    public boolean stopRecording(UUID uuid) {
        return recordingScheduler.removeRecording(uuid) != null;
    }

    private void startScheduler() {
        recordingScheduler.runTaskTimer(plugin, 0, 1);
    }

    private void stopScheduler() {
        recordingScheduler.cancel();
    }

}
