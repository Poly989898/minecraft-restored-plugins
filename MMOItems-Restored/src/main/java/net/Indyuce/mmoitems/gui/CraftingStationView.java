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

   public CraftingStationView(Player var1, CraftingStation var2, int var3) {
      super(var1);
      this.station = var2;
      this.layout = var2.getLayout();
      this.page = var3;
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
      String var1 = MythicLib.plugin
         .parseColors(
            MythicLib.plugin
               .getPlaceholderParser()
               .parse(this.getPlayer(), this.station.getName().replace("#page#", "" + this.page).replace("#max#", "" + this.station.getMaxPage()))
         );
      final Inventory var2 = Bukkit.createInventory(this, this.layout.getSize(), var1);
      int var3 = (this.page - 1) * this.layout.getRecipeSlots().size();
      int var4 = this.page * this.layout.getRecipeSlots().size();

      for (int var5 = var3; var5 < var4; var5++) {
         if (var5 >= this.recipes.size()) {
            if (this.station.getItemOptions().hasNoRecipe()) {
               var2.setItem(this.layout.getRecipeSlots().get(var5 - var3), this.station.getItemOptions().getNoRecipe());
            }
         } else {
            var2.setItem(this.layout.getRecipeSlots().get(var5 - var3), this.recipes.get(var5).display());
         }
      }

      if (var4 < this.recipes.size()) {
         var2.setItem(this.layout.getRecipeNextSlot(), ConfigItems.NEXT_PAGE.getItem());
      }

      if (this.page > 1) {
         var2.setItem(this.layout.getRecipePreviousSlot(), ConfigItems.PREVIOUS_PAGE.getItem());
      }

      final CraftingStatus.CraftingQueue var7 = this.playerData.getCrafting().getQueue(this.station);

      for (int var6 = this.queueOffset; var6 < this.queueOffset + this.layout.getQueueSlots().size(); var6++) {
         if (var6 >= var7.getCrafts().size()) {
            if (this.station.getItemOptions().hasNoQueueItem()) {
               var2.setItem(this.layout.getQueueSlots().get(var6 - this.queueOffset), this.station.getItemOptions().getNoQueueItem());
            }
         } else {
            var2.setItem(
               this.layout.getQueueSlots().get(var6 - this.queueOffset),
               ConfigItems.QUEUE_ITEM_DISPLAY.newBuilder(var7.getCrafts().get(var6), var6 + 1).build()
            );
         }
      }

      if (this.queueOffset + this.layout.getQueueSlots().size() < var7.getCrafts().size()) {
         var2.setItem(this.layout.getQueueNextSlot(), ConfigItems.NEXT_IN_QUEUE.getItem());
      }

      if (this.queueOffset > 0) {
         var2.setItem(this.layout.getQueuePreviousSlot(), ConfigItems.PREVIOUS_IN_QUEUE.getItem());
      }

      (new BukkitRunnable() {
            public void run() {
               if (var2.getViewers().isEmpty()) {
                  this.cancel();
               } else {
                  for (int var1x = CraftingStationView.this.queueOffset;
                     var1x < CraftingStationView.this.queueOffset + CraftingStationView.this.layout.getQueueSlots().size();
                     var1x++
                  ) {
                     if (var1x >= var7.getCrafts().size()) {
                        var2.setItem(
                           CraftingStationView.this.layout.getQueueSlots().get(var1x - CraftingStationView.this.queueOffset),
                           CraftingStationView.this.station.getItemOptions().hasNoQueueItem()
                              ? CraftingStationView.this.station.getItemOptions().getNoQueueItem()
                              : null
                        );
                     } else {
                        var2.setItem(
                           CraftingStationView.this.layout.getQueueSlots().get(var1x - CraftingStationView.this.queueOffset),
                           ConfigItems.QUEUE_ITEM_DISPLAY.newBuilder(var7.getCrafts().get(var1x), var1x + 1).build()
                        );
                     }
                  }
               }
            }
         })
         .runTaskTimer(MMOItems.plugin, 0L, 20L);
      if (this.station.getItemOptions().hasFill()) {
         for (int var8 = 0; var8 < this.layout.getSize(); var8++) {
            if (var2.getItem(var8) == null || var2.getItem(var8).getType() == Material.AIR) {
               var2.setItem(var8, this.station.getItemOptions().getFill());
            }
         }
      }

      return var2;
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      if (this.playerData.isOnline()) {
         var1.setCancelled(true);
         if (MMOUtils.isMetaItem(var1.getCurrentItem(), false)) {
            NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCurrentItem());
            if (var2.getString("ItemId").equals("PREVIOUS_IN_QUEUE")) {
               this.queueOffset--;
               this.open();
            } else if (var2.getString("ItemId").equals("NEXT_IN_QUEUE")) {
               this.queueOffset++;
               this.open();
            } else if (var2.getString("ItemId").equals("NEXT_PAGE")) {
               this.page++;
               this.open();
            } else if (var2.getString("ItemId").equals("PREVIOUS_PAGE")) {
               this.page--;
               this.open();
            } else {
               NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCurrentItem());
               String var4 = var3.getString("recipeId");
               if (!var4.isEmpty()) {
                  this.updateData();
                  CheckedRecipe var5 = this.getRecipe(var4);
                  if (var1.isRightClick()) {
                     new CraftingStationPreview(this, var5).open();
                  } else {
                     this.processRecipe(var5);
                     this.open();
                  }
               } else if (!(var4 = var3.getString("queueId")).isEmpty()) {
                  CraftingStatus.CraftingQueue.QueueItem var11 = this.playerData.getCrafting().getQueue(this.station).getCraft(UUID.fromString(var4));
                  CraftingRecipe var6 = var11.getRecipe();
                  if (var11.isReady()) {
                     ItemStack var12 = var6.hasOption(Recipe.RecipeOption.OUTPUT_ITEM) ? var6.getOutputItemStack(this.playerData.getRPG()) : null;
                     PlayerUseCraftingStationEvent var13 = new PlayerUseCraftingStationEvent(this.playerData, this.station, var6, var12);
                     Bukkit.getPluginManager().callEvent(var13);
                     if (var13.isCancelled()) {
                        return;
                     }

                     this.playerData.getCrafting().getQueue(this.station).remove(var11);
                     var6.whenClaimed().forEach(var1x -> var1x.whenCrafting(this.playerData));
                     CustomSoundListener.playSound(var12, CustomSound.ON_CRAFT, this.player);
                     if (!var6.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
                        this.player.playSound(this.player.getLocation(), this.station.getSound(), 1.0F, 1.0F);
                     }

                     if (var12 != null) {
                        new SmartGive(this.player).give(new ItemStack[]{var12});
                     }
                  } else {
                     PlayerUseCraftingStationEvent var7 = new PlayerUseCraftingStationEvent(this.playerData, this.station, var6);
                     Bukkit.getPluginManager().callEvent(var7);
                     if (var7.isCancelled()) {
                        return;
                     }

                     this.playerData.getCrafting().getQueue(this.station).remove(var11);
                     var6.whenCanceled().forEach(var1x -> var1x.whenCrafting(this.playerData));
                     if (!var6.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
                        this.player.playSound(this.player.getLocation(), this.station.getSound(), 1.0F, 1.0F);
                     }

                     for (Ingredient var9 : var11.getRecipe().getIngredients()) {
                        new SmartGive(this.player).give(new ItemStack[]{var9.generateItemStack(this.playerData.getRPG(), false)});
                     }

                     var6.getConditions()
                        .stream()
                        .filter(var0 -> var0 instanceof MoneyCondition)
                        .map(var0 -> (MoneyCondition)var0)
                        .forEach(var1x -> MMOItems.plugin.getVault().getEconomy().depositPlayer(this.player, var1x.getAmount()));
                  }

                  this.updateData();
                  this.open();
               }
            }
         }
      }
   }

   public void processRecipe(CheckedRecipe var1) {
      if (!var1.areConditionsMet()) {
         Message.CONDITIONS_NOT_MET.format(ChatColor.RED).send(this.player);
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      } else if (!var1.allIngredientsHad()) {
         Message.NOT_ENOUGH_MATERIALS.format(ChatColor.RED).send(this.player);
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      } else if (!var1.getRecipe().canUse(this.playerData, this.ingredients, var1, this.station)) {
         this.updateData();
      } else {
         if (var1.getRecipe().whenUsed(this.playerData, this.ingredients, var1, this.station)) {
            var1.getIngredients().forEach(var0 -> var0.takeAway());
            var1.getConditions().forEach(var1x -> var1x.getCondition().whenCrafting(this.playerData));
            var1.getRecipe().whenUsed().forEach(var1x -> var1x.whenCrafting(this.playerData));
            this.updateData();
         }
      }
   }

   @Nullable
   private CheckedRecipe getRecipe(@NotNull String var1) {
      for (CheckedRecipe var3 : this.recipes) {
         if (var3.getRecipe().getId().equals(var1)) {
            return var3;
         }
      }

      return null;
   }
}
