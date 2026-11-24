package net.Indyuce.mmoitems.api.crafting.recipe;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.ShapedIngredient;
import io.lumine.mythic.lib.api.crafting.outputs.MRORecipe;
import io.lumine.mythic.lib.api.crafting.outputs.MythicRecipeOutput;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCachedResult;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipe;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.GemStone;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.data.EnchantListData;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomSmithingRecipe extends MythicRecipeOutput {
   @Nullable
   MythicRecipe mainInputConsumption;
   @Nullable
   MythicRecipe ingotInputConsumption;
   int outputAmount;
   @NotNull
   final MMOItemTemplate outputItem;
   final boolean dropGemstones;
   @NotNull
   final SmithingCombinationType enchantmentTreatment;
   @NotNull
   final SmithingCombinationType upgradeTreatment;

   public CustomSmithingRecipe(
      @NotNull MMOItemTemplate mmoItemTemplate, boolean flag, @NotNull SmithingCombinationType smithingCombinationType, @NotNull SmithingCombinationType upgradeCombinationType, int count
   ) {
      this.outputItem = mmoItemTemplate;
      this.dropGemstones = flag;
      this.enchantmentTreatment = smithingCombinationType;
      this.upgradeTreatment = upgradeCombinationType;
      this.outputAmount = count;
   }

   @Nullable
   public MythicRecipe getMainInputConsumption() {
      return this.mainInputConsumption;
   }

   public void setMainInputConsumption(@Nullable MythicRecipe mythicRecipe) {
      this.mainInputConsumption = this.nullifyIfEmpty(mythicRecipe);
   }

   public boolean hasInputConsumption() {
      return this.ingotInputConsumption != null || this.mainInputConsumption != null;
   }

   @Nullable
   public MythicRecipe nullifyIfEmpty(@Nullable MythicRecipe mythicRecipe) {
      if (mythicRecipe == null) {
         return null;
      }

      for (MythicRecipeIngredient ingredient : mythicRecipe.getIngredients()) {
         if (ingredient != null && ingredient.getIngredient().isDefinesItem()) {
            return mythicRecipe;
         }
      }

      return null;
   }

   @Nullable
   public MythicRecipe getIngotInputConsumption() {
      return this.ingotInputConsumption;
   }

   public void setIngotInputConsumption(@Nullable MythicRecipe mythicRecipe) {
      this.ingotInputConsumption = this.nullifyIfEmpty(mythicRecipe);
   }

   @NotNull
   MythicRecipeInventory generateResultOf(@NotNull MythicRecipe mythicRecipe) {
      HashMap<Integer, ItemStack[]> map = new HashMap<>();

      for (MythicRecipeIngredient ingredient : mythicRecipe.getIngredients()) {
         if (ingredient != null) {
            ShapedIngredient shapedIngredient = (ShapedIngredient)ingredient;
            MythicIngredient mythicIngredient = ingredient.getIngredient();
            if (mythicIngredient.isDefinesItem()) {
               FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
               friendlyFeedbackProvider.activatePrefix(true, "Recipe of " + this.getOutputItem().getType().getId() + " " + this.getOutputItem().getId());
               ItemStack randomSubstituteItem = ingredient.getIngredient().getRandomSubstituteItem(friendlyFeedbackProvider);
               if (randomSubstituteItem != null) {
                  ItemStack[] items = (ItemStack[])map.get(-shapedIngredient.getVerticalOffset());
                  if (items == null) {
                     items = new ItemStack[shapedIngredient.getHorizontalOffset() + 1];
                  }

                  if (items.length < shapedIngredient.getHorizontalOffset() + 1) {
                     ItemStack[] shapedItems = new ItemStack[shapedIngredient.getHorizontalOffset() + 1];

                     for (int i = 0; i < items.length; i++) {
                        shapedItems[i] = items[i];
                     }

                     items = shapedItems;
                  }

                  items[shapedIngredient.getHorizontalOffset()] = randomSubstituteItem;
                  map.put(-shapedIngredient.getVerticalOffset(), items);
               } else {
                  friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MythicLib.plugin.getServer().getConsoleSender());
               }
            }
         }
      }

      MythicRecipeInventory mythicRecipeInventory = new MythicRecipeInventory();

      for (Integer key : map.keySet()) {
         mythicRecipeInventory.setRow(key, (ItemStack[])map.get(key));
      }

      return mythicRecipeInventory;
   }

   public int getOutputAmount() {
      return this.outputAmount;
   }

   public void setOutputAmount(int count) {
      this.outputAmount = count;
   }

   @NotNull
   public MMOItemTemplate getOutputItem() {
      return this.outputItem;
   }

   @NotNull
   public SmithingCombinationType getEnchantmentTreatment() {
      return this.enchantmentTreatment;
   }

   public boolean isDropGemstones() {
      return this.dropGemstones;
   }

   @NotNull
   public SmithingCombinationType getUpgradeTreatment() {
      return this.upgradeTreatment;
   }

   @Nullable
   MMOItem fromStack(@Nullable ItemStack item) {
      if (item == null) {
         return null;
      }

      NBTItem nbtItem = NBTItem.get(item);
      return MMOItems.getType(nbtItem) != null ? new LiveMMOItem(item) : null;
   }

   @Nullable
   ItemStack firstFromFirstSide(@NotNull MythicBlueprintInventory mythicBlueprintInventory) {
      return mythicBlueprintInventory.getSideInventoryNames().size() == 0 ? null : mythicBlueprintInventory.getSideInventory((String)mythicBlueprintInventory.getSideInventoryNames().get(0)).getFirst();
   }

   @NotNull
   public MythicRecipeInventory applyDisplay(@NotNull MythicBlueprintInventory mythicBlueprintInventory, @NotNull InventoryClickEvent event, @NotNull VanillaInventoryMapping vanillaInventoryMapping) {
      if (!(event.getWhoClicked() instanceof Player)) {
         return mythicBlueprintInventory.getResultInventory();
      }

      MythicBlueprintInventory currentBlueprintInventory = vanillaInventoryMapping.extractFrom(VersionUtils.getView(event).getTopInventory());
      ItemStack first = currentBlueprintInventory.getMainInventory().getFirst();
      ItemStack item = this.firstFromFirstSide(currentBlueprintInventory);
      MMOItem mmoItem = this.fromCombinationWith(first, item, (Player)event.getWhoClicked(), null);
      MythicRecipeInventory mythicRecipeInventory = mythicBlueprintInventory.getResultInventory().clone();
      mythicRecipeInventory.setItemAt(vanillaInventoryMapping.getResultWidth(vanillaInventoryMapping.getResultInventoryStart()), vanillaInventoryMapping.getResultHeight(vanillaInventoryMapping.getResultInventoryStart()), mmoItem.newBuilder().build());
      return mythicRecipeInventory;
   }

   public void applyResult(
      @NotNull MythicRecipeInventory mythicRecipeInventory,
      @NotNull MythicBlueprintInventory mythicBlueprintInventory,
      @NotNull MythicCachedResult mythicCachedResult,
      @NotNull InventoryClickEvent event,
      @NotNull VanillaInventoryMapping vanillaInventoryMapping,
      int count
   ) {
      event.setCancelled(true);
      if (event.getWhoClicked() instanceof Player) {
         Player whoClicked = (Player)event.getWhoClicked();
         ItemStack first = mythicBlueprintInventory.getMainInventory().getFirst();
         ItemStack item = this.firstFromFirstSide(mythicBlueprintInventory);
         Ref<ArrayList<ItemStack>> ref = new Ref<>();
         MMOItem mmoItem = this.fromCombinationWith(first, item, whoClicked, ref);
         if (count == 1 && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            MythicRecipeInventory resultInventory = mythicBlueprintInventory.getResultInventory().clone();
            resultInventory.setItemAt(
               vanillaInventoryMapping.getResultWidth(vanillaInventoryMapping.getResultInventoryStart()), vanillaInventoryMapping.getResultHeight(vanillaInventoryMapping.getResultInventoryStart()), mmoItem.newBuilder().build()
            );
            ItemStack cursor = event.getCursor();
            this.processInventory(mythicRecipeInventory, resultInventory, 1);
            ItemStack itemAt = mythicRecipeInventory.getItemAt(vanillaInventoryMapping.getResultWidth(event.getSlot()), vanillaInventoryMapping.getResultHeight(event.getSlot()));
            if (itemAt == null) {
               itemAt = new ItemStack(Material.AIR);
            }

            ItemStack itemCopy = itemAt.clone();
            if (!SilentNumbers.isAir(cursor)) {
               if (!cursor.isSimilar(itemCopy)) {
                  return;
               }

               int amount = cursor.getAmount();
               int itemAmount = itemCopy.getAmount();
               int maxStackSize = itemCopy.getMaxStackSize();
               if (amount + itemAmount > maxStackSize) {
                  return;
               }

               itemCopy.setAmount(amount + itemAmount);
            }

            itemAt.setAmount(0);
            vanillaInventoryMapping.applyToResultInventory(event.getInventory(), mythicRecipeInventory, false);
            VersionUtils.getView(event).setCursor(itemCopy);
            this.consumeIngredients(mythicBlueprintInventory, mythicCachedResult, event.getInventory(), vanillaInventoryMapping, 1);
            if (this.hasInputConsumption()) {
               ArrayList list = new ArrayList();
               if (this.getMainInputConsumption() != null) {
                  MythicRecipeInventory mainMythicInventory = vanillaInventoryMapping.getMainMythicInventory(event.getInventory());
                  MythicRecipeInventory mainConsumption = this.generateResultOf(this.getMainInputConsumption());
                  list.addAll(MRORecipe.stackWhatsPossible(mainMythicInventory, mainConsumption));
                  vanillaInventoryMapping.applyToMainInventory(event.getInventory(), mainMythicInventory, false);
               }

               if (this.getIngotInputConsumption() != null) {
                  MythicRecipeInventory sideMythicInventory = vanillaInventoryMapping.getSideMythicInventory("ingot", event.getInventory());
                  MythicRecipeInventory ingotConsumption = this.generateResultOf(this.getIngotInputConsumption());
                  list.addAll(MRORecipe.stackWhatsPossible(sideMythicInventory, ingotConsumption));
                  vanillaInventoryMapping.applyToSideInventory(event.getInventory(), sideMythicInventory, "ingot", false);
               }

               MRORecipe.distributeInInventoryOrDrop(event.getWhoClicked().getInventory(), list, event.getWhoClicked().getLocation());
            }
         } else {
            HashMap<Integer, ItemStack> map = null;
            PlayerInventory inventory = whoClicked.getInventory();
            int totalAmount = 0;

            for (int i = 1; i <= count; totalAmount = i++) {
               MythicRecipeInventory resultInventory = mythicBlueprintInventory.getResultInventory().clone();
               resultInventory.setItemAt(
                  vanillaInventoryMapping.getResultWidth(vanillaInventoryMapping.getResultInventoryStart()), vanillaInventoryMapping.getResultHeight(vanillaInventoryMapping.getResultInventoryStart()), mmoItem.newBuilder().build()
               );
               ArrayList list = MRORecipe.toItemsList(resultInventory);
               if (this.hasInputConsumption()) {
                  if (this.getMainInputConsumption() != null) {
                     MythicRecipeInventory mainConsumption = this.generateResultOf(this.getMainInputConsumption());
                     list.addAll(MRORecipe.toItemsList(mainConsumption));
                  }

                  if (this.getIngotInputConsumption() != null) {
                     MythicRecipeInventory ingotConsumption = this.generateResultOf(this.getIngotInputConsumption());
                     list.addAll(MRORecipe.toItemsList(ingotConsumption));
                  }
               }

               HashMap<Integer, ItemStack> leftovers = MRORecipe.distributeInInventory(inventory, list, map);
               if (leftovers == null) {
                  break;
               }

               map = leftovers;
            }

            if (totalAmount == 0) {
               return;
            }

            count = totalAmount;

            for (Integer key : map.keySet()) {
               ItemStack slotItem = (ItemStack)map.get(key);
               inventory.setItem(key, slotItem);
            }

            this.consumeIngredients(mythicBlueprintInventory, mythicCachedResult, event.getInventory(), vanillaInventoryMapping, count);
         }

         if (this.isDropGemstones() && ref.getValue() != null && whoClicked.getLocation().getWorld() != null) {
            for (ItemStack leftoverItem : whoClicked.getInventory().addItem(ref.getValue().toArray(new ItemStack[0])).values()) {
               if (!SilentNumbers.isAir(leftoverItem)) {
                  whoClicked.getWorld().dropItem(whoClicked.getLocation(), leftoverItem);
               }
            }
         }
      }
   }

   @NotNull
   MMOItem fromCombinationWith(@Nullable ItemStack item, @Nullable ItemStack inputItem, @NotNull Player player, @Nullable Ref<ArrayList<ItemStack>> ref) {
      MMOItem mmoItem = this.fromStack(item);
      MMOItem inputMMOItem = this.fromStack(inputItem);
      MMOItem outputMMOItem = this.getOutputItem().newBuilder(0, null).build();
      ArrayList<MMOItem> mmoItems = new ArrayList();
      if (mmoItem != null) {
         mmoItem.extractGemstones().forEach(var1x -> mmoItems.add(var1x.getValue()));
      }

      if (inputMMOItem != null) {
         inputMMOItem.extractGemstones().forEach(var1x -> mmoItems.add(var1x.getValue()));
      }

      ArrayList list = new ArrayList();

      for (MMOItem sourceMMOItem : mmoItems) {
         GemSocketsData data = (GemSocketsData)outputMMOItem.getData(ItemStats.GEM_SOCKETS);
         if (data != null && data.getEmptySlots().size() != 0) {
            GemStone gemStone = new GemStone(PlayerData.get(player), sourceMMOItem.newBuilder().buildNBT());
            GemStone.ApplyResult applyResult = gemStone.applyOntoItem(outputMMOItem, outputMMOItem.getType(), "", false, true);
            if (applyResult.getType() == GemStone.ResultType.SUCCESS && applyResult.getResultAsMMOItem() != null) {
               outputMMOItem = applyResult.getResultAsMMOItem();
            } else {
               list.add(sourceMMOItem.newBuilder().build());
            }
         } else {
            list.add(sourceMMOItem.newBuilder().build());
         }
      }

      Ref.setValue(ref, list);
      if (this.getEnchantmentTreatment() != SmithingCombinationType.NONE) {
         EnchantListData data = (EnchantListData)outputMMOItem.getData(ItemStats.ENCHANTS);
         if (data == null) {
            data = (EnchantListData)ItemStats.ENCHANTS.getClearStatData();
         }

         EnchantListData enchantListData = mmoItem != null ? (EnchantListData)mmoItem.getData(ItemStats.ENCHANTS) : Enchants.fromVanilla(item);
         EnchantListData outputEnchants = inputMMOItem != null ? (EnchantListData)inputMMOItem.getData(ItemStats.ENCHANTS) : Enchants.fromVanilla(inputItem);

         for (Enchantment enchantment : Enchantment.values()) {
            int level = data.getLevel(enchantment);
            int inputLevel = enchantListData.getLevel(enchantment);
            int outputLevel = outputEnchants.getLevel(enchantment);
            int count;
            switch (this.getEnchantmentTreatment()) {
               case ADDITIVE:
                  count = inputLevel + outputLevel + level;
                  break;
               case MAXIMUM:
                  if (level == 0) {
                     level = inputLevel;
                  }

                  count = Math.max(level, Math.max(inputLevel, outputLevel));
                  break;
               case MINIMUM:
                  if (level == 0) {
                     level = inputLevel;
                  }

                  count = Math.max(level, Math.min(inputLevel, outputLevel));
                  break;
               default:
                  if (level == 0) {
                     count = SilentNumbers.ceil((inputLevel + outputLevel) / 2.0);
                  } else {
                     count = SilentNumbers.ceil((inputLevel + outputLevel + level) / 3.0);
                  }
            }

            data.addEnchant(enchantment, count);
         }

         outputMMOItem.setData(ItemStats.ENCHANTS, data);
      }

      if (outputMMOItem.hasUpgradeTemplate() && this.getUpgradeTreatment() != SmithingCombinationType.NONE) {
         int count = 0;
         if (mmoItem != null) {
            count = mmoItem.getUpgradeLevel();
         }

         int matchedAmount = 0;
         if (inputMMOItem != null) {
            matchedAmount = inputMMOItem.getUpgradeLevel();
         }

         int remainingAmount;
         switch (this.getUpgradeTreatment()) {
            case ADDITIVE:
               remainingAmount = count + matchedAmount;
               break;
            case MAXIMUM:
               remainingAmount = Math.max(count, matchedAmount);
               break;
            case MINIMUM:
               remainingAmount = Math.min(count, matchedAmount);
               break;
            default:
               remainingAmount = SilentNumbers.ceil((count + matchedAmount) / 2.0);
         }

         outputMMOItem.getUpgradeTemplate().upgradeTo(outputMMOItem, Math.min(remainingAmount, outputMMOItem.getMaxUpgradeLevel()));
      }

      return outputMMOItem;
   }
}
