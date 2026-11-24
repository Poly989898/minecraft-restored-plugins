package net.Indyuce.mmoitems.api.crafting.ingredient.inventory;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ingredient.CheckedIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.IngredientType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IngredientInventory {
   private final Map<String, List<PlayerIngredient>> ingredients = new HashMap<>();

   public IngredientInventory(Player var1) {
      this(var1.getInventory());
   }

   public IngredientInventory(Inventory var1) {
      for (ItemStack var5 : var1.getContents()) {
         if (var5 != null && var5.getType() != Material.AIR && var5.getAmount() > 0) {
            NBTItem var6 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var5);

            for (IngredientType var8 : MMOItems.plugin.getCrafting().getIngredients()) {
               if (var8.check(var6)) {
                  this.addIngredient(var6, var8);
                  break;
               }
            }
         }
      }
   }

   public void addIngredient(NBTItem var1, IngredientType var2) {
      String var3 = var2.getId();
      List var4 = this.ingredients.computeIfAbsent(var3, var0 -> new ArrayList<>());
      var4.add(var2.readPlayerIngredient(var1));
   }

   @Nullable
   public CheckedIngredient findMatching(@NotNull Ingredient var1) {
      ArrayList var2 = new ArrayList();
      if (!this.ingredients.containsKey(var1.getId())) {
         return new CheckedIngredient(var1, var2);
      }

      for (PlayerIngredient var4 : this.ingredients.get(var1.getId())) {
         if (var1.matches(var4)) {
            var2.add(var4);
         }
      }

      return new CheckedIngredient(var1, var2);
   }

   @Deprecated
   public boolean hasIngredient(Ingredient var1) {
      return this.findMatching(var1).isHad();
   }

   @Deprecated
   public enum IngredientLookupMode {
      IGNORE_ITEM_LEVEL,
      BASIC;
   }
}
