package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import kotlin.Metadata;
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
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0006\u001a\u00020\u00072\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\b\u001a\u00020\tH\u0016¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/CampfireBuilder;", "Lcom/nexomc/nexo/recipes/builders/CookingBuilder;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/entity/Player;)V", "createInventory", "Lorg/bukkit/inventory/Inventory;", "inventoryTitle", "Lnet/kyori/adventure/text/Component;", "com.nexomc:core"}
)
public final class CampfireBuilder extends CookingBuilder {
   public CampfireBuilder(@NotNull Player player) {
      super(player, RecipeType.CAMPFIRE);
   }

   @NotNull
   public Inventory createInventory(@Nullable Player player, @NotNull Component inventoryTitle) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)player, InventoryType.SMOKER, inventoryTitle);
      return inventory;
   }
}
