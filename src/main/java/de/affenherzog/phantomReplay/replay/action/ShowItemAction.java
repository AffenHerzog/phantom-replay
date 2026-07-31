package de.affenherzog.phantomReplay.replay.action;

import org.bukkit.inventory.ItemStack;

public record ShowItemAction(ItemStack itemStack) implements ReplayAction {
}
