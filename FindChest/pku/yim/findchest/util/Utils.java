package pku.yim.findchest.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;


public class Utils {
    public static void drawDistanceLine(Player player, Location loc, double distance, int amount) {
        Location[] locs = new Location[12];
        for (int i = 0; i < 12; i++) {
            locs[i] = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        }
        locs[0].add(0, -distance, -distance);
        locs[1].add(0, -distance, distance);
        locs[2].add(0, distance, -distance);
        locs[3].add(0, distance, distance);
        locs[4].add(distance, 0, -distance);
        locs[5].add(distance, 0, distance);
        locs[6].add(-distance, 0, -distance);
        locs[7].add(-distance, 0, distance);
        locs[8].add(-distance, -distance, 0);
        locs[9].add(-distance, distance, 0);
        locs[10].add(distance, -distance, 0);
        locs[11].add(distance, distance, 0);
        for (int i = 0; i < 4; i++) {
            for(double pt = -distance; pt<=distance; pt+=2*distance/amount){
                player.spawnParticle(Particle.VILLAGER_HAPPY,locs[i].getX()+pt,locs[i].getY(),locs[i].getZ(),1);
            }
        }
        for (int i = 4; i < 8; i++){
            for(double pt = -distance; pt<=distance; pt+=2*distance/amount){
                player.spawnParticle(Particle.VILLAGER_HAPPY,locs[i].getX(),locs[i].getY()+pt,locs[i].getZ(),1);
            }
        }
        for (int i = 8; i < 12; i++){
            for(double pt = -distance; pt<=distance; pt+=2*distance/amount){
                player.spawnParticle(Particle.VILLAGER_HAPPY,locs[i].getX(),locs[i].getY(),locs[i].getZ()+pt,1);
            }
        }
    }


}
