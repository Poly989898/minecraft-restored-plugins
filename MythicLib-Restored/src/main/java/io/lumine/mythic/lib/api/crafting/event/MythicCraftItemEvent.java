package io.lumine.mythic.lib.api.crafting.event;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCachedResult;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class MythicCraftItemEvent extends Event implements Cancellable {
   @NotNull
   final MythicRecipeInventory resultInventory;
   @NotNull
   final MythicBlueprintInventory otherInventories;
   @NotNull
   final MythicCachedResult cache;
   @NotNull
   VanillaInventoryMapping mapping;
   @NotNull
   final InventoryClickEvent trigger;
   @NotNull
   static final HandlerList handlers = new HandlerList();
   boolean cancelled = false;

   @NotNull
   public MythicRecipeInventory getResultInventory() {
      return this.resultInventory;
   }

   @NotNull
   public MythicBlueprintInventory getOtherInventories() {
      return this.otherInventories;
   }

   @NotNull
   public MythicCachedResult getCache() {
      return this.cache;
   }

   @NotNull
   public VanillaInventoryMapping getMapping() {
      return this.mapping;
   }

   public void setMapping(@NotNull VanillaInventoryMapping map) {
      this.mapping = map;
   }

   @NotNull
   public InventoryClickEvent getTrigger() {
      return this.trigger;
   }

   public MythicCraftItemEvent(
      @NotNull InventoryClickEvent reason,
      @NotNull MythicRecipeInventory resultInventory,
      @NotNull VanillaInventoryMapping mapping,
      @NotNull MythicBlueprintInventory otherInventories,
      @NotNull MythicCachedResult cache
   ) {
      this.trigger = reason;
      this.resultInventory = resultInventory;
      this.mapping = mapping;
      this.otherInventories = otherInventories;
      this.cache = cache;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }
}
