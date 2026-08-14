package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.gui.item.PhantomGuiItem;
import de.affenherzog.phantomreplay.gui.util.GuiSoundUtil;
import de.affenherzog.phantomreplay.gui.util.GuiTitleUtil;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.affenherzog.phantomreplay.util.MUtil.MM;

public class PlaybackActiveGui extends AbstractPlaybackModifyAllGui {

    private static final Component TITLE =
            MM.deserialize("<gradient:#660000:#8b3a00><bold>Aktivität ändern</bold></gradient>");

    private static final Component CENTERED_GUI_TITLE = GuiTitleUtil.centerTitle(TITLE, "Aktivität ändern");

    public PlaybackActiveGui(Plugin plugin, UUID playerUUID, PlaybackStats playbackStats, PlaybackManager playbackManager, PlaybackGuiService playbackGuiService) {
        super(plugin, playerUUID, CENTERED_GUI_TITLE, playbackGuiService, playbackManager, playbackStats);
    }

    @Override
    protected void addItems() {
        super.addItems();
        items.put(0, buildActivateAllReplays(playbackStats.inactiveCount()));
        items.put(4, buildDeactivateAllReplays(playbackStats.activeCount()));
    }

    private PhantomGuiItem buildActivateAllReplays(int affectedReplays) {
        ItemStack itemStack = new ItemStack(Material.LIME_WOOL);

        List<Component> lore = new ArrayList<>(List.of(
                Component.empty(),
                MM.deserialize("<gray>Startet alle deine Replays gleichzeitig.</gray>"),
                Component.empty(),
                MM.deserialize("<gray>Aktuell inaktiv: <yellow>" + affectedReplays + "</yellow></gray>"),
                MM.deserialize("<gray>Aktion ändert: <yellow>" + affectedReplays + " Replays</yellow></gray>")
        ));

        if (affectedReplays != 0) {
            lore.addAll(List.of(
                    Component.empty(),
                    MM.deserialize("<dark_green>▶ Klicke zum Ausführen</dark_green>")
            ));
        }

        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<dark_green><bold>Alle Replays Aktivieren</bold></dark_green>"));
            meta.lore(lore);
        });

        return new PhantomGuiItem(itemStack, () -> {
            if (affectedReplays == 0) {
                playbackGuiService.openPlaybackGui(playerUUID);
                GuiSoundUtil.playWarning(getPlayer());
                return;
            }
            playbackManager.updateActiveSession(playerUUID, true);
            playbackGuiService.openPlaybackGui(playerUUID);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

    private PhantomGuiItem buildDeactivateAllReplays(int affectedReplays) {
        ItemStack itemStack = new ItemStack(Material.RED_WOOL);

        List<Component> lore = new ArrayList<>(List.of(
                Component.empty(),
                MM.deserialize("<gray>Stoppt alle laufenden Replays sofort.</gray>"),
                Component.empty(),
                MM.deserialize("<gray>Aktuell aktiv: <yellow>"+affectedReplays +"</yellow></gray>"),
                MM.deserialize("<gray>Aktion ändert: <yellow>"+affectedReplays +" Replays</yellow></gray>")
        ));

        if (affectedReplays != 0) {
            lore.addAll(List.of(
                    Component.empty(),
                    MM.deserialize("<dark_green>▶ Klicke zum Ausführen</dark_green>")
            ));
        }

        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<dark_red><bold>Alle Replays Deaktivieren</bold></dark_red>"));
            meta.lore(lore);
        });

        return new PhantomGuiItem(itemStack, () -> {
            if (affectedReplays == 0) {
                playbackGuiService.openPlaybackGui(playerUUID);
                GuiSoundUtil.playWarning(getPlayer());
                return;
            }
            playbackManager.updateActiveSession(playerUUID, false);
            playbackGuiService.openPlaybackGui(playerUUID);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

}