/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.chrisbotcom.customvillager;

import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 *
 * @author chrisbot
 */
public class PathfinderGoalWalkToLocation extends PathfinderGoal {

    private final double speed;
    private final EntityInsentient entity;
    private final Location location;
    private boolean flipflop;
    
    public PathfinderGoalWalkToLocation(EntityInsentient entity, Location location, double speed) {
        
        this.entity = entity;
        this.location = location;
        this.speed = speed;
    }
    
    @Override
    public boolean a() { // should start

        //Bukkit.getServer().getPlayer(UUID.fromString("7459e9b5-21a6-4a59-9025-0413e4d41cda")).sendMessage(String.valueOf(entity.locX)); 
        Vector v = new Vector(entity.locX, entity.locY, entity.locZ);
        Vector min = location.getBlock().getLocation().toVector().subtract(new Vector(1,1,1));
        Vector max = location.getBlock().getLocation().toVector().add(new Vector(1,1,1));
        if (!v.isInAABB(min, max)) {
            
            flipflop = !flipflop;
        }
        else {
            
            flipflop = false;
        }
        
        return flipflop;
    }
 
    @Override
    public void c() { // on start
        
        //Bukkit.getServer().getPlayer(UUID.fromString("7459e9b5-21a6-4a59-9025-0413e4d41cda")).sendMessage("c()");
        //this.entity.getNavigation().a(location.getX(), location.getY(), location.getZ());
        this.entity.getControllerMove().a(location.getX(), location.getY(), location.getZ(), speed);
    }
}
