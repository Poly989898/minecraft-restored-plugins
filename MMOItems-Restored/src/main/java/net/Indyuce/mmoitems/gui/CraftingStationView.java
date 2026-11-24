package net.Indyuce.mmoitems.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;
import java.util.List;
import java.util.UUID;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import net.Indyuce.mmoitems.api.crafting.CraftingStatus;
import net.Indyuce.mmoitems.api.crafting.Layout;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.crafting.recipe.CheckedRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.item.util.ConfigItems;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.comp.eco.MoneyCondition;
import net.Indyuce.mmoitems.listener.CustomSoundListener;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingStationView extends PluginInventory {
   private final CraftingStation station;
   private final Layout layout;
   private List<CheckedRecipe> recipes;
   private IngredientInventory ingredients;
   private int queueOffset;

   public CraftingStationView(Player player, CraftingStation craftingStation, int count) {
      super(player);
      this.station = craftingStation;
      this.layout = craftingStation.getLayout();
      this.page = count;
      this.updateData();
   }

   public CraftingStation getStation() {
      return this.station;
   }

   void updateData() {
      this.ingredients = new IngredientInventory(this.player);
      this.recipes = this.station.getAvailableRecipes(this.playerData, this.ingredients);
   }

   @Override
   public Inventory getInventory() {
      String colors = MythicLib.plugin
         .parseColors(
            MythicLib.plugin
               .getPlaceholderParser()
               .parse(this.getPlayer(), this.station.getName().replace("#page#", "" + this.page).replace("#max#", "" + this.station.getMaxPage()))
         );
      final Inventory inventory = Bukkit.createInventory(this, this.layout.getSize(), colors);
      int count = (this.page - 1) * this.layout.getRecipeSlots().size();
      int firstRecipeIndex = this.page * this.layout.getRecipeSlots().size();

      for (int i = count; i < firstRecipeIndex; i++) {
         if (i >= this.recipes.size()) {
            if (this.station.getItemOptions().hasNoRecipe()) {
               inventory.setItem(this.layout.getRecipeSlots().get(i - count), this.station.getItemOptions().getNoRecipe());
            }
         } else {
            inventory.setItem(this.layout.getRecipeSlots().get(i - count), this.recipes.get(i).display());
         }
      }

      if (firstRecipeIndex < this.recipes.size()) {
         inventory.setItem(this.layout.getRecipeNextSlot(), ConfigItems.NEXT_PAGE.getItem());
      }

      if (this.page > 1) {
         inventory.setItem(this.layout.getRecipePreviousSlot(), ConfigItems.PREVIOUS_PAGE.getItem());
      }

      final CraftingStatus.CraftingQueue queue = this.playerData.getCrafting().getQueue(this.station);

      for (int i = this.queueOffset; i < this.queueOffset + this.layout.getQueueSlots().size(); i++) {
         if (i >= queue.getCrafts().size()) {
            if (this.station.getItemOptions().hasNoQueueItem()) {
               inventory.setItem(this.layout.getQueueSlots().get(i - this.queueOffset), this.station.getItemOptions().getNoQueueItem());
            }
         } else {
            inventory.setItem(
               this.layout.getQueueSlots().get(i - this.queueOffset),
               ConfigItems.QUEUE_ITEM_DISPLAY.newBuilder(queue.getCrafts().get(i), i + 1).build()
            );
         }
      }

      if (this.queueOffset + this.layout.getQueueSlots().size() < queue.getCrafts().size()) {
         inventory.setItem(this.layout.getQueueNextSlot(), ConfigItems.NEXT_IN_QUEUE.getItem());
      }

      if (this.queueOffset > 0) {
         inventory.setItem(this.layout.getQueuePreviousSlot(), ConfigItems.PREVIOUS_IN_QUEUE.getItem());
      }

      (new BukkitRunnable() {
            public void run() {
               if (inventory.getViewers().isEmpty()) {
                  this.cancel();
               } else {
                  for (int var1x = CraftingStationView.this.queueOffset;
                     var1x < CraftingStationView.this.queueOffset + CraftingStationView.this.layout.getQueueSlots().size();
                     var1x++
                  ) {
                     if (var1x >= queue.getCrafts().size()) {
                        inventory.setItem(
                           CraftingStationView.this.layout.getQueueSlots().get(var1x - CraftingStationView.this.queueOffset),
                           CraftingStationView.this.station.getItemOptions().hasNoQueueItem()
                              ? CraftingStationView.this.station.getItemOptions().getNoQueueItem()
                              : null
                        );
                     } else {
                        inventory.setItem(
                           CraftingStationView.this.layout.getQueueSlots().get(var1x - CraftingStationView.this.queueOffset),
                           ConfigItems.QUEUE_ITEM_DISPLAY.newBuilder(queue.getCrafts().get(var1x), var1x + 1).build()
                        );
                     }
                  }
               }
            }
         })
         .runTaskTimer(MMOItems.plugin, 0L, 20L);
      if (this.station.getItemOptions().hasFill()) {
         for (int i = 0; i < this.layout.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
               inventory.setItem(i, this.station.getItemOptions().getFill());
            }
         }
      }

      return inventory;
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      if (this.playerData.isOnline()) {
         event.setCancelled(true);
         if (MMOUtils.isMetaItem(event.getCurrentItem(), false)) {
            NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCurrentItem());
            if (nBTItem.getString("ItemId").equals("PREVIOUS_IN_QUEUE")) {
               this.queueOffset--;
               this.open();
            } else if (nBTItem.getString("ItemId").equals("NEXT_IN_QUEUE")) {
               this.queueOffset++;
               this.open();
            } else if (nBTItem.getString("ItemId").equals("NEXT_PAGE")) {
               this.page++;
               this.open();
            } else if (nBTItem.getString("ItemId").equals("PREVIOUS_PAGE")) {
               this.page--;
               this.open();
            } else {
               NBTItem clickedNbtItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCurrentItem());
               String recipeId = clickedNbtItem.getString("recipeId");
               if (!recipeId.isEmpty()) {
                  this.updateData();
                  CheckedRecipe recipe = this.getRecipe(recipeId);
                  if (event.isRightClick()) {
                     new CraftingStationPreview(this, recipe).open();
                  } else {
                     this.processRecipe(recipe);
                     this.open();
                  }
               } else if (!(recipeId = clickedNbtItem.getString("queueId")).isEmpty()) {
                  CraftingStatus.CraftingQueue.QueueItem craft = this.playerData.getCrafting().getQueue(this.station).getCraft(UUID.fromString(recipeId));
                  CraftingRecipe recipe = craft.getRecipe();
                  if (craft.isReady()) {
                     ItemStack item = recipe.hasOption(Recipe.RecipeOption.OUTPUT_ITEM) ? recipe.getOutputItemStack(this.playerData.getRPG()) : null;
                     PlayerUseCraftingStationEvent itemEvent = new PlayerUseCraftingStationEvent(this.playerData, this.station, recipe, item);
                     Bukkit.getPluginManager().callEvent(itemEvent);
                     if (itemEvent.isCancelled()) {
                        return;
                     }

                     this.playerData.getCrafting().getQueue(this.station).remove(craft);
                     recipe.whenClaimed().forEach(var1x -> var1x.whenCrafting(this.playerData));
                     CustomSoundListener.playSound(item, CustomSound.ON_CRAFT, this.player);
                     if (!recipe.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
                        this.player.playSound(this.player.getLocation(), this.station.getSound(), 1.0F, 1.0F);
                     }

                     if (item != null) {
                        new SmartGive(this.player).give(new ItemStack[]{item});
                     }
                  } else {
                     PlayerUseCraftingStationEvent recipeEvent = new PlayerUseCraftingStationEvent(this.playerData, this.station, recipe);
                     Bukkit.getPluginManager().callEvent(recipeEvent);
                     if (recipeEvent.isCancelled()) {
                        return;
                     }

                     this.playerData.getCrafting().getQueue(this.station).remove(craft);
                     recipe.whenCanceled().forEach(var1x -> var1x.whenCrafting(this.playerData));
                     if (!recipe.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
                        this.player.playSound(this.player.getLocation(), this.station.getSound(), 1.0F, 1.0F);
                     }

                     for (Ingredient ingredient : craft.getRecipe().getIngredients()) {
                        new SmartGive(this.player).give(new ItemStack[]{ingredient.generateItemStack(this.playerData.getRPG(), false)});
                     }

                     recipe.getConditions()
                        .stream()
                        .filter(condition -> condition instanceof MoneyCondition)
                        .map(condition -> (MoneyCondition)condition)
                        .forEach(var1x -> MMOItems.plugin.getVault().getEconomy().depositPlayer(this.player, var1x.getAmount()));
                  }

                  this.updateData();
                  this.open();
               }
            }
         }
      }
   }

   public void processRecipe(CheckedRecipe checkedRecipe) {
      if (!checkedRecipe.areConditionsMet()) {
         Message.CONDITIONS_NOT_MET.format(ChatColor.RED).send(this.player);
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      } else if (!checkedRecipe.allIngredientsHad()) {
         Message.NOT_ENOUGH_MATERIALS.format(ChatColor.RED).send(this.player);
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      } else if (!checkedRecipe.getRecipe().canUse(this.playerData, this.ingredients, checkedRecipe, this.station)) {
         this.updateData();
      } else {
         if (checkedRecipe.getRecipe().whenUsed(this.playerData, this.ingredients, checkedRecipe, this.station)) {
            checkedRecipe.getIngredients().forEach(checkedIngredient -> checkedIngredient.takeAway());
            checkedRecipe.getConditions().forEach(var1x -> var1x.getCondition().whenCrafting(this.playerData));
            checkedRecipe.getRecipe().whenUsed().forEach(var1x -> var1x.whenCrafting(this.playerData));
            this.updateData();
         }
      }
   }

   @Nullable
   private CheckedRecipe getRecipe(@NotNull String text) {
      for (CheckedRecipe recipe : this.recipes) {
         if (recipe.getRecipe().getId().equals(text)) {
            return recipe;
         }
      }

      return null;
   }
}
