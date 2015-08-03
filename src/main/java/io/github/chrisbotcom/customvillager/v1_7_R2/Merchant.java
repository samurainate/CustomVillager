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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.server.v1_7_R2.EntityHuman;
import net.minecraft.server.v1_7_R2.EntityVillager;
import net.minecraft.server.v1_7_R2.ItemStack;
import net.minecraft.server.v1_7_R2.MerchantRecipe;
import net.minecraft.server.v1_7_R2.MerchantRecipeList;
import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R2.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.server.v1_7_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R2.PathfinderGoalTradeWithPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Villager;

/**
 *
 * @author chrisbot
 */
public class Merchant {
    
    public static ItemStack buy1 = null;
    public static ItemStack buy2 = null;
    public static ItemStack sell = null;
    public static int maxUses = 3;
        
    public static void setInvulnerable(Villager villager, Boolean value) {
        
        EntityVillager nmsVillager = ((CraftVillager)villager).getHandle();            

        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        
        nmsVillager.e(nbtTagCompound);
        nbtTagCompound.setBoolean("Invulnerable", value);
        nmsVillager.f(nbtTagCompound);
    }
    
    public static Boolean getInvulnerable(Villager villager) {
        
        EntityVillager nmsVillager = ((CraftVillager)villager).getHandle();            

        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        
        nmsVillager.e(nbtTagCompound);
        
        return nbtTagCompound.getBoolean("Invulnerable");
    }
    
    public static String[] getInventory(Villager villager) throws Exception {
        
        EntityVillager nmsVillager = ((CraftVillager)villager).getHandle(); 
        
        MerchantRecipeList recipeList = getRecipes(villager);

        String[] result = new String[recipeList.size()];

        if (recipeList.size() == 0) {
            result = new String[1];
            result[0] = ChatColor.GOLD + "Villager has no inventory.";
        }
        else {
            for (int i = 0; i < recipeList.size(); i++) {
                result[i] = String.format("(%s) ", i);
                org.bukkit.inventory.ItemStack buyItemStack1 = CraftItemStack.asBukkitCopy(((MerchantRecipe)recipeList.get(i)).getBuyItem1());
                org.bukkit.inventory.ItemStack buyItemStack2 = CraftItemStack.asBukkitCopy(((MerchantRecipe)recipeList.get(i)).getBuyItem2());
                org.bukkit.inventory.ItemStack sellItemStack = CraftItemStack.asBukkitCopy(((MerchantRecipe)recipeList.get(i)).getBuyItem3());
                
                result[i] += String.format("%sx%s ", buyItemStack1.getAmount(), buyItemStack1.getType().toString());
                if (buyItemStack2.getType() != Material.AIR)
                    result[i] += String.format("+ %sx%s ", buyItemStack2.getAmount(), buyItemStack2.getType().toString());
                result[i] += String.format("= %sx%s ", sellItemStack.getAmount(), sellItemStack.getType().toString());
            }
        }
        return result;
    }
    
    public static MerchantRecipeList getRecipes(Villager villager) throws Exception {
        
        EntityVillager nmsVillager = ((CraftVillager)villager).getHandle(); 
        
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nmsVillager.b(nbtTagCompound);
        return new MerchantRecipeList(nbtTagCompound.getCompound("Offers"));
    }
    
    public static void setRecipes(Villager villager, MerchantRecipeList recipes) {
        
        EntityVillager nmsVillager = ((CraftVillager)villager).getHandle(); 
        
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nmsVillager.b(nbtTagCompound);
        nbtTagCompound.set("Offers", recipes.a());
        nmsVillager.a(nbtTagCompound);
    }
    
    public static void addRecipe(Villager villager) throws Exception {
        
        MerchantRecipe recipe;
        if (buy1 == null) throw new Exception("Missing buy items.");
        if (sell == null) throw new Exception("Missing sell item.");
        MerchantRecipeList recipes = getRecipes(villager);
        if (recipes.size() >= 10) throw new Exception("Villager has the maximum of 10 recipes.");
        
        if (buy2 == null)
            recipe = new MerchantRecipe(buy1, sell);
        else
            recipe = new MerchantRecipe(buy1, buy2, sell);
        
        Class<?> merchantRecipe = recipe.getClass();
        Field maxUsesField = merchantRecipe.getDeclaredField("maxUses");
        maxUsesField.setAccessible(true);
        maxUsesField.set(recipe, maxUses);
    
        recipes.a(recipe);
        
        setRecipes(villager,recipes);
    }
    
    public static void deleteRecipe(Villager villager, int index) throws Exception {
        
        MerchantRecipeList recipes = getRecipes(villager);
        
        if (index >= recipes.size()) throw new Exception("Recipe index does not exist.");
        
        recipes.remove(index);
        
        setRecipes(villager, recipes);
    }

    public static void confine(Villager villager, Location location) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {        
        
        // Access goalSelector and add PathFinderGoals
        EntityVillager nmsVillager = ((CraftVillager)villager).getHandle(); 
        
        //@SuppressWarnings("rawtypes")
        Class<?> entityInsentientClass = nmsVillager.getClass().getSuperclass().getSuperclass().getSuperclass();

        // Remove protected and final modifiers for goalSelector field
        Field goalSelectorField = entityInsentientClass.getDeclaredField("goalSelector");
        goalSelectorField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(goalSelectorField, goalSelectorField.getModifiers() & ~Modifier.FINAL);

        // Load goalSelector and villager's PathfinderGoals
        PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(nmsVillager.world.methodProfiler);

        goalSelector.a(0, new PathfinderGoalLookAtPlayer(nmsVillager, EntityHuman.class, 8.0F));
        goalSelector.a(2, new PathfinderGoalTradeWithPlayer(nmsVillager));
        goalSelector.a(3, new PathfinderGoalLookAtTradingPlayer(nmsVillager));
        goalSelector.a(4, new PathfinderGoalWalkToLocation(nmsVillager, location, 1.0D));

        goalSelectorField.set(nmsVillager, goalSelector);
    }
}
