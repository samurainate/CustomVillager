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
package io.github.chrisbotcom.customvillager;

//import io.github.chrisbotcom.customvillager.v1_7_R3.Merchant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

// net.minecraft.v1_7_R3, .v1_7_R2, .v1_7_R1
/**
 *
 * @author chrisbot
 */
public final class CustomVillager extends JavaPlugin implements Listener {

    public NMSOBCInterface nmsobcHandler;
    public FileConfiguration config;
    public Map<Player, Villager> villagers = new HashMap<>();

    @Override
    public void onEnable() {

        // Get net.minecraft version and setup appropriate handler
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        this.getLogger().log(Level.INFO, "CustomVillager implementing NMS and OCB interface for {0}.", version);
        try {
            final Class<?> clazz = Class.forName("io.github.chrisbotcom.customvillager." + version + ".NMSOBCHandler");
            // Verify class for this version of NMS exists.
            if (NMSOBCInterface.class.isAssignableFrom(clazz)) {
                this.nmsobcHandler = (NMSOBCInterface) clazz.getConstructor().newInstance();
            }
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "CustomVillager does not support CraftBukkit version ({0}). Please, contact author and request support.", version);
            this.setEnabled(false);
            return;
        }

        // Load config
        this.config = this.getConfig();
        this.config.options().copyDefaults(true);
        this.saveConfig();

        // Restore villager confines
        restoreConfines();

        // Register commands
        this.getCommand("cvspawn").setExecutor(new Commands(this));
        this.getCommand("cvselect").setExecutor(new Commands(this));
        this.getCommand("cvrelease").setExecutor(new Commands(this));
        this.getCommand("cvkill").setExecutor(new Commands(this));
        this.getCommand("cvname").setExecutor(new Commands(this));
        this.getCommand("cvprofession").setExecutor(new Commands(this));
        this.getCommand("cvinvulnerable").setExecutor(new Commands(this));
        this.getCommand("cvinventory").setExecutor(new Commands(this));
        this.getCommand("cvbuy").setExecutor(new Commands(this));
        this.getCommand("cvsell").setExecutor(new Commands(this));
        this.getCommand("cvclear").setExecutor(new Commands(this));
        this.getCommand("cvadd").setExecutor(new Commands(this));
        this.getCommand("cvdelete").setExecutor(new Commands(this));
        this.getCommand("cvconfine").setExecutor(new Commands(this));

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();

        if (this.villagers.containsKey(player)
                && this.villagers.get(player) == null
                && event.getRightClicked().getType() == EntityType.VILLAGER) {

            this.villagers.put(player, (Villager) event.getRightClicked());
            player.sendMessage(ChatColor.GOLD + "Villager selected.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        // Restore confines
        World world = event.getWorld();
        String path = world.getName();

        if (config.contains(path)) {
            path += ".";
            for (Entity entity : event.getChunk().getEntities()) {
                if (entity.getType() == EntityType.VILLAGER) {
                    Villager villager = (Villager) entity;
                    if (config.contains(path + villager.getUniqueId())) {
                        this.nmsobcHandler.confineMerchant(villager, getConfigMerchantLocation(villager));
                    }
                }
            }
        }
    }

    public void saveConfigMerchant(Villager villager, Location location) {

        String path = location.getWorld().getName() + "." + villager.getUniqueId().toString();

        config.set(path + ".name", villager.getCustomName());
        config.set(path + ".vector", location.toVector());
        saveConfig();
    }

    public Boolean hasConfigMerchant(Villager villager, World world) {

        return config.contains(world.getName() + "." + villager.getUniqueId().toString());
    }

    public void deleteConfigMerchant(Villager villager) {

        String path = villager.getWorld().getName() + "." + villager.getUniqueId().toString();
        config.set(path, null);
    }

    public Location getConfigMerchantLocation(Villager villager) {

        String path = villager.getWorld().getName() + "." + villager.getUniqueId().toString() + ".vector";
        return config.getVector(path).toLocation(villager.getWorld());
    }

    public List<String> getConfigMerchantUUIDs(World world) {

        if (config.contains(world.getName())) {
            Set<String> keys = config.getConfigurationSection(world.getName()).getKeys(false);
            return new ArrayList<>(keys);
        }
        return null;
    }

    private void restoreConfines() {

        // need to change this to clean config only after testing that chunkgen will restore confines on restart.   
        for (World world : this.getServer().getWorlds()) {

            String path = world.getName();

            if (config.contains(path)) {

                //ArrayList <String> uuidList = new ArrayList<>(config.getConfigurationSection(world.getName()).getKeys(false));
                path += ".";

                for (Villager villager : world.getEntitiesByClass(Villager.class)) {

                    if (config.contains(path + villager.getUniqueId())) {

                        //uuidList.remove(villager.getUniqueId().toString());
                        this.nmsobcHandler.confineMerchant(villager, getConfigMerchantLocation(villager));
                    }
                }

//                for (String uuid : uuidList) {
//                    
//                    config.set(path + uuid, null);
//                }
                this.saveConfig();
            }
        }
    }
}
