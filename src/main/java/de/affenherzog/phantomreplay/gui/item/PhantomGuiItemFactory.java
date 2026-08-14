package de.affenherzog.phantomreplay.gui.item;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import de.affenherzog.phantomreplay.gui.playback.PlaybackGui;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static de.affenherzog.phantomreplay.util.MUtil.MM;

public class PhantomGuiItemFactory {

    private PhantomGuiItemFactory() {
        /* This utility class should not be instantiated */
    }

    public static PhantomGuiItem buildBlackFillerGuiItem() {
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = fillerItem.getItemMeta();
        fillerItemMeta.setHideTooltip(true);
        fillerItem.setItemMeta(fillerItemMeta);
        return new PhantomGuiItem(fillerItem, () -> {});
    }

    public static PhantomGuiItem buildReturnToReplayGuiItem(Plugin plugin, UUID uuid, PlaybackManager playbackManager) {
        ItemStack returnToPlayback = new ItemStack(Material.BARRIER);
        returnToPlayback.editMeta(it -> it.displayName(MM.deserialize("<dark_gray><bold>Zurück")));
        return new PhantomGuiItem(returnToPlayback, () -> new PlaybackGui(plugin, uuid, playbackManager).open());
    }

    public static PhantomGuiItem buildCloseGuiItem(PhantomGui gui) {
        ItemStack close = new ItemStack(Material.BARRIER);
        close.editMeta(it -> it.displayName(MM.deserialize("<dark_gray><bold>Tschüss")));
        return new PhantomGuiItem(close, gui::close);
    }

}
