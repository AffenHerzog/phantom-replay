package de.affenherzog.phantomreplay.playback;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.affenherzog.phantomreplay.replay.Frame;
import de.affenherzog.phantomreplay.replay.Position;
import de.affenherzog.phantomreplay.replay.action.*;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ReplayAnimator {

    private final int npcEntityId;
    private final UUID npcUUID = UUID.randomUUID();

    private final ProtocolManager protocolManager;
    private final PacketFactory packetFactory = new PacketFactory();

    private Position lastPosition;
    private boolean isSneaking = false;
    private boolean isSprinting = false;

    private final Set<UUID> activeViewers = new HashSet<>();

    @Setter
    private VisibilityScope scope;

    private final UUID ownerUUID;

    public ReplayAnimator(VisibilityScope scope, UUID ownerUUID) {
        this.scope = scope;
        this.ownerUUID = ownerUUID;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.npcEntityId = generateEntityId();
    }

    private int generateEntityId() {
        return 100000 + (new Random().nextInt(900000));
    }

    public void playFrame(Frame frame) {
        updateActiveViewers(frame.position());

        if (activeViewers.isEmpty()) {
            lastPosition = frame.position();
            return;
        }

        if (lastPosition == null) {
            lastPosition = frame.position();
            return;
        }

        sendMovement(frame.position(), lastPosition);

        if (frame.replayActions() != null) {
            boolean metadataChanged = false;

            for (ReplayAction action : frame.replayActions()) {
                switch (action) {
                    case LeftClickAction _ -> sendArmAnimation();
                    case ShowItemAction s -> sendItemInHand(s);
                    case SneakAction sn -> {
                        this.isSneaking = sn.isSneaking();
                        metadataChanged = true;
                    }
                    case SprintAction sp -> {
                        this.isSprinting = sp.isSprinting();
                        metadataChanged = true;
                    }
                }
            }

            if (metadataChanged) {
                sendMetadataStatus();
            }
        }

        lastPosition = frame.position();
    }

    public List<Player> getViewersInScope(Position currentPos) {
        if (scope == VisibilityScope.PRIVAT) {
            Player owner = Bukkit.getPlayer(ownerUUID);
            if (owner != null && owner.isOnline() && isInRange(owner, currentPos)) {
                return Collections.singletonList(owner);
            }
            return Collections.emptyList();
        }

        if (scope == VisibilityScope.GLOBAL) {
            World world = Bukkit.getWorld(currentPos.worldName());
            if (world == null) return Collections.emptyList();
            Location loc = currentPos.toBukkitLocation(world);
            return new ArrayList<>(loc.getNearbyPlayers(60.0));
        }

        return Collections.emptyList();
    }

    private boolean isInRange(Player p, Position pos) {
        if (!p.getWorld().getName().equals(pos.worldName())) return false;

        double dx = p.getLocation().getX() - pos.x();
        double dy = p.getLocation().getY() - pos.y();
        double dz = p.getLocation().getZ() - pos.z();

        return (dx * dx + dy * dy + dz * dz) <= (60.0 * 60.0);
    }

    private void updateActiveViewers(Position position) {
        Position correctPos = lastPosition != null ? lastPosition : position;
        List<Player> viewersInScope = getViewersInScope(position);

        Set<UUID> viewersInScopeUuids = new HashSet<>();
        for (Player p : viewersInScope) {
            viewersInScopeUuids.add(p.getUniqueId());
        }

        Iterator<UUID> iterator = activeViewers.iterator();
        while (iterator.hasNext()) {
            UUID activeUuid = iterator.next();
            Player activePlayer = Bukkit.getPlayer(activeUuid);

            if (activePlayer == null || !activePlayer.isOnline() || !viewersInScopeUuids.contains(activeUuid)) {
                if (activePlayer != null && activePlayer.isOnline()) {
                    despawn(activePlayer);
                }
                iterator.remove();
            }
        }

        for (Player viewer : viewersInScope) {
            if (!activeViewers.contains(viewer.getUniqueId())) {
                spawn(viewer, correctPos);
                activeViewers.add(viewer.getUniqueId());
            }
        }
    }

    private void spawn(Player player, Position currentPos) {
        PacketContainer infoPacket = packetFactory.buildInfoPackage(npcUUID, "MeinNPC");
        PacketContainer spawnPacket = packetFactory.buildSpawnPacket(npcUUID, npcEntityId, currentPos);
        protocolManager.sendServerPacket(player, infoPacket);
        protocolManager.sendServerPacket(player, spawnPacket);
    }

    public void despawn(Player player) {
        PacketContainer destroyPacket = packetFactory.buildDestroyPacket(npcEntityId);
        PacketContainer infoRemovePacket = packetFactory.buildInfoRemovePacket(npcUUID);
        protocolManager.sendServerPacket(player, destroyPacket);
        protocolManager.sendServerPacket(player, infoRemovePacket);
    }

    public void despawnAll() {
        for (UUID uuid : activeViewers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                despawn(player);
            }
        }
        activeViewers.clear();
    }

    private void sendMovement(Position newPos, Position oldPos) {
        double dx = newPos.x() - oldPos.x();
        double dy = newPos.y() - oldPos.y();
        double dz = newPos.z() - oldPos.z();
        double distanceSq = (dx * dx + dy * dy + dz * dz);

        if (distanceSq > 64.0) {
            Set<UUID> currentViewers = new HashSet<>(activeViewers);
            for (UUID uuid : currentViewers) {
                Player viewer = Bukkit.getPlayer(uuid);
                if (viewer != null && viewer.isOnline()) {
                    despawn(viewer);
                    spawn(viewer, newPos);
                }
            }
        } else {
            PacketContainer movePacket = packetFactory.buildMovePacket(npcEntityId, oldPos, newPos);

            Player dummyPlayer = activeViewers.isEmpty() ? null : Bukkit.getPlayer(activeViewers.iterator().next());
            PacketContainer headPacket = dummyPlayer != null ? packetFactory.buildHeadRotationPacket(npcEntityId, newPos, dummyPlayer) : null;

            sendPacketToAll(movePacket, headPacket);
        }

        lastPosition = newPos;
    }

    private void sendMetadataStatus() {
        if (activeViewers.isEmpty()) return;
        PacketContainer metadataPacket = packetFactory.buildMetadataPacket(npcEntityId, isSneaking, isSprinting);

        sendPacketToAll(metadataPacket);
    }

    private void sendArmAnimation() {
        if (activeViewers.isEmpty()) return;

        Player dummyPlayer = Bukkit.getPlayer(activeViewers.iterator().next());
        if (dummyPlayer == null) return;

        PacketContainer animationPacket = packetFactory.buildSwingArmPacket(npcEntityId, dummyPlayer);
        sendPacketToAll(animationPacket);
    }

    private void sendItemInHand(ShowItemAction s) {
        if (activeViewers.isEmpty()) return;
        Material material = Material.valueOf(s.material());

        PacketContainer equipmentPacket = packetFactory.buildItemPacket(npcEntityId, material);
        sendPacketToAll(equipmentPacket);
    }

    private void sendPacketToAll(PacketContainer... packets) {
        if (activeViewers.isEmpty()) return;

        for (UUID uuid : activeViewers) {
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null && viewer.isOnline()) {
                for (PacketContainer packet : packets) {
                    if (packet != null) {
                        protocolManager.sendServerPacket(viewer, packet);
                    }
                }
            }
        }
    }

}