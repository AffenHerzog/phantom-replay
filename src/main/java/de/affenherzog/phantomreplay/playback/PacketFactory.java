package de.affenherzog.phantomreplay.playback;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import de.affenherzog.phantomreplay.replay.Position;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public PacketContainer buildMetadataPacket(int npcEntityId, boolean sneaking, boolean sprinting) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, npcEntityId);

        List<WrappedDataValue> dataValues = new ArrayList<>();

        byte flags = 0;
        if (sneaking) flags |= 0x02;
        if (sprinting) flags |= 0x08;

        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get((Type) Byte.class);
        dataValues.add(new WrappedDataValue(0, byteSerializer, flags));

        Object nmsPose = (sneaking ? EnumWrappers.EntityPose.CROUCHING : EnumWrappers.EntityPose.STANDING).toNms();
        WrappedDataWatcher.Serializer poseSerializer = WrappedDataWatcher.Registry.get((Type) nmsPose.getClass());
        dataValues.add(new WrappedDataValue(6, poseSerializer, nmsPose));
        packet.getDataValueCollectionModifier().write(0, dataValues);

        return packet;
    }

    public PacketContainer buildSwingArmPacket(int npcEntityId, Player dummyPlayer) {

        PacketContainer packetContainer = protocolManager.createPacketConstructor(
                PacketType.Play.Server.ANIMATION,
                dummyPlayer,
                0
        ).createPacket(dummyPlayer, 0);

        packetContainer.getIntegers().write(0, npcEntityId);
        packetContainer.getIntegers().write(1, 0);

        return packetContainer;
    }

    public PacketContainer buildItemPacket(int npcEntityId, Material material) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, npcEntityId);

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
        ItemStack itemStack = new ItemStack(material != null ? material : Material.AIR);

        pairs.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, itemStack));
        packet.getSlotStackPairLists().writeSafely(0, pairs);

        return packet;
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
