package de.affenherzog.phantomreplay.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import de.affenherzog.phantomreplay.gui.playback.PlaybackGui;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class GuiReplayCommand implements PhantomCommand {

    private final Plugin plugin;

    private final PlaybackManager playbackManager;
    private final PhantomPlayerManager phantomPlayerManager;

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
        phantomPlayerManager.getPhantomPlayer(player.getUniqueId()).ifPresent(_ ->
                new PlaybackGui(plugin, player.getUniqueId(), playbackManager).open());

    }

}
