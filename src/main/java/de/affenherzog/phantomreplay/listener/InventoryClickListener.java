package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!(topInventory.getHolder(false) instanceof PhantomGui phantomGui)) {
            return;
        }

        if (event.getClickedInventory() == null) return;

        if (!event.getClickedInventory().equals(topInventory)) {
            switch (event.getAction()) {
                case COLLECT_TO_CURSOR, MOVE_TO_OTHER_INVENTORY:
                    event.setCancelled(true);
                    break;
                default:
                    break;
            }
            return;
        }

        event.setCancelled(true);
        phantomGui.onInventoryClick(event.getSlot());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!(topInventory.getHolder(false) instanceof PhantomGui)) {
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < topInventory.getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}