package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import de.affenherzog.phantomreplay.gui.util.GuiFillerUtil;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.buildBlackFillerGuiItem;
import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.buildReturnToReplayGuiItem;

public abstract class AbstractPlaybackModifyAllGui extends PhantomGui {

    protected final PlaybackManager playbackManager;
    protected final PlaybackStats playbackStats;

    protected AbstractPlaybackModifyAllGui(Plugin plugin, UUID playerUUID, Component title, PlaybackManager playbackManager, PlaybackStats playbackStats) {
        super(plugin, playerUUID, title, InventoryType.HOPPER);
        this.playbackManager = playbackManager;
        this.playbackStats = playbackStats;
        initialise();
    }

    @Override
    protected void addItems() {
        items.put(2, buildReturnToReplayGuiItem(plugin, playerUUID, playbackManager));
    }

    @Override
    protected void addFiller() {
        items.putAll(GuiFillerUtil.fillBorder(inventory.getSize(), buildBlackFillerGuiItem()));
    }


}
