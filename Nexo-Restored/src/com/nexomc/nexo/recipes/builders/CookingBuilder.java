package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
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
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\f\u001a\u00020\r2\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u001a\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0013H\u0016J\u000e\u0010\u0015\u001a\u00020\u00112\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\u0016\u001a\u00020\u00112\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/CookingBuilder;", "Lcom/nexomc/nexo/recipes/builders/RecipeBuilder;", "player", "Lorg/bukkit/entity/Player;", "recipeType", "Lcom/nexomc/nexo/recipes/RecipeType;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/recipes/RecipeType;)V", "cookingTime", "", "experience", "", "createInventory", "Lorg/bukkit/inventory/Inventory;", "inventoryTitle", "Lnet/kyori/adventure/text/Component;", "saveRecipe", "", "name", "", "permission", "setCookingTime", "setExperience", "com.nexomc:core"}
)
public class CookingBuilder extends RecipeBuilder {
   private int cookingTime;
   private float experience;

   public CookingBuilder(@NotNull Player player, @NotNull RecipeType recipeType) {
      super(player, recipeType);
   }

   @NotNull
   public Inventory createInventory(@Nullable Player player, @NotNull Component inventoryTitle) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)player, InventoryType.FURNACE, inventoryTitle);
      return inventory;
   }

   public void saveRecipe(@NotNull String name, @Nullable String permission) {
      ItemStack[] contents = this.getInventory().getContents();
      ItemStack[] content = contents;
      ConfigurationSection configurationSection = this.getConfig().createSection(name);
      ConfigurationSection newCraftSection = configurationSection;
      ConfigurationSection configurationSection2 = newCraftSection.createSection("result");
      ItemStack itemStack = content[2];
      Intrinsics.checkNotNull(content[2]);
      this.setItemStack(configurationSection2, itemStack);
      configurationSection2 = newCraftSection.createSection("input");
      itemStack = content[0];
      Intrinsics.checkNotNull(content[0]);
      this.setItemStack(configurationSection2, itemStack);
      newCraftSection.set("cookingTime", this.cookingTime);
      newCraftSection.set("experience", this.experience);
      CharSequence charSequence = (CharSequence)permission;
      if (charSequence != null && charSequence.length() != 0) {
         newCraftSection.set("permission", permission);
      }

      this.saveConfig();
      this.close();
   }

   public final void setCookingTime(int cookingTime) {
      this.cookingTime = cookingTime;
   }

   public final void setExperience(float experience) {
      this.experience = experience;
   }
}
