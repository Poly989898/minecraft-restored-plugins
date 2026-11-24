package net.Indyuce.mmoitems.api.event;

import javax.annotation.Nullable;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import net.Indyuce.mmoitems.api.crafting.recipe.CheckedRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.crafting.recipe.UpgradingRecipe;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerUseCraftingStationEvent extends PlayerDataEvent {
   private final CraftingStation station;
   private final Recipe recipe;
   private final PlayerUseCraftingStationEvent.StationAction action;
   @Nullable
   private final CheckedRecipe recipeInfo;
   @Nullable
   private final ItemStack result;
   private static final HandlerList handlers = new HandlerList();

   public PlayerUseCraftingStationEvent(PlayerData var1, CraftingStation var2, CheckedRecipe var3) {
      this(var1, var2, var3, var3.getRecipe(), null, PlayerUseCraftingStationEvent.StationAction.INTERACT_WITH_RECIPE);
   }

   public PlayerUseCraftingStationEvent(PlayerData var1, CraftingStation var2, UpgradingRecipe.CheckedUpgradingRecipe var3) {
      this(var1, var2, var3, var3.getRecipe(), null, PlayerUseCraftingStationEvent.StationAction.UPGRADE_RECIPE);
   }

   public PlayerUseCraftingStationEvent(PlayerData var1, CraftingStation var2, CheckedRecipe var3, ItemStack var4) {
      this(var1, var2, var3, var3.getRecipe(), var4, PlayerUseCraftingStationEvent.StationAction.INSTANT_RECIPE);
   }

   public PlayerUseCraftingStationEvent(PlayerData var1, CraftingStation var2, Recipe var3, ItemStack var4) {
      this(var1, var2, null, var3, var4, PlayerUseCraftingStationEvent.StationAction.CRAFTING_QUEUE);
   }

   public PlayerUseCraftingStationEvent(PlayerData var1, CraftingStation var2, Recipe var3) {
      this(var1, var2, null, var3, null, PlayerUseCraftingStationEvent.StationAction.CANCEL_QUEUE);
   }

   private PlayerUseCraftingStationEvent(
      PlayerData var1, CraftingStation var2, CheckedRecipe var3, Recipe var4, ItemStack var5, PlayerUseCraftingStationEvent.StationAction var6
   ) {
      super(var1);
      this.station = var2;
      this.recipe = var4;
      this.recipeInfo = var3;
      this.result = var5;
      this.action = var6;
   }

   public CraftingStation getStation() {
      return this.station;
   }

   public boolean hasRecipeInfo() {
      return this.recipeInfo != null;
   }

   public CheckedRecipe getRecipeInfo() {
      Validate.notNull(
         this.action == PlayerUseCraftingStationEvent.StationAction.INSTANT_RECIPE
            || this.action == PlayerUseCraftingStationEvent.StationAction.INTERACT_WITH_RECIPE,
         "No recipe info is provided with " + this.action.name()
      );
      return this.recipeInfo;
   }

   public boolean hasResult() {
      return this.result != null;
   }

   public ItemStack getResult() {
      Validate.notNull(
         this.action == PlayerUseCraftingStationEvent.StationAction.INSTANT_RECIPE || this.action == PlayerUseCraftingStationEvent.StationAction.CRAFTING_QUEUE,
         "No result item is provided with " + this.action.name()
      );
      return this.result;
   }

   public Recipe getRecipe() {
      return this.recipe;
   }

   public PlayerUseCraftingStationEvent.StationAction getInteraction() {
      return this.action;
   }

   @Deprecated
   public boolean isInstant() {
      return this.recipe instanceof CraftingRecipe && ((CraftingRecipe)this.recipe).isInstant();
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public enum StationAction {
      UPGRADE_RECIPE,
      INTERACT_WITH_RECIPE,
      INSTANT_RECIPE,
      CRAFTING_QUEUE,
      CANCEL_QUEUE;
   }
}
