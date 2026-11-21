package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0006\u001a\u00020\u00072\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\b\u001a\u00020\tH\u0016J\u001a\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0016¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/BrewingBuilder;", "Lcom/nexomc/nexo/recipes/builders/RecipeBuilder;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/entity/Player;)V", "createInventory", "Lorg/bukkit/inventory/Inventory;", "inventoryTitle", "Lnet/kyori/adventure/text/Component;", "saveRecipe", "", "name", "", "permission", "com.nexomc:core"}
)
public final class BrewingBuilder extends RecipeBuilder {
   public BrewingBuilder(@NotNull Player player) {
      super(player, RecipeType.BREWING, AdventureUtilsKt.deserialize("<glyph:brewing_stand_recipe_builder>"));
   }

   @NotNull
   public Inventory createInventory(@Nullable Player player, @NotNull Component inventoryTitle) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)player, InventoryType.BREWING, inventoryTitle);
      return inventory;
   }

   public void saveRecipe(@NotNull String name, @Nullable String permission) {
      ItemStack[] contents = this.getInventory().getContents();
      ItemStack orNull = (ItemStack)ArraysKt.getOrNull(contents, 3);
      if (orNull != null) {
         ItemStack ingredient = orNull;
         orNull = (ItemStack)ArraysKt.getOrNull(contents, 0);
         if (orNull != null) {
            ItemStack input = orNull;
            orNull = (ItemStack)ArraysKt.getOrNull(contents, 2);
            if (orNull != null) {
               ItemStack result = orNull;
               ConfigurationSection configurationSection = this.getConfig().createSection(name);
               ConfigurationSection newCraftSection = configurationSection;
               ConfigurationSection configurationSection2 = newCraftSection.createSection("result");
               this.setItemStack(configurationSection2, result);
               configurationSection2 = newCraftSection.createSection("ingredient");
               this.setItemStack(configurationSection2, ingredient);
               configurationSection2 = newCraftSection.createSection("input");
               this.setItemStack(configurationSection2, input);
               CharSequence charSequence = (CharSequence)permission;
               if (charSequence != null && charSequence.length() != 0) {
                  newCraftSection.set("permission", permission);
               }

               this.saveConfig();
               this.close();
            }
         }
      }
   }
}
