package de.affenherzog.phantomreplay.gui.item;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static PhantomGuiItem buildCloseGuiItem(PhantomGui gui) {
        return new PhantomGuiItem(new ItemStack(Material.BARRIER), gui::close);
    }

}
