package de.affenherzog.phantomreplay.application;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.player.PhantomPlayer;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static de.affenherzog.phantomreplay.util.MUtil.MM;

@RequiredArgsConstructor
public class ReplayManagementService {

    private final PhantomPlayerManager phantomPlayerManager;
    private final PlaybackManager playbackManager;
    private final ReplayRepository replayRepository;

    private final Cache<UUID, Replay> renaming = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    public void addRenaming(UUID uuid, Replay replay) {
        renaming.put(uuid, replay);
    }

    public void removeRenaming(UUID uuid) {
        renaming.invalidate(uuid);
    }

    public boolean isRenaming(UUID uuid) {
        return renaming.getIfPresent(uuid) != null;
    }

    public Replay getRenaming(UUID uuid) {
        return renaming.getIfPresent(uuid);
    }

    public RenameResult renameReplay(Replay replay, UUID uuid, String newName) {
        String replayName = replay.name();
        PhantomPlayer player = phantomPlayerManager.getPhantomPlayer(uuid).orElseThrow();
        Replay newReplay = replay.withName(newName);

        if (newName.length() > 50) {
            return RenameResult.NAME_TOO_LONG;
        }

        boolean nameExists = player.getOwnedReplays().stream().anyMatch(it ->
                it.getUniqueName().equals(newReplay.getUniqueName()));

        if (nameExists) {
            return RenameResult.NAME_EXISTS;
        }

        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(it -> {
            it.getOwnedReplays().removeIf(old -> old.getUniqueName().equals(replayName));
            it.getOwnedReplays().add(newReplay);
        });
        playbackManager.loadReplayIntoSession(newReplay.id(), newReplay);
        replayRepository.updateReplayName(newReplay.id(), newReplay.name());

        return RenameResult.SUCCESS;
    }


    public void deleteReplay(Replay replay, UUID uuid) {
        if (replay == null) return;

        if (isRenaming(uuid)) {
            removeRenaming(uuid);
        }

        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(it ->
                it.getOwnedReplays().removeIf(old -> old.id() == replay.id())
        );
        playbackManager.removeSessionByReplayId(replay.id());
        replayRepository.deleteReplay(replay.id());
    }


    public enum RenameResult {
        SUCCESS("<gray>Replay <yellow><old_name></yellow> erfolgreich zu <yellow><new_name></yellow> umbenannt."),
        NAME_TOO_LONG("<red>Die Aufnahme konnte nicht umbenannt werden, da der Name zu lang ist."),
        NAME_EXISTS("<red>Die Aufnahme konnte nicht umbenannt werden, da dieser Name bereits verwendet wird.");

        private final String rawMessage;

        RenameResult(String inputString) {
            this.rawMessage = inputString;
        }

        public Component getMessageComponent() {
            return MM.deserialize(rawMessage);
        }

        public Component getMessageComponent(String oldName, String newName) {
            return MM.deserialize(rawMessage, Placeholder.parsed("old_name", oldName), Placeholder.parsed("new_name", newName));
        }
    }
}


