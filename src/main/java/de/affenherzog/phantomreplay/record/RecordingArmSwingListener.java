package de.affenherzog.phantomreplay.record;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

@RequiredArgsConstructor
public class RecordingArmSwingListener implements Listener {

    private final RecordingManager recordingManager;

    @EventHandler
    public void onSwingArm(PlayerArmSwingEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        if (!recordingManager.isRecording(uuid)) {
            return;
        }

        recordingManager.markLeftClick(uuid);
    }

}
