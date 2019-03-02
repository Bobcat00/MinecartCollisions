// MinecartCollisions - Prevents minecarts from colliding with mobs or abandoned minecarts
// Copyright 2015-2018 Bobcat00
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.bobcat00.minecartcollisions;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public final class Listeners implements Listener
{
    @SuppressWarnings("unused")
    private MinecartCollisions plugin;
    
    public Listeners(MinecartCollisions plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleMove(VehicleMoveEvent event)
    {
        if (!(event.getVehicle() instanceof Minecart)) return;
        
        Minecart minecart = (Minecart) event.getVehicle();
        
        if (!minecart.isEmpty())
        {
            Player player = null;
            for (Entity passenger : minecart.getPassengers())
            {
                if (passenger instanceof Player)
                {
                    player = (Player) passenger;
                    break;
                }
            }
            
            if (player != null && !player.hasPermission("minecartcollisions.exempt"))
            {
                // Player is riding in a minecart and player has the required permission
                // Now find nearby entities and decide what, if anything, to do
                
                Location minecartLocation = event.getTo();
                double minecartX = minecartLocation.getX();
                double minecartZ = minecartLocation.getZ();
                
                Location minecartFromLocation = event.getFrom();
                double minecartFromX = minecartFromLocation.getX();
                double minecartFromZ = minecartFromLocation.getZ();
                
                // Check each nearby entity
                for (Entity entity : minecart.getNearbyEntities(1.0, 1.0, 1.0))
                {
                    if ((entity instanceof Monster) ||
                        (entity instanceof Animals) ||
                        (entity instanceof NPC)     ||
                        (entity instanceof Slime))
                    {
                        // Handle spider jockeys, chicken jockeys, and skeleton traps
                        if (((entity.getType() == EntityType.SPIDER) &&
                             !entity.isEmpty() && (entity.getPassengers().get(0) instanceof Skeleton)) ||
                            ((entity.getType() == EntityType.CHICKEN) &&
                             !entity.isEmpty() && (entity.getPassengers().get(0) instanceof Zombie)) ||
                            ((entity.getType() == EntityType.SKELETON_HORSE) &&
                             !entity.isEmpty() && (entity.getPassengers().get(0) instanceof Skeleton)))
                        {
                            entity.eject();
                            if (player.hasPermission("minecartcollisions.debug"))
                            {
                                player.sendMessage(ChatColor.LIGHT_PURPLE + "[MinecartCollisions] " + ChatColor.WHITE + 
                                        "Ejected passengers from " + entity.getType());
                            }
                        }
                        
                        if (!entity.isInsideVehicle() && entity.isEmpty())
                        {
                            // Entity is a monster, animal, NPC, or slime, is not riding in a vehicle and has no passengers.
                            // Move the entity further away from the minecart.
                            // This may kill the entity (sucks to be him!).
                            
                            Location entityLocation = entity.getLocation();
                            double entityX = entityLocation.getX();
                            double entityZ = entityLocation.getZ();
                            double newX = entityX;
                            double newZ = entityZ;
                            
                            if ((minecartFromX == minecartX) || (minecartFromZ == minecartZ))
                            {
                                // Minecart moving straight, move entity further away on a diagonal by changing X and Z
                                
                                if (entityX < minecartX)
                                    newX = entityX - 1.0;
                                else
                                    newX = entityX + 1.0;
                                
                                if (entityZ < minecartZ)
                                    newZ = entityZ - 1.0;
                                else
                                    newZ = entityZ + 1.0;
                            }
                            else
                            {
                                // Minecart moving on a diagonal, so move entity to the side by changing X or Z
                                
                                if (Math.abs(minecartX-entityX) > Math.abs(minecartZ-entityZ))
                                {
                                    if (entityX < minecartX)
                                        newX = entityX - 1.0;
                                    else
                                        newX = entityX + 1.0;
                                }
                                else
                                {
                                    if (entityZ < minecartZ)
                                        newZ = entityZ - 1.0;
                                    else
                                        newZ = entityZ + 1.0;
                                }
                            }
                            
                            entityLocation.setX(newX);
                            entityLocation.setZ(newZ);
                            entity.teleport(entityLocation);
                            
                            if (player.hasPermission("minecartcollisions.debug"))
                            {
                                DecimalFormat df = new DecimalFormat();
                                df.setMinimumFractionDigits(2);
                                df.setMaximumFractionDigits(2);
    
                                player.sendMessage(ChatColor.LIGHT_PURPLE + "[MinecartCollisions] " + ChatColor.WHITE + 
                                        "Teleported " + entity.getType() + " to " +
                                        df.format(newX) + ", " + df.format(newZ));
                            }
                            
                        } // end if entity is inside vehicle
                        
                    }
                    else if ((entity instanceof RideableMinecart) && entity.isEmpty())
                    {
                        // Remove abandoned minecart blocking the tracks
                        entity.remove();
                        
                        if (player.hasPermission("minecartcollisions.debug"))
                        {
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "[MinecartCollisions] " + ChatColor.WHITE + 
                                    "Removed " + entity.getType());
                        }
                        
                    }
                   
               } // end for each entity
                
            } // end if minecart contains player and player has permission

        } // end if minecart is not empty
        
    } // end event handler
    
}