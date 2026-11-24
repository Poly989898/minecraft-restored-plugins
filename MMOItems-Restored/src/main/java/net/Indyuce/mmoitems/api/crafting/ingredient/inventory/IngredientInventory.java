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

   public IngredientInventory(Player player) {
      this(player.getInventory());
   }

   public IngredientInventory(Inventory inventory) {
      for (ItemStack content : inventory.getContents()) {
         if (content != null && content.getType() != Material.AIR && content.getAmount() > 0) {
            NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(content);

            for (IngredientType ingredient : MMOItems.plugin.getCrafting().getIngredients()) {
               if (ingredient.check(nBTItem)) {
                  this.addIngredient(nBTItem, ingredient);
                  break;
               }
            }
         }
      }
   }

   public void addIngredient(NBTItem nbtItem, IngredientType ingredientType) {
      String id = ingredientType.getId();
      List ifAbsent = this.ingredients.computeIfAbsent(id, text -> new ArrayList<>());
      ifAbsent.add(ingredientType.readPlayerIngredient(nbtItem));
   }

   @Nullable
   public CheckedIngredient findMatching(@NotNull Ingredient ingredient) {
      ArrayList list = new ArrayList();
      if (!this.ingredients.containsKey(ingredient.getId())) {
         return new CheckedIngredient(ingredient, list);
      }

      for (PlayerIngredient playerIngredient : this.ingredients.get(ingredient.getId())) {
         if (ingredient.matches(playerIngredient)) {
            list.add(playerIngredient);
         }
      }

      return new CheckedIngredient(ingredient, list);
   }

   @Deprecated
   public boolean hasIngredient(Ingredient ingredient) {
      return this.findMatching(ingredient).isHad();
   }

   @Deprecated
   public enum IngredientLookupMode {
      IGNORE_ITEM_LEVEL,
      BASIC;
   }
}
