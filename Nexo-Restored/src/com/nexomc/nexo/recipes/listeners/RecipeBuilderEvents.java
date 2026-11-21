package com.nexomc.nexo.recipes.listeners;

import com.nexomc.nexo.recipes.builders.RecipeBuilder;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0007\u001a\u00020\b*\u00020\tH\u0007b\u000e\b\n\u0012\n\b\u000b\u0012\u0006\b\n0\f8\rJ\u001c\u0010\u000e\u001a\u00020\b*\u00020\u000fH\u0007b\u000e\b\n\u0012\n\b\u000b\u0012\u0006\b\n0\f8\rR\u0013\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/recipes/listeners/RecipeBuilderEvents;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "empty", "Lorg/bukkit/inventory/ItemStack;", "Lorg/jetbrains/annotations/NotNull;", "setCursor", "", "Lorg/bukkit/event/inventory/InventoryClickEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGH", "onInventoryClosed", "Lorg/bukkit/event/inventory/InventoryCloseEvent;", "com.nexomc:core"}
)
public final class RecipeBuilderEvents implements Listener {
   @NotNull
   private final ItemStack empty;

   public RecipeBuilderEvents() {
      ItemStack itemStack = ItemStack.of(Material.AIR);
      this.empty = itemStack;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void setCursor(@NotNull InventoryClickEvent inventoryClickEvent) {
      RecipeBuilder.Companion companion = RecipeBuilder.Companion;
      UUID uniqueId = inventoryClickEvent.getWhoClicked().getUniqueId();
      RecipeBuilder recipeBuilder = companion.currentBuilder(uniqueId);
      if (recipeBuilder != null) {
         RecipeBuilder builder = recipeBuilder;
         if (Intrinsics.areEqual(inventoryClickEvent.getView().title(), builder.getInventoryTitle())) {
            int slot = inventoryClickEvent.getSlot();
            InventoryType.SlotType slotType = inventoryClickEvent.getSlotType();
            if (builder.validSlot(slot, slotType)) {
               inventoryClickEvent.setCancelled(true);
               ItemStack currentItem = inventoryClickEvent.getCurrentItem();
               if (currentItem == null) {
                  currentItem = this.empty;
               }

               Pair pair = TuplesKt.to(currentItem.clone(), inventoryClickEvent.getCursor().clone());
               Object component1 = pair.component1();
               ItemStack currentResult = (ItemStack)component1;
               component1 = pair.component2();
               ItemStack currentCursor = (ItemStack)component1;
               if (inventoryClickEvent.isLeftClick()) {
                  inventoryClickEvent.setCurrentItem(currentCursor);
                  inventoryClickEvent.setCursor(currentResult);
               } else if (inventoryClickEvent.isRightClick()) {
                  ItemStack itemStack = currentCursor.clone();
                  int value = 0;
                  itemStack.setAmount(1);
                  inventoryClickEvent.setCurrentItem(itemStack);
                  inventoryClickEvent.setCursor(currentCursor.subtract(1));
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void onInventoryClosed(@NotNull InventoryCloseEvent inventoryCloseEvent) {
      RecipeBuilder.Companion companion = RecipeBuilder.Companion;
      UUID uniqueId = inventoryCloseEvent.getPlayer().getUniqueId();
      RecipeBuilder recipeBuilder2 = companion.currentBuilder(uniqueId);
      if (recipeBuilder2 != null) {
         RecipeBuilder recipeBuilder = recipeBuilder2;
         if (Intrinsics.areEqual(inventoryCloseEvent.getView().title(), recipeBuilder.getInventoryTitle())) {
            Inventory inventory = inventoryCloseEvent.getInventory();
            recipeBuilder.setInventory(inventory);
         }
      }
   }
}
