package de.affenherzog.phantomreplay.player;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class PhantomPlayerManager {

    private final Map<UUID, PhantomPlayer> phantomPlayers;

    public Optional<PhantomPlayer> getPhantomPlayer(UUID uuid) {
        return Optional.ofNullable(phantomPlayers.get(uuid));
    }

    public void addPhantomPlayer(PhantomPlayer phantomPlayer) {
        Objects.requireNonNull(phantomPlayer, "PhantomPlayer darf nicht null sein!");
        UUID uuid = phantomPlayer.getUuid();
        phantomPlayers.put(uuid, phantomPlayer);
    }

    public void removePhantomPlayer(UUID uuid) {
        phantomPlayers.remove(uuid);
    }

}
