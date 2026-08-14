package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.gui.PhantomGui;
import de.affenherzog.phantomreplay.gui.item.PhantomGuiItem;
import de.affenherzog.phantomreplay.gui.util.GuiTitleUtil;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackSessionModel;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import de.affenherzog.phantomreplay.playback.VisibilityScope;
import de.affenherzog.phantomreplay.replay.Position;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static de.affenherzog.phantomreplay.gui.util.GuiFillerUtil.fillBorder;
import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.buildBlackFillerGuiItem;
import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.buildCloseGuiItem;
import static de.affenherzog.phantomreplay.util.MUtil.MM;

public class PlaybackGui extends PhantomGui {

    private static final Component TITLE =
            MM.deserialize("<gradient:#660000:#8b3a00><bold>Aufnahmen</bold></gradient>");

    private static final Component CENTERED_GUI_TITLE = GuiTitleUtil.centerTitle(TITLE, "Aufnahmen");

    private final PlaybackManager playbackManager;

    public PlaybackGui(Plugin plugin, UUID uuid, PlaybackManager playbackManager) {
        super(plugin, uuid, CENTERED_GUI_TITLE, 54);
        this.playbackManager = playbackManager;
        initialise();
    }

    @Override
    protected void addItems() {
        PlaybackStats playbackStats = playbackManager.getPlayerPlaybackStats(playerUUID);

        items.put(49, buildCloseGuiItem(this));
        items.put(2, buildToggleGlobalVisibilityItem(playbackStats));
        items.put(4, buildInfoItem(playbackStats.totalCount()));
        items.put(6, buildToggleGlobalActiveItem(playbackStats));

        List<PlaybackSessionModel> sessions = playbackManager.getSessions(playerUUID);
        Stack<Integer> emptySlots = getEmptySlots();

        for (int i = 0; i < 35; i++) {
            if (i >= sessions.size()) {
                return;
            }
            items.put(emptySlots.pop(), buildPlaybackItem(sessions.get(i)));
        }
    }

    private Stack<Integer> getEmptySlots() {
        Stack<Integer> emptySlots = new Stack<>();
        for (int i = inventory.getSize() - 9; i >= 10; i--) {
            if (i % 9 == 0 || i % 9 == 8) {
                continue;
            }
            emptySlots.push(i);
        }
        return emptySlots;
    }

    private PhantomGuiItem buildPlaybackItem(PlaybackSessionModel session) {
        String active = session.active() ? "<dark_green>Aktiv" : "<dark_red>Inaktiv";

        String visibility = session.visibilityScope() == VisibilityScope.PRIVAT ? "Privat" : "Öffentlich";

        Position startPosition = session.replay().getStartPosition();
        String startPositionString = startPosition.getBlockX() + " " + startPosition.getBlockY() + " " + startPosition.getBlockZ();

        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        itemStack.editMeta(meta -> {
           meta.displayName(MM.deserialize("<dark_gray>" + session.replay().name()));
           meta.lore(List.of(
                   Component.empty(),
                   MM.deserialize("<gray>Aktivitätsstatus: <yellow>" + active),
                   MM.deserialize("<gray>Sichtbarkeit: <yellow>" + visibility),
                   MM.deserialize("<gray>Startpunkt: <yellow>" + startPositionString),
                   Component.empty(),
                   MM.deserialize("<gold>▶ Klicke zum Anpassen</gold>")
           ));
        });
        return new PhantomGuiItem(itemStack, () -> {});
    }

    private PhantomGuiItem buildInfoItem(int replayCount) {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Info</bold></gradient>"));
            meta.lore(List.of(
                    Component.empty(),
                    MM.deserialize("<gray>Gesamte Replays: <yellow>" + replayCount + "</yellow></gray>")
            ));
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });

        return new PhantomGuiItem(itemStack, () -> {});
    }

    private PhantomGuiItem buildToggleGlobalVisibilityItem(PlaybackStats playbackStats) {
        ItemStack itemStack = new ItemStack(Material.ENDER_EYE);
        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Sichtbarkeit</bold></gradient>"));
            meta.lore(List.of(
                    Component.empty(),
                    MM.deserialize("<gray>Ändere die Sichtbarkeit für alle</gray>"),
                    MM.deserialize("<gray>deine Replays gleichzeitig.</gray>"),
                    Component.empty(),
                    MM.deserialize("<gray>Privat: <yellow>" + playbackStats.privateCount() + "</yellow></gray>"),
                    MM.deserialize("<gray>Öffentlich: <yellow>" + playbackStats.globalCount() + "</yellow></gray>"),
                    Component.empty(),
                    MM.deserialize("<gold>▶ Klicke zum Anpassen</gold>")
            ));
        });

        return new PhantomGuiItem(itemStack, () ->
                new PlaybackVisibilityGui(plugin, playerUUID, playbackManager, playbackStats).open());
    }

    private PhantomGuiItem buildToggleGlobalActiveItem(PlaybackStats playbackStats) {
        ItemStack itemStack = new ItemStack(Material.LEATHER_BOOTS);
        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Aktivitätsstatus</bold></gradient>"));
            meta.lore(List.of(
                    Component.empty(),
                    MM.deserialize("<gray>Starte oder stoppe alle</gray>"),
                    MM.deserialize("<gray>deine Replays gleichzeitig.</gray>"),
                    Component.empty(),
                    MM.deserialize("<gray>Aktiv: <yellow>" + playbackStats.activeCount() + "</yellow></gray>"),
                    MM.deserialize("<gray>Inaktiv: <yellow>" + playbackStats.inactiveCount() + "</yellow></gray>"),
                    Component.empty(),
                    MM.deserialize("<gold>▶ Klicke zum Anpassen</gold>")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });

        return new PhantomGuiItem(itemStack, () ->
                new PlaybackActiveGui(plugin, playerUUID, playbackManager, playbackStats).open());
    }

    @Override
    protected void addFiller() {
        items.putAll(fillBorder(inventory.getSize(), buildBlackFillerGuiItem()));
    }
}