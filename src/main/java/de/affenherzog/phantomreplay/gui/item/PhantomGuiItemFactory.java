package de.affenherzog.phantomreplay.gui.item;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import de.affenherzog.phantomreplay.gui.playback.PlaybackGuiService;
import de.affenherzog.phantomreplay.gui.util.GuiSoundUtil;
import de.affenherzog.phantomreplay.playback.PlaybackSessionModel;
import de.affenherzog.phantomreplay.playback.VisibilityScope;
import de.affenherzog.phantomreplay.replay.Position;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
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

    public static PhantomGuiItem buildReturnToPlaybackGuiItem(UUID uuid, PlaybackGuiService playbackGuiService) {
        ItemStack returnToPlayback = new ItemStack(Material.BARRIER);
        returnToPlayback.editMeta(it -> it.displayName(MM.deserialize("<dark_gray><bold>Zurück")));
        return new PhantomGuiItem(returnToPlayback, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            playbackGuiService.openPlaybackGui(uuid);
            GuiSoundUtil.playClick(player);
        });
    }

    public static PhantomGuiItem buildCloseGuiItem(PhantomGui gui, UUID uuid) {
        ItemStack close = new ItemStack(Material.BARRIER);
        close.editMeta(it -> it.displayName(MM.deserialize("<dark_gray><bold>Tschüss")));
        return new PhantomGuiItem(close, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            gui.close();
            GuiSoundUtil.playClick(player);
        });
    }

    public static PhantomGuiItem buildPlaybackItem(PlaybackSessionModel session, boolean showClickPrompt, Runnable onClick) {
        String active = session.active() ? "<dark_green>Aktiv</dark_green>" : "<dark_red>Inaktiv</dark_red>";
        String visibility = session.visibilityScope() == VisibilityScope.PRIVAT ? "Privat" : "Öffentlich";

        Position startPosition = session.replay().getStartPosition();
        String startPositionString = startPosition.getBlockX() + " " + startPosition.getBlockY() + " " + startPosition.getBlockZ();

        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>" + session.replay().name() + "</bold></gradient>"));
            List<Component> lore = new java.util.ArrayList<>(List.of(
                    Component.empty(),
                    MM.deserialize("<gray>Aktivitätsstatus: <yellow>" + active),
                    MM.deserialize("<gray>Sichtbarkeit: <yellow>" + visibility),
                    MM.deserialize("<gray>Startpunkt: <yellow>" + startPositionString)
            ));

            if (showClickPrompt) {
                lore.add(Component.empty());
                lore.add(MM.deserialize("<gold>▶ Klicke zum Anpassen</gold>"));
            }

            meta.lore(lore);
        });

        return new PhantomGuiItem(itemStack, onClick);
    }

}
