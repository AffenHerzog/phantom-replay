package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import de.affenherzog.phantomreplay.gui.util.GuiFillerUtil;
import de.affenherzog.phantomreplay.gui.util.GuiSoundUtil;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.buildBlackFillerGuiItem;
import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.buildReturnToPlaybackGuiItem;
import static de.affenherzog.phantomreplay.gui.playback.PlaybackGuiService.COOLDOWN_TIME_TICKS;

public abstract class AbstractPlaybackModifyAllGui extends PhantomGui {

    protected final PlaybackGuiService playbackGuiService;
    protected final PlaybackManager playbackManager;
    protected final PlaybackStats playbackStats;

    protected AbstractPlaybackModifyAllGui(Plugin plugin, UUID playerUUID, Component title, PlaybackGuiService playbackGuiService, PlaybackManager playbackManager, PlaybackStats playbackStats) {
        super(plugin, playerUUID, title, InventoryType.HOPPER);
        this.playbackGuiService = playbackGuiService;
        this.playbackManager = playbackManager;
        this.playbackStats = playbackStats;
        initialise();
    }

    @Override
    protected void addItems() {
        items.put(2, buildReturnToPlaybackGuiItem(playerUUID, playbackGuiService));
    }

    @Override
    protected void addFiller() {
        items.putAll(GuiFillerUtil.fillBorder(inventory.getSize(), buildBlackFillerGuiItem()));
    }

    protected Runnable createToggle(int affectedReplays, Material firstMaterial, Material secondMaterial, Runnable databaseAction) {
        return () -> {
            Player player = getPlayer();

            if (playbackGuiService.isOnCooldown(playerUUID)) {
                return;
            }

            if (affectedReplays == 0) {
                playbackGuiService.openPlaybackGui(playerUUID);
                GuiSoundUtil.playWarning(player);
                return;
            }

            playbackGuiService.setCooldown(playerUUID);
            player.setCooldown(firstMaterial, COOLDOWN_TIME_TICKS);
            player.setCooldown(secondMaterial, COOLDOWN_TIME_TICKS);

            databaseAction.run();

            playbackGuiService.openPlaybackGui(playerUUID);
            GuiSoundUtil.playSuccess(getPlayer());
        };
    }

}
