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

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecartCollisions extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        
        // Metrics
        int pluginId = 4861;
        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info("Metrics enabled if allowed by plugins/bStats/config.yml");
    }
 
    @Override
    public void onDisable()
    {
        // HandlerList.unregisterAll(listeners);
    }

}