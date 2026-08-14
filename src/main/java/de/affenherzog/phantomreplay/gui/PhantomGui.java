package de.affenherzog.phantomreplay.gui;

import de.affenherzog.phantomreplay.gui.item.PhantomGuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PhantomGui implements InventoryHolder {

    private final UUID playerUUID;

    protected final Inventory inventory;
    protected final Map<Integer, PhantomGuiItem> items = new HashMap<>();

    protected PhantomGui(Plugin plugin, UUID playerUUID, Component title, InventoryType inventoryType) {
        this.playerUUID = playerUUID;
        this.inventory = plugin.getServer().createInventory(this, inventoryType, title);
        initialise();
    }

    protected PhantomGui(Plugin plugin, UUID playerUUID, Component title, int size) {
        this.playerUUID = playerUUID;
        this.inventory = plugin.getServer().createInventory(this, size, title);
        initialise();
    }

    public void onInventoryClick(int slot) {
        ItemStack item = getInventory().getItem(slot);
        if (item == null) return;
        PhantomGuiItem phantomGuiItem = items.get(slot);
        if (phantomGuiItem == null) return;
        phantomGuiItem.run();
    }

    public void open() {
        Player player = getPlayer();
        player.openInventory(inventory);
    }

    public void close() {
        Player player = getPlayer();
        Inventory currentInventory = player.getOpenInventory().getTopInventory();
        if (currentInventory != inventory) return;
        player.closeInventory();
    }

    protected abstract void addItems();

    protected abstract void setFiller();

    protected Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    private void initialise() {
        addItems();
        setFiller();
        setItems();
    }

    private void setItems() {
        items.forEach((slot, item) -> inventory.setItem(slot, item.getItemStack()));
    }

}
