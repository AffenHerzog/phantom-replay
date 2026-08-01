package de.affenherzog.phantomReplay.record;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class RecordingScheduler extends BukkitRunnable {

    private final Map<UUID, Recording> currentActiveRecordings;

    public RecordingScheduler(Map<UUID, Recording> currentActiveRecordings) {
        this.currentActiveRecordings = currentActiveRecordings;
    }

    public boolean isRecording(UUID uuid) {
        return currentActiveRecordings.containsKey(uuid);
    }

    public void addRecording(UUID uuid, Recording recording) {
        currentActiveRecordings.put(uuid, recording);
    }

    public Recording removeRecording(UUID uuid) {
        return currentActiveRecordings.remove(uuid);
    }

    public Recording getRecording(UUID uuid) {
        return currentActiveRecordings.get(uuid);
    }

    @Override
    public void run() {
        currentActiveRecordings.forEach((_, recording) -> recording.recordFrame());
    }

}
