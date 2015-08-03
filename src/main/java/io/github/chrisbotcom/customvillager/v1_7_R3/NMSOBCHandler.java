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
package io.github.chrisbotcom.customvillager.v1_7_R3;

import io.github.chrisbotcom.customvillager.NMSOBCInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

/**
 *
 * @author chrisbot
 */
public class NMSOBCHandler implements NMSOBCInterface {

    @Override
    public void addBuyItem(Player player) {
        if (Merchant.buy1 == null) {
            Merchant.buy1 = CraftItemStack.asNMSCopy(player.getEquipment().getItemInHand());
            player.sendMessage(ChatColor.GOLD + "Buy item 1 set.");
        } else if (Merchant.buy2 == null) {
            if (CraftItemStack.asBukkitCopy(Merchant.buy1).getType() == player.getEquipment().getItemInHand().getType()) {
                player.sendMessage(ChatColor.RED + "Item 1 already contains this item.");
                return;
            }
            Merchant.buy2 = CraftItemStack.asNMSCopy(player.getEquipment().getItemInHand());
            player.sendMessage(ChatColor.GOLD + "Buy item 2 set.");
        } else {
            player.sendMessage(ChatColor.RED + "Buy items already set. Use /vmclear to clear.");
        }
    }

    @Override
    public void addSellItem(Player player) {
        if (Merchant.sell == null) {
            Merchant.sell = CraftItemStack.asNMSCopy(player.getEquipment().getItemInHand());
            player.sendMessage(ChatColor.GOLD + "Sell item set.");
        } else {
            player.sendMessage(ChatColor.RED + "Sell item already set. Use /vmclear to clear.");
        }
    }

    @Override
    public boolean getMerchantInvulnerable(Villager villager) {
        return Merchant.getInvulnerable(villager);
    }

    @Override
    public void setMerchantInvulnerable(Villager villager, Boolean value) {
        Merchant.setInvulnerable(villager, value);
    }

    @Override
    public String[] getMerchantInventory(Villager villager) {
        try {
            return Merchant.getInventory(villager);
        } catch (Exception ex) {
            Logger.getLogger(NMSOBCHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void clearMerchantRecipe() {
        Merchant.buy1 = null;
        Merchant.buy2 = null;
        Merchant.sell = null;
        Merchant.maxUses = Integer.MAX_VALUE;
    }

    @Override
    public void addMerchantRecipe(Villager villager) {
        try {
            Merchant.addRecipe(villager);
        } catch (Exception ex) {
            Logger.getLogger(NMSOBCHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteMerchantRecipe(Villager villager, int index) {
        try {
            Merchant.deleteRecipe(villager, index);
        } catch (Exception ex) {
            Logger.getLogger(NMSOBCHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void confineMerchant(Villager villager, Location location) {
        try {
            Merchant.confine(villager, location);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(NMSOBCHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
