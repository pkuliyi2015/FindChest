package pku.yim.findchest;


import org.bukkit.GameMode;
import pku.yim.findchest.finder.ChestFinder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import pku.yim.findchest.util.Metrics;

import java.util.HashMap;


public class FindChest extends JavaPlugin {
    private static FindChest instance;
    public static boolean enable;

    public HashMap<Player,Long> lastUseTime = new HashMap<>();
    public FindChest() {
        instance = this;
    }
    public static FindChest getInstance() {
        return instance;
    }


    public void onEnable() {
        enable = true;
        Metrics metrics = new Metrics(this,11144);
        this.getLogger().info("====================命令指南=====================");
        this.getLogger().info("/findchest -- 在附近箱子中寻找与手中物品相同的物品");
        this.getLogger().info("/findchest full -- 寻找满箱");
        this.getLogger().info("===============================================");
        ChestFinder.start();
    }

    public void onDisable() {
        enable = false;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            this.getLogger().info("====================命令指南=====================");
            this.getLogger().info("/findchest -- 在附近箱子中寻找与手中物品相同的物品，空手则找任意箱子");
            this.getLogger().info("/findchest full -- 寻找满箱(必须空手）");
            this.getLogger().info("===============================================");
        }else{
            Player player = (Player) sender;
            if(!player.hasPermission("findchest"))return true;
            lastUseTime.putIfAbsent(player, (long)0);
            long time = System.currentTimeMillis()-lastUseTime.get(player);
            if(player.getGameMode()== GameMode.CREATIVE||time>6000) {
                lastUseTime.put(player,System.currentTimeMillis());
                ChestFinder.FindChest(player, args.length == 1);
            }else{
                player.sendMessage(String.format("您还需要%d毫秒才能使用本功能", time));
            }
        }

        return true;
    }
}
