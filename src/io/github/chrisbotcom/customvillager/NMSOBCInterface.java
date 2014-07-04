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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

/**
 *
 * @author chrisbot
 */
public interface NMSOBCInterface {
    public void addBuyItem(Player player);
    public void addSellItem(Player player);
    public boolean getMerchantInvulnerable(Villager villager);
    public void setMerchantInvulnerable(Villager villager, Boolean value);
    public String[] getMerchantInventory(Villager villager);
    public void clearMerchantRecipe();
    public void addMerchantRecipe(Villager villager);
    public void deleteMerchantRecipe(Villager villager, int index);
    public void confineMerchant(Villager villager, Location location);       
}
