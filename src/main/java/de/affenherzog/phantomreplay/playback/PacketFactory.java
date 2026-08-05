package de.affenherzog.phantomreplay.playback;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import de.affenherzog.phantomreplay.replay.Position;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class PacketFactory {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();


    public PacketContainer buildInfoPackage(UUID npcUUID, String npcName) {
        PacketContainer gameProfilePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(npcUUID, npcName);

        PlayerInfoData data = new PlayerInfoData(
                npcUUID,
                0,
                true,
                EnumWrappers.NativeGameMode.SURVIVAL,
                wrappedGameProfile,
                WrappedChatComponent.fromText(npcName),
                true,
                9999,
                null
        );

        gameProfilePacket.getPlayerInfoActions().write(0, Collections.singleton(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        gameProfilePacket.getPlayerInfoDataLists().write(1, Collections.singletonList(data));

        return gameProfilePacket;
    }

    public PacketContainer buildSpawnPacket(UUID npcUUID, int npcEntityId, Position pos) {
        PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        spawnPacket.getIntegers().write(0, npcEntityId);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.PLAYER);
        spawnPacket.getUUIDs().write(0, npcUUID);


        spawnPacket.getDoubles()
                .write(0, pos.x())
                .write(1, pos.y())
                .write(2, pos.z());

        byte yaw = (byte) (pos.yaw() * 256.0F / 360.0F);
        byte pitch = (byte) (pos.pitch() * 256.0F / 360.0F);

        spawnPacket.getBytes()
                .write(0, yaw)
                .write(1, pitch)
                .write(2, (byte) 0);


        return spawnPacket;
    }

    public PacketContainer buildMovePacket(int npcEntityId, Position oldPos, Position newPos) {
        PacketContainer movePacket = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);

        movePacket.getIntegers().write(0, npcEntityId);

        long deltaX = (long) (newPos.x() * 4096) - (long) (oldPos.x() * 4096);
        long deltaY = (long) (newPos.y() * 4096) - (long) (oldPos.y() * 4096);
        long deltaZ = (long) (newPos.z() * 4096) - (long) (oldPos.z() * 4096);

        movePacket.getShorts()
                .write(0, (short) deltaX)
                .write(1, (short) deltaY)
                .write(2, (short) deltaZ);

        byte yaw = (byte) (newPos.yaw() * 256.0F / 360.0F);
        byte pitch = (byte) (newPos.pitch() * 256.0F / 360.0F);

        movePacket.getBytes()
                .write(0, yaw)
                .write(1, pitch);

        movePacket.getBooleans().write(0, true);

        return movePacket;
    }

    public PacketContainer buildHeadRotationPacket(int npcEntityId, Position newPos, Player dummy) {

        PacketContainer headPacket = protocolManager.createPacketConstructor(
                PacketType.Play.Server.ENTITY_HEAD_ROTATION,
                dummy,
                (byte) 0
        ).createPacket(dummy, (byte) 0);

        headPacket.getIntegers().write(0, npcEntityId);

        byte headYaw = (byte) (newPos.yaw() * 256.0F / 360.0F);
        headPacket.getBytes().write(0, headYaw);

        return headPacket;
    }

    public PacketContainer buildDestroyPacket(int npcEntityId) {
        PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, Collections.singletonList(npcEntityId));
        return destroyPacket;
    }

    public PacketContainer buildInfoRemovePacket(UUID npcUUID) {
        PacketContainer removeInfoPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        removeInfoPacket.getUUIDLists().write(0, Collections.singletonList(npcUUID));
        return removeInfoPacket;
    }


}
