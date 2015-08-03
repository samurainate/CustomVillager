/*
 * Copyright (C) 2014 Chris Courson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.chrisbotcom.customvillager.v1_7_R2;

import net.minecraft.server.v1_7_R2.EntityInsentient;
import net.minecraft.server.v1_7_R2.PathfinderGoal;
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
