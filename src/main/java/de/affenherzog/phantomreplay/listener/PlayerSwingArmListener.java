package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.record.RecordingManager;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PlayerSwingArmListener implements Listener {

    private final RecordingManager recordingManager;

    public PlayerSwingArmListener(RecordingManager recordingManager) {
        this.recordingManager = recordingManager;
    }

    @EventHandler
    public void onSwingArm(PlayerArmSwingEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        if (!recordingManager.isRecording(uuid)) {
            return;
        }

        recordingManager.markLeftClick(uuid);
    }

}
