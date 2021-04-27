package pku.yim.findchest.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pku.yim.findchest.finder.ChestFinder;


import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class SpawnGlowingEntity {
    public static void spawnShulker(Player player, Location location, int entityId) throws InvocationTargetException {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer spawn_shulker = manager.createPacketConstructor(PacketType.Play.Server.SPAWN_ENTITY_LIVING).createPacket();
        spawn_shulker.getIntegers().write(0,entityId).write(1, ChestFinder.shulker_id);
        spawn_shulker.getUUIDs().write(0,UUID.randomUUID());
        spawn_shulker.getDoubles().write(0,location.getX()+0.5).write(1,location.getY()-0.125).write(2,location.getZ()+0.5);
        if(NMSVersion.isGreaterEqualThan(NMSVersion.v1_15_R1)){
            manager.sendServerPacket(player, spawn_shulker);
            PacketContainer meta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
            meta.getModifier().writeDefaults();
            meta.getIntegers().write(0, entityId);
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher(meta.getWatchableCollectionModifier().read(0));
            WrappedDataWatcher.WrappedDataWatcherObject Effect = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
            dataWatcher.setObject(Effect, (byte) 96);
            WrappedDataWatcher.WrappedDataWatcherObject NoGravity = new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class));
            dataWatcher.setObject(NoGravity, true);
            meta.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
            manager.sendServerPacket(player, meta);
        }else{
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
            WrappedDataWatcher.WrappedDataWatcherObject Effect = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
            dataWatcher.setObject(Effect, (byte) 96);
            WrappedDataWatcher.WrappedDataWatcherObject NoGravity = new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class));
            dataWatcher.setObject(NoGravity, true);
            spawn_shulker.getDataWatcherModifier().write(0,dataWatcher);
            manager.sendServerPacket(player,spawn_shulker);
        }
    }

}
