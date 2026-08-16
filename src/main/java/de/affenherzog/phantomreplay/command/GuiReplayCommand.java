package de.affenherzog.phantomreplay.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import de.affenherzog.phantomreplay.gui.playback.PlaybackGuiService;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class GuiReplayCommand implements PhantomCommand {

    private final PhantomPlayerManager phantomPlayerManager;
    private final PlaybackGuiService playbackGuiService;

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("playback")
                .requires(source -> source.getSender().hasPermission("phantomreplay.gui.playback"))
                .executes(context -> {
                    if (context.getSource().getSender() instanceof Player player) {
                        openReplayGui(player);
                    }
                    return 0;
                }).build();
    }

    private void openReplayGui(Player player) {
        UUID uuid = player.getUniqueId();
        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(_ ->
                playbackGuiService.openPlaybackGui(uuid));

    }

}
