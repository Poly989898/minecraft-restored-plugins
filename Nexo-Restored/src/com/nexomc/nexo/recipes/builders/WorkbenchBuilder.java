package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\b\u001a\u00020\t2\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0016¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/WorkbenchBuilder;", "Lcom/nexomc/nexo/recipes/builders/RecipeBuilder;", "player", "Lorg/bukkit/entity/Player;", "recipeType", "Lcom/nexomc/nexo/recipes/RecipeType;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/recipes/RecipeType;)V", "createInventory", "Lorg/bukkit/inventory/Inventory;", "inventoryTitle", "Lnet/kyori/adventure/text/Component;", "com.nexomc:core"}
)
public abstract class WorkbenchBuilder extends RecipeBuilder {
   public WorkbenchBuilder(@NotNull Player player, @NotNull RecipeType recipeType) {
      super(player, recipeType);
   }

   @NotNull
   public Inventory createInventory(@Nullable Player player, @NotNull Component inventoryTitle) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)player, InventoryType.WORKBENCH, inventoryTitle);
      return inventory;
   }
}
