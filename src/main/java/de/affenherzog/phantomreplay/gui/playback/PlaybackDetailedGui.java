package de.affenherzog.phantomreplay.gui.playback;

import de.affenherzog.phantomreplay.application.ReplayManagementService;
import de.affenherzog.phantomreplay.gui.PhantomGui;
import de.affenherzog.phantomreplay.gui.item.PhantomGuiItem;
import de.affenherzog.phantomreplay.gui.util.GuiFillerUtil;
import de.affenherzog.phantomreplay.gui.util.GuiSoundUtil;
import de.affenherzog.phantomreplay.gui.util.GuiTitleUtil;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackSessionModel;
import de.affenherzog.phantomreplay.playback.VisibilityScope;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

import static de.affenherzog.phantomreplay.gui.item.PhantomGuiItemFactory.*;
import static de.affenherzog.phantomreplay.util.MUtil.MM;

public class PlaybackDetailedGui extends PhantomGui {

    private static final Component TITLE =
            MM.deserialize("<gradient:#660000:#8b3a00><bold>Aufnahme</bold></gradient>");

    private static final Component CENTERED_GUI_TITLE = GuiTitleUtil.centerTitle(TITLE, "Aufnahme");

    private final PlaybackManager playbackManager;
    private final ReplayManagementService replayManagementService;
    private final PlaybackSessionModel session;
    private final PlaybackGuiService playbackGuiService;

    protected PlaybackDetailedGui(Plugin plugin, UUID playerUUID, PlaybackSessionModel session, PlaybackManager playbackManager, ReplayManagementService replayManagementService, PlaybackGuiService playbackGuiService) {
        super(plugin, playerUUID, CENTERED_GUI_TITLE, 27);
        this.playbackManager = playbackManager;
        this.replayManagementService = replayManagementService;
        this.session = session;
        this.playbackGuiService = playbackGuiService;
        initialise();
    }

    @Override
    protected void addItems() {
        items.put(4, buildPlaybackItem(session, false, () -> {
        }));
        items.put(11, buildRenameItem());
        items.put(13, buildActiveItem(session));
        items.put(15, buildVisibilityItem(session));
        items.put(22, buildReturnToPlaybackGuiItem(playerUUID, playbackGuiService));
        items.put(26, buildDeleteItem(session));
    }

    private PhantomGuiItem buildRenameItem() {
        ItemStack infoItemStack = new ItemStack(Material.WRITABLE_BOOK);
        infoItemStack.editMeta(meta -> meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Umbenennen</bold></gradient>")));
        return new PhantomGuiItem(infoItemStack, () -> {
            Player player = getPlayer();

            if (replayManagementService.isRenaming(playerUUID)) {
                player.sendMessage(MM.deserialize("<gray>Du bist bereits im Prozess der Umbenennung!"));
                close();
                GuiSoundUtil.playWarning(player);
                return;
            }

            replayManagementService.addRenaming(playerUUID, session.replay());

            player.showTitle(Title.title(
                    MM.deserialize("<gold>Chat öffnen</gold>"),
                    MM.deserialize("<gray>Tippe den neuen Namen ein</gray>")
            ));
            player.sendMessage(MM.deserialize("<gray>▶ Bitte gib den neuen Namen für das Replay ein.\n</gray>" +
                    "<gray>▶ Schreibe <red>'abbruch'</red>, um abzubrechen.</gray>"
            ));

            close();
            GuiSoundUtil.playSuccess(player);
        });
    }

    private PhantomGuiItem buildActiveItem(PlaybackSessionModel session) {
        ItemStack infoItemStack = session.active() ? new ItemStack(Material.GREEN_DYE) : new ItemStack(Material.RED_DYE);
        infoItemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Aktivitätsstatus</bold></gradient>"));
            meta.lore(List.of(
                    Component.empty(),
                    MM.deserialize("<gray>Status: <yellow>" + (session.active() ? "Aktiv" : "Inaktiv")),
                    Component.empty(),
                    MM.deserialize("<gold>▶ Klicke um zu " + (session.active() ? "deaktivieren" : "aktivieren"))
            ));
        });

        return new PhantomGuiItem(infoItemStack, () -> {
            PlaybackSessionModel newSession = session.withActive(!session.active());
            playbackManager.updateActiveSession(session.replay().id(), newSession.active());
            playbackGuiService.openPlaybackDetailedGui(playerUUID, newSession);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

    private PhantomGuiItem buildVisibilityItem(PlaybackSessionModel session) {
        ItemStack infoItemStack = new ItemStack(Material.ENDER_EYE);
        infoItemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Sichbarkeit</bold></gradient>"));
            meta.lore(List.of(
                    Component.empty(),
                    MM.deserialize("<gray>Status: <yellow>" + (session.visibilityScope() == VisibilityScope.PRIVAT ? "Privat" : "Öffentlich")),
                    Component.empty(),
                    MM.deserialize("<gold>▶ Klicke um die Aufnahme " + (session.visibilityScope() == VisibilityScope.PRIVAT ? "öffentlich" : "privat" + " zu machen"))
            ));
        });

        return new PhantomGuiItem(infoItemStack, () -> {
            PlaybackSessionModel newSession = session.withVisibilityScope(session.visibilityScope() == VisibilityScope.PRIVAT ? VisibilityScope.GLOBAL : VisibilityScope.PRIVAT);
            playbackManager.updateVisibilitySession(session.replay().id(), newSession.visibilityScope());
            playbackGuiService.openPlaybackDetailedGui(playerUUID, newSession);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

    private PhantomGuiItem buildDeleteItem(PlaybackSessionModel session) {
        ItemStack infoItemStack = new ItemStack(Material.LAVA_BUCKET);
        infoItemStack.editMeta(meta -> {
            meta.displayName(MM.deserialize("<gradient:#660000:#8b3a00><bold>Löschen</bold></gradient>"));
            meta.lore(List.of(
                    Component.empty(),
                    MM.deserialize("<dark_red>Gefährlicher bereich!</dark_red><gray> hier"),
                    MM.deserialize("<gray>löscht du die Aufnahme"),
                    Component.empty(),
                    MM.deserialize("<gold>▶ Klicke zum löschen</gold>")
            ));
        });

        return new PhantomGuiItem(infoItemStack, () -> {
            replayManagementService.deleteReplay(session.replay(), playerUUID);
            playbackGuiService.openPlaybackGui(playerUUID);
            GuiSoundUtil.playSuccess(getPlayer());
        });
    }

    @Override
    protected void addFiller() {
        items.putAll(GuiFillerUtil.fill(inventory.getSize(), buildBlackFillerGuiItem()));
    }
}
