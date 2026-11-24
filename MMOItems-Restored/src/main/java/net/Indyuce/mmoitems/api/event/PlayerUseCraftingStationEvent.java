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

   public PlayerUseCraftingStationEvent(PlayerData playerData, CraftingStation craftingStation, CheckedRecipe checkedRecipe) {
      this(playerData, craftingStation, checkedRecipe, checkedRecipe.getRecipe(), null, PlayerUseCraftingStationEvent.StationAction.INTERACT_WITH_RECIPE);
   }

   public PlayerUseCraftingStationEvent(PlayerData playerData, CraftingStation craftingStation, UpgradingRecipe.CheckedUpgradingRecipe checkedUpgradingRecipe) {
      this(playerData, craftingStation, checkedUpgradingRecipe, checkedUpgradingRecipe.getRecipe(), null, PlayerUseCraftingStationEvent.StationAction.UPGRADE_RECIPE);
   }

   public PlayerUseCraftingStationEvent(PlayerData playerData, CraftingStation craftingStation, CheckedRecipe checkedRecipe, ItemStack item) {
      this(playerData, craftingStation, checkedRecipe, checkedRecipe.getRecipe(), item, PlayerUseCraftingStationEvent.StationAction.INSTANT_RECIPE);
   }

   public PlayerUseCraftingStationEvent(PlayerData playerData, CraftingStation craftingStation, Recipe recipe, ItemStack item) {
      this(playerData, craftingStation, null, recipe, item, PlayerUseCraftingStationEvent.StationAction.CRAFTING_QUEUE);
   }

   public PlayerUseCraftingStationEvent(PlayerData playerData, CraftingStation craftingStation, Recipe recipe) {
      this(playerData, craftingStation, null, recipe, null, PlayerUseCraftingStationEvent.StationAction.CANCEL_QUEUE);
   }

   private PlayerUseCraftingStationEvent(
      PlayerData playerData, CraftingStation craftingStation, CheckedRecipe checkedRecipe, Recipe recipe, ItemStack item, PlayerUseCraftingStationEvent.StationAction stationAction
   ) {
      super(playerData);
      this.station = craftingStation;
      this.recipe = recipe;
      this.recipeInfo = checkedRecipe;
      this.result = item;
      this.action = stationAction;
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
