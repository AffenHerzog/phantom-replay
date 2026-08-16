package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.application.ReplayManagementService;
import de.affenherzog.phantomreplay.gui.playback.PlaybackGuiService;
import de.affenherzog.phantomreplay.gui.util.GuiSoundUtil;
import de.affenherzog.phantomreplay.replay.Replay;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static de.affenherzog.phantomreplay.util.MUtil.MM;

@RequiredArgsConstructor
public class RenamePlaybackChatListener implements Listener {

    private final Plugin plugin;
    private final PlaybackGuiService menuService;
    private final ReplayManagementService replayManagementService;

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Component component = event.message();

        if (!replayManagementService.isRenaming(uuid)) {
            return;
        }

        event.setCancelled(true);

        PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
        String newName = plainTextComponentSerializer.serialize(component);

        if (newName.equalsIgnoreCase("abbruch")) {
            replayManagementService.removeRenaming(uuid);
            player.sendMessage(MM.deserialize("<red>Umbenennen abgebrochen.</red>"));
            Bukkit.getScheduler().runTask(plugin, () -> menuService.openPlaybackGui(uuid));
            return;
        }

        Replay replay = replayManagementService.getRenaming(uuid);
        if (replay == null) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            ReplayManagementService.RenameResult renameResult = replayManagementService.renameReplay(replay, uuid, newName);

            if (renameResult != ReplayManagementService.RenameResult.SUCCESS) {
                player.sendMessage(renameResult.getMessageComponent());
                return;
            }

            replayManagementService.removeRenaming(uuid);
            menuService.openPlaybackGui(uuid);
            GuiSoundUtil.playSuccess(player);
            player.sendMessage(renameResult.getMessageComponent(replay.name(), newName));
        });

    }

}
