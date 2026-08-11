package de.affenherzog.phantomreplay.record;

import de.affenherzog.phantomreplay.events.ReplaySavedEvent;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.replay.KeyFrame;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import de.affenherzog.phantomreplay.util.MUtil;
import de.affenherzog.phantomreplay.util.PluginSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class RecordingManager {

    private final Plugin plugin;
    private final PluginSettings pluginSettings;
    private final PhantomPlayerManager phantomPlayerManager;
    private final ReplayRepository replayRepository;

    @Getter
    private final RecordingScheduler recordingScheduler;

    public boolean isRecording(UUID uuid) {
        return recordingScheduler.isRecording(uuid);
    }

    public void markLeftClick(UUID uuid) {
        if (recordingScheduler.isRecording(uuid)) {
            Recording recording = recordingScheduler.getRecording(uuid);
            if (recording != null) {
                recording.setLeftClickThisTick();
            }
        }
    }

    public boolean startRecording(UUID uuid) {
        if (recordingScheduler.isRecording(uuid)) {
            return false;
        }
        recordingScheduler.addRecording(uuid, createRecording(uuid));
        return true;
    }

    private @NotNull Recording createRecording(UUID uuid) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        return new Recording(player, pluginSettings.getMaxTicksRecord(), () -> {
            player.sendMessage(MUtil.parse("<red>Maximale Aufnahmezeit erreicht, Aufnahme wird gespeichert!"));
            saveRecording(uuid);
        });
    }

    public boolean saveRecording(UUID uuid) {
        Recording recording = recordingScheduler.removeRecording(uuid);

        if (recording == null) {
            return false;
        }

        recording.addLastFrame();
        List<KeyFrame> keyFrames = recording.getKeyFrames();
        saveReplay(uuid, keyFrames);

        return true;
    }

    private void saveReplay(UUID uuid, List<KeyFrame> keyFrames) {
        Replay replay = new Replay(Replay.DEFAULT_ID, Replay.DEFAULT_NAME, uuid, keyFrames);

        replayRepository.saveReplay(replay).whenComplete((savedReplay, exception) -> {

            if (exception != null) {
                plugin.getSLF4JLogger().error("Fehler beim Speichern des Replays: {}", exception.getMessage());
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                phantomPlayerManager.getPhantomPlayer(uuid).
                        ifPresent(currentPlayer -> currentPlayer.addReplay(savedReplay));
                Bukkit.getPluginManager().callEvent(new ReplaySavedEvent(uuid, savedReplay));
            });

        });
    }

    public boolean stopRecording(UUID uuid) {
        return recordingScheduler.removeRecording(uuid) != null;
    }
}
