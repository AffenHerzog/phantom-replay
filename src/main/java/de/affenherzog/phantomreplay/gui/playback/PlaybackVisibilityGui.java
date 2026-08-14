package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.gui.item.PhantomGuiItem;
import de.affenherzog.phantomreplay.gui.util.GuiSoundUtil;
import de.affenherzog.phantomreplay.gui.util.GuiTitleUtil;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackStats;
import de.affenherzog.phantomreplay.playback.VisibilityScope;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.affenherzog.phantomreplay.util.MUtil.MM;

public class PlaybackVisibilityGui extends AbstractPlaybackModifyAllGui {

    private static final Component TITLE =
            MM.deserialize("<gradient:#660000:#8b3a00><bold>Sichtbarkeit ändern</bold></gradient>");

    private static final Component CENTERED_GUI_TITLE = GuiTitleUtil.centerTitle(TITLE, "Sichtbarkeit ändern");

    public PlaybackVisibilityGui(Plugin plugin, UUID playerUUID, PlaybackStats playbackStats, PlaybackManager playbackManager, PlaybackGuiService playbackGuiService) {
        super(plugin, playerUUID, CENTERED_GUI_TITLE, playbackGuiService, playbackManager, playbackStats);
    }

    @Override
    protected void addItems() {
        super.addItems();
        items.put(0, buildPrivateVisibilityReplays(playbackStats.globalCount()));
        items.put(4, buildPublicVisibilityReplays(playbackStats.privateCount()));
    }

    private PhantomGuiItem buildPrivateVisibilityReplays(int affectedReplays) {
        ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);

        List<Component> lore = new ArrayList<>(List.of(
                Component.empty(),
                MM.deserialize("<gray>Macht alle Replays nur für dich sichtbar.</gray>"),
                Component.empty(),
                MM.deserialize("<gray>Aktuell öffentlich: <yellow>" + affectedReplays + "</yellow></gray>"),
                MM.deserialize("<gray>Aktion ändert: <yellow>" + affectedReplays + " Replays</yellow></gray>")
        ));

        if (affectedReplays != 0) {
            lore.addAll(List.of(
                    Component.empty(),
                    MM.deserialize("<dark_red>▶ Klicke zum Ausführen</dark_red>")
            ));
        }

        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<dark_red><bold>Alle auf Privat setzen</bold></dark_red>"));
            meta.lore(lore);
        });

        return new PhantomGuiItem(itemStack, () -> {
            if (affectedReplays == 0) {
                playbackGuiService.openPlaybackGui(playerUUID);
                GuiSoundUtil.playWarning(getPlayer());
                return;
            }
            playbackManager.updateVisibilitySession(playerUUID, VisibilityScope.PRIVAT);
            playbackGuiService.openPlaybackGui(playerUUID);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

    private PhantomGuiItem buildPublicVisibilityReplays(int affectedReplays) {
        ItemStack itemStack = new ItemStack(Material.ENDER_EYE);

        List<Component> lore = new ArrayList<>(List.of(
                Component.empty(),
                MM.deserialize("<gray>Macht alle Replays für jeden sichtbar.</gray>"),
                Component.empty(),
                MM.deserialize("<gray>Aktuell privat: <yellow>" + affectedReplays + "</yellow></gray>"),
                MM.deserialize("<gray>Aktion ändert: <yellow>" + affectedReplays + " Replays</yellow></gray>")
        ));

        if (affectedReplays != 0) {
            lore.addAll(List.of(
                    Component.empty(),
                    MM.deserialize("<dark_green>▶ Klicke zum Ausführen</dark_green>")
            ));
        }

        itemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<dark_green><bold>Alle auf Öffentlich setzen</bold></dark_green>"));
            meta.lore(lore);
        });

        return new PhantomGuiItem(itemStack, () -> {
            if (affectedReplays == 0) {
                playbackGuiService.openPlaybackGui(playerUUID);
                GuiSoundUtil.playWarning(getPlayer());
                return;
            }
            playbackManager.updateVisibilitySession(playerUUID, VisibilityScope.GLOBAL);
            playbackGuiService.openPlaybackGui(playerUUID);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

}