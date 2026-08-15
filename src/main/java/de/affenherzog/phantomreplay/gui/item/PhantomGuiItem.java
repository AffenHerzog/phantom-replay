package de.affenherzog.phantomreplay.gui.item;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class PhantomGuiItem {

    @Getter
    private final ItemStack itemStack;
    private final Runnable onClick;

    public PhantomGuiItem(ItemStack itemStack, Runnable onClick) {
        this.itemStack = itemStack;
        this.onClick = onClick;
    }

    public void run() {
        onClick.run();
    }


}
