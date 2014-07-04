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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

/**
 *
 * @author chrisbot
 */
class Commands implements CommandExecutor {

    private final CustomVillager plugin;
    private Villager lastSelect = null;

    public Commands(CustomVillager plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {

        Player player = (Player) cs;

        try {
            // cvspawn [name] [profession]
            if (cmnd.getName().equalsIgnoreCase("cvspawn")) {
                if (args.length > 2) {
                    throw new Exception("Too many arguments in cvspawn command.");
                }

                String arg1 = args.length > 0 ? args[0] : null;
                String arg2 = args.length > 1 ? args[1] : null;
                String name = null;
                Profession profession = null;

                if (arg1 != null) {
                    try {
                        profession = Profession.valueOf(arg1.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        name = arg1;
                    }

                    if (arg2 != null) {

                        if (profession == null) {
                            try {
                                profession = Profession.valueOf(arg2.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new Exception("Profession not defined.");
                            }
                        } else if (name == null) {
                            name = arg2;
                        } else {
                            throw new Exception("Profession not defined.");
                        }
                    }
                }

                Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);
                plugin.villagers.put(player, villager);
                plugin.saveConfigMerchant(villager, player.getLocation());

                if (name != null) {
                    villager.setCustomName(name);
                    villager.setCustomNameVisible(true);
                }
                if (profession != null) {
                    villager.setProfession(profession);
                }
            }

            // cvlist
            if (cmnd.getName().equalsIgnoreCase("cvlist")) {

            }

            // cvselect
            if (cmnd.getName().equalsIgnoreCase("cvselect")) {
                if (args.length != 0) {
                    throw new Exception("cvselect does not accept arguments.");
                }
                this.lastSelect = plugin.villagers.get(player);
                plugin.villagers.put(player, null);
                player.sendMessage(ChatColor.GOLD + "Right-click villager to select.");
            }

            // cvcancel
            if (cmnd.getName().equalsIgnoreCase("cvcancel")) {
                if (args.length != 0) {
                    throw new Exception("cvselect does not accept arguments.");
                }
                plugin.villagers.put(player, lastSelect);
                player.sendMessage(ChatColor.GOLD + "Select cancelled and last selection restored.");
            }

            // cvrelease
            if (cmnd.getName().equalsIgnoreCase("cvrelease")) {
                if (args.length != 0) {
                    throw new Exception("cvrelease does not accept arguments.");
                }
                plugin.villagers.remove(player);
                player.sendMessage(ChatColor.GOLD + "Villager released.");
            }

            // cvkill
            if (cmnd.getName().equalsIgnoreCase("cvkill")) {
                if (args.length != 0) {
                    throw new Exception("cvkill does not accept arguments.");
                }
                plugin.villagers.get(player).remove();

                Villager villager = plugin.villagers.get(player);
                String path = player.getWorld().getName() + "." + villager.getUniqueId();
                plugin.config.set(path, null);
                plugin.saveConfig();

                player.sendMessage(ChatColor.GOLD + "Villager removed.");
            }

            // cvname <name>
            if (cmnd.getName().equalsIgnoreCase("cvname")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }

                if (args.length > 0) {
                    String name = "";
                    for (String word : args) {
                        name += " " + word;
                    }

                    name = name.replace('&', '\u00A7').trim();

                    plugin.villagers.get(player).setCustomName(name);
                    plugin.villagers.get(player).setCustomNameVisible(true);
                } else {
                    plugin.villagers.get(player).setCustomName("");
                    plugin.villagers.get(player).setCustomNameVisible(false);
                }
            }

            // cvprofession [profession]
            if (cmnd.getName().equalsIgnoreCase("cvprofession")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                // FARMER|LIBRARIAN|PRIEST|BLACKSMITH|BUTCHER
                if (args.length > 1) {
                    throw new Exception("cvprofession accepts one profession argument (FARMER|LIBRARIAN|PRIEST|BLACKSMITH|BUTCHER) or no argument to return profession.");
                }
                if (args.length == 1) {
                    Profession profession;
                    try {
                        profession = Profession.valueOf(args[0].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new Exception("Profession not defined.");
                    }
                    plugin.villagers.get(player).setProfession(profession);
                } else {
                    player.sendMessage(ChatColor.GOLD + "Villager is a " + plugin.villagers.get(player).getProfession().toString() + ".");
                }
            }

            // cvinvulnerable|cvgod [on|off]
            if (cmnd.getName().equalsIgnoreCase("cvinvulnerable")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length > 1) {
                    throw new Exception("cvinvulnerable accepts one argument (ON|OFF) no argument to return vulnerablility.");
                }

                Villager villager = plugin.villagers.get(player);

                Boolean invulnerable = plugin.nmsobcHandler.getMerchantInvulnerable(villager);
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("on")) {
                        invulnerable = true;
                    } else if (args[0].equalsIgnoreCase("off")) {
                        invulnerable = false;
                    } else {
                        throw new Exception("Argument must be ON or OFF");
                    }
                    plugin.nmsobcHandler.setMerchantInvulnerable(villager, invulnerable);
                }
                player.sendMessage(ChatColor.GOLD + "Villiager invulnerable is set to " + invulnerable.toString() + ".");
            }

            // cvi[nventory]
            if (cmnd.getName().equalsIgnoreCase("cvinventory")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length > 1) {
                    throw new Exception("cvinventory accepts no argument.");
                }

                Villager villager = plugin.villagers.get(player);
                player.sendMessage(plugin.nmsobcHandler.getMerchantInventory(villager));
            }

            // cvbuy
            if (cmnd.getName().equalsIgnoreCase("cvbuy")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length > 1) {
                    throw new Exception("cvbuy accepts no argument.");
                }
                if (player.getEquipment().getItemInHand().getType().equals(Material.AIR)) {
                    throw new Exception("Cannot add AIR to recipe.");
                }

                plugin.nmsobcHandler.addBuyItem(player);
            }

            // cvsell
            if (cmnd.getName().equalsIgnoreCase("cvsell")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length > 1) {
                    throw new Exception("cvsell accepts no argument.");
                }
                if (player.getEquipment().getItemInHand().getType().equals(Material.AIR)) {
                    throw new Exception("Cannot add AIR to recipe.");
                }
                
                plugin.nmsobcHandler.addSellItem(player);
            }

            // cvclear
            if (cmnd.getName().equalsIgnoreCase("cvclear")) {
                if (args.length > 1) {
                    throw new Exception("cvbuy accepts no argument.");
                }
                plugin.nmsobcHandler.clearMerchantRecipe();
                player.sendMessage(ChatColor.GOLD + "Buy and sell items cleared.");
            }

            // cvadd
            if (cmnd.getName().equalsIgnoreCase("cvadd")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length > 1) {
                    throw new Exception("cvadd accepts no argument.");
                }

                plugin.nmsobcHandler.addMerchantRecipe(plugin.villagers.get(player));

                player.sendMessage(ChatColor.GOLD + "Recipe added to villager.");

                plugin.nmsobcHandler.clearMerchantRecipe();
            }

            // cvdelete
            if (cmnd.getName().equalsIgnoreCase("cvdelete")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length != 1) {
                    throw new Exception("cvdelete requires one index argument.");
                }
                int index;
                try {
                    index = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    throw new Exception("Index is not a number.");
                }

                plugin.nmsobcHandler.deleteMerchantRecipe(plugin.villagers.get(player), index);

                player.sendMessage(ChatColor.GOLD + "Recipe deleted.");
            }

            // cvconfine
            if (cmnd.getName().equalsIgnoreCase("cvconfine")) {
                if (!plugin.villagers.containsKey(player)) {
                    throw new Exception("Villager not selected.");
                }
                if (args.length > 1) {
                    throw new Exception("cvdelete accepts no argument.");
                }

                Villager villager = plugin.villagers.get(player);

                plugin.nmsobcHandler.confineMerchant(villager, player.getLocation());

                String path = player.getWorld().getName() + "." + villager.getUniqueId() + ".";
                plugin.config.set(path + "name", villager.getCustomName());
                plugin.config.set(path + "vector", player.getLocation().toVector());
                plugin.saveConfig();

                player.sendMessage(ChatColor.GOLD + "Confine set for villager.");
            }
        } catch (Exception ex) {

            player.sendMessage(ChatColor.RED + ex.getMessage());
        }
        return true;
    }
}
