package pku.yim.findchest.finder;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pku.yim.findchest.FindChest;
import pku.yim.findchest.util.*;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ChestFinder {

    public static FileConfiguration config = null;
    public static boolean protocolLib = false;
    public static Plugin plugin;
    public static int chest_effect;
    public static int chunkDistance;
    public static int chunkChestLimit;
    public static int shulker_id;

    private static class GlowBlock {
        Player player;
        Location location;
        int id;

        GlowBlock(Player p, Location loc, int eid) {
            player = p;
            location = loc;
            id = eid;
        }
    }

    public static ConcurrentLinkedQueue<GlowBlock> toGlow = new ConcurrentLinkedQueue<>();

    public static boolean isEnable() {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    public static void log(String s) {
        plugin.getLogger().info(s);
    }


    public static void start() {
        plugin = FindChest.getInstance();
        config = plugin.getConfig();
        config.addDefault("chunk_distance", 1);
        config.addDefault("chest_effect", 1);
        config.addDefault("chunk_chest_limit", 128);
        config.options().copyDefaults(true);
        plugin.saveConfig();
        chest_effect = config.getInt("chest_effect");
        chunkDistance = config.getInt("chunk_distance");
        chunkChestLimit = config.getInt("chunk_chest_limit");

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            protocolLib = true;
        } else {
            log("ProtocolLib not found.");
            log("Full chest tips switched to particle effect.");
        }
        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_16_R2)) shulker_id = 70;
        else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_16_R1)) shulker_id = 69;
        else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_15_R1)) shulker_id = 63;
        else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_14_R1)) shulker_id = 62;
        else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_13_R1)) shulker_id = 59;
        else shulker_id = 69;
    }


    public static void FindChest(Player player, boolean fullchest) {
        if (!isEnable()) return;
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();
        ArrayList<Chunk> chunks = new ArrayList<>();
        for (int i = -chunkDistance; i <= chunkDistance; i++) {
            for (int j = -chunkDistance; j <= chunkDistance; j++) {
                try {
                    chunks.add(world.getChunkAt(chunk.getX() + i, chunk.getZ() + j));
                } catch (Exception e) {
                }
            }
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType()!= Material.AIR) player.sendMessage("正在附近箱子中寻找"+itemStack.getType().toString());
        else if(fullchest){
            player.sendMessage("正在附近寻找满箱");
        }else{
            player.sendMessage("正在附近寻找空箱");
        }
        for (Chunk c : chunks) {
            int chunk_chest_count = 0;
            for (BlockState e : c.getTileEntities()) {
                if (e instanceof Chest) {
                    Chest chest = (Chest) e;
                    if (chunk_chest_count > chunkChestLimit) {
                        player.sendMessage(String.format("区块(%d, %d)箱子超过上限，停止扫描该区块", c.getX(), c.getZ()));
                        break;
                    }
                    chunk_chest_count++;
                    if (itemStack.getType() == Material.AIR) {
                        if (!fullchest) {
                            toGlow.add(new GlowBlock(player, e.getLocation(),
                                    ThreadLocalRandom.current().nextInt(10000, Integer.MAX_VALUE)));
                        } else if (chest.getInventory().firstEmpty() == -1) {
                            toGlow.add(new GlowBlock(player, e.getLocation(),
                                    ThreadLocalRandom.current().nextInt(10000, Integer.MAX_VALUE)));
                        }
                        continue;
                    }
                    for (ItemStack i : chest.getInventory()) {
                        if (itemStack.isSimilar(i)) {
                            toGlow.add(new GlowBlock(player, e.getLocation(),
                                    ThreadLocalRandom.current().nextInt(10000, Integer.MAX_VALUE)));
                            break;
                        }
                    }
                }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (chest_effect == 1 && protocolLib) {

                    ArrayList<Integer> toDestroy = new ArrayList<>();
                    for (GlowBlock b : toGlow) {
                        SpawnGlowingEntity.spawnShulker(player, b.location, b.id);
                        toDestroy.add(b.id);
                    }
                    Thread.sleep(6000);
                    int[] arr = toDestroy.stream().mapToInt(Integer::intValue).toArray();
                    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                    PacketContainer destroy_fb = manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                    destroy_fb.getIntegerArrays().write(0, arr);
                    manager.sendServerPacket(player, destroy_fb);
                    toGlow.clear();
                } else {
                    for (int i = 0; i < 3; i++) {
                        for (GlowBlock b : toGlow) {
                            Utils.drawDistanceLine(player, b.location.clone().add(0.5, 0.5, 0.5), 0.45, 1);
                        }
                        Thread.sleep(2000);
                    }
                    toGlow.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
