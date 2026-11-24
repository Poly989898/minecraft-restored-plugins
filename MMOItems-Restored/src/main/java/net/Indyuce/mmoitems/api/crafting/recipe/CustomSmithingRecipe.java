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
      @NotNull MMOItemTemplate mmoItemTemplate, boolean flag, @NotNull SmithingCombinationType smithingCombinationType, @NotNull SmithingCombinationType smithingCombinationType2, int count
   ) {
      this.outputItem = mmoItemTemplate;
      this.dropGemstones = flag;
      this.enchantmentTreatment = smithingCombinationType;
      this.upgradeTreatment = smithingCombinationType2;
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
            MythicIngredient ingredient2 = ingredient.getIngredient();
            if (ingredient2.isDefinesItem()) {
               FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
               friendlyFeedbackProvider.activatePrefix(true, "Recipe of " + this.getOutputItem().getType().getId() + " " + this.getOutputItem().getId());
               ItemStack randomSubstituteItem = ingredient.getIngredient().getRandomSubstituteItem(friendlyFeedbackProvider);
               if (randomSubstituteItem != null) {
                  ItemStack[] items = (ItemStack[])map.get(-shapedIngredient.getVerticalOffset());
                  if (items == null) {
                     items = new ItemStack[shapedIngredient.getHorizontalOffset() + 1];
                  }

                  if (items.length < shapedIngredient.getHorizontalOffset() + 1) {
                     ItemStack[] items2 = new ItemStack[shapedIngredient.getHorizontalOffset() + 1];

                     for (int i = 0; i < items.length; i++) {
                        items2[i] = items[i];
                     }

                     items = items2;
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

      MythicBlueprintInventory mythicBlueprintInventory2 = vanillaInventoryMapping.extractFrom(VersionUtils.getView(event).getTopInventory());
      ItemStack first = mythicBlueprintInventory2.getMainInventory().getFirst();
      ItemStack item = this.firstFromFirstSide(mythicBlueprintInventory2);
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
            MythicRecipeInventory mythicRecipeInventory2 = mythicBlueprintInventory.getResultInventory().clone();
            mythicRecipeInventory2.setItemAt(
               vanillaInventoryMapping.getResultWidth(vanillaInventoryMapping.getResultInventoryStart()), vanillaInventoryMapping.getResultHeight(vanillaInventoryMapping.getResultInventoryStart()), mmoItem.newBuilder().build()
            );
            ItemStack cursor = event.getCursor();
            this.processInventory(mythicRecipeInventory, mythicRecipeInventory2, 1);
            ItemStack itemAt = mythicRecipeInventory.getItemAt(vanillaInventoryMapping.getResultWidth(event.getSlot()), vanillaInventoryMapping.getResultHeight(event.getSlot()));
            if (itemAt == null) {
               itemAt = new ItemStack(Material.AIR);
            }

            ItemStack item2 = itemAt.clone();
            if (!SilentNumbers.isAir(cursor)) {
               if (!cursor.isSimilar(item2)) {
                  return;
               }

               int amount = cursor.getAmount();
               int amount2 = item2.getAmount();
               int maxStackSize = item2.getMaxStackSize();
               if (amount + amount2 > maxStackSize) {
                  return;
               }

               item2.setAmount(amount + amount2);
            }

            itemAt.setAmount(0);
            vanillaInventoryMapping.applyToResultInventory(event.getInventory(), mythicRecipeInventory, false);
            VersionUtils.getView(event).setCursor(item2);
            this.consumeIngredients(mythicBlueprintInventory, mythicCachedResult, event.getInventory(), vanillaInventoryMapping, 1);
            if (this.hasInputConsumption()) {
               ArrayList list = new ArrayList();
               if (this.getMainInputConsumption() != null) {
                  MythicRecipeInventory mainMythicInventory = vanillaInventoryMapping.getMainMythicInventory(event.getInventory());
                  MythicRecipeInventory mythicRecipeInventory3 = this.generateResultOf(this.getMainInputConsumption());
                  list.addAll(MRORecipe.stackWhatsPossible(mainMythicInventory, mythicRecipeInventory3));
                  vanillaInventoryMapping.applyToMainInventory(event.getInventory(), mainMythicInventory, false);
               }

               if (this.getIngotInputConsumption() != null) {
                  MythicRecipeInventory sideMythicInventory = vanillaInventoryMapping.getSideMythicInventory("ingot", event.getInventory());
                  MythicRecipeInventory mythicRecipeInventory3 = this.generateResultOf(this.getIngotInputConsumption());
                  list.addAll(MRORecipe.stackWhatsPossible(sideMythicInventory, mythicRecipeInventory3));
                  vanillaInventoryMapping.applyToSideInventory(event.getInventory(), sideMythicInventory, "ingot", false);
               }

               MRORecipe.distributeInInventoryOrDrop(event.getWhoClicked().getInventory(), list, event.getWhoClicked().getLocation());
            }
         } else {
            HashMap<Integer, ItemStack> map = null;
            PlayerInventory inventory = whoClicked.getInventory();
            int count2 = 0;

            for (int i = 1; i <= count; count2 = i++) {
               MythicRecipeInventory mythicRecipeInventory2 = mythicBlueprintInventory.getResultInventory().clone();
               mythicRecipeInventory2.setItemAt(
                  vanillaInventoryMapping.getResultWidth(vanillaInventoryMapping.getResultInventoryStart()), vanillaInventoryMapping.getResultHeight(vanillaInventoryMapping.getResultInventoryStart()), mmoItem.newBuilder().build()
               );
               ArrayList list = MRORecipe.toItemsList(mythicRecipeInventory2);
               if (this.hasInputConsumption()) {
                  if (this.getMainInputConsumption() != null) {
                     MythicRecipeInventory mythicRecipeInventory3 = this.generateResultOf(this.getMainInputConsumption());
                     list.addAll(MRORecipe.toItemsList(mythicRecipeInventory3));
                  }

                  if (this.getIngotInputConsumption() != null) {
                     MythicRecipeInventory mythicRecipeInventory3 = this.generateResultOf(this.getIngotInputConsumption());
                     list.addAll(MRORecipe.toItemsList(mythicRecipeInventory3));
                  }
               }

               HashMap<Integer, ItemStack> map2 = MRORecipe.distributeInInventory(inventory, list, map);
               if (map2 == null) {
                  break;
               }

               map = map2;
            }

            if (count2 == 0) {
               return;
            }

            count = count2;

            for (Integer key : map.keySet()) {
               ItemStack item2 = (ItemStack)map.get(key);
               inventory.setItem(key, item2);
            }

            this.consumeIngredients(mythicBlueprintInventory, mythicCachedResult, event.getInventory(), vanillaInventoryMapping, count);
         }

         if (this.isDropGemstones() && ref.getValue() != null && whoClicked.getLocation().getWorld() != null) {
            for (ItemStack item2 : whoClicked.getInventory().addItem(ref.getValue().toArray(new ItemStack[0])).values()) {
               if (!SilentNumbers.isAir(item2)) {
                  whoClicked.getWorld().dropItem(whoClicked.getLocation(), item2);
               }
            }
         }
      }
   }

   @NotNull
   MMOItem fromCombinationWith(@Nullable ItemStack item, @Nullable ItemStack item2, @NotNull Player player, @Nullable Ref<ArrayList<ItemStack>> ref) {
      MMOItem mmoItem = this.fromStack(item);
      MMOItem mmoItem2 = this.fromStack(item2);
      MMOItem mmoItem3 = this.getOutputItem().newBuilder(0, null).build();
      ArrayList<MMOItem> mmoItems = new ArrayList();
      if (mmoItem != null) {
         mmoItem.extractGemstones().forEach(var1x -> mmoItems.add(var1x.getValue()));
      }

      if (mmoItem2 != null) {
         mmoItem2.extractGemstones().forEach(var1x -> mmoItems.add(var1x.getValue()));
      }

      ArrayList list = new ArrayList();

      for (MMOItem mmoItem4 : mmoItems) {
         GemSocketsData data = (GemSocketsData)mmoItem3.getData(ItemStats.GEM_SOCKETS);
         if (data != null && data.getEmptySlots().size() != 0) {
            GemStone gemStone = new GemStone(PlayerData.get(player), mmoItem4.newBuilder().buildNBT());
            GemStone.ApplyResult applyResult = gemStone.applyOntoItem(mmoItem3, mmoItem3.getType(), "", false, true);
            if (applyResult.getType() == GemStone.ResultType.SUCCESS && applyResult.getResultAsMMOItem() != null) {
               mmoItem3 = applyResult.getResultAsMMOItem();
            } else {
               list.add(mmoItem4.newBuilder().build());
            }
         } else {
            list.add(mmoItem4.newBuilder().build());
         }
      }

      Ref.setValue(ref, list);
      if (this.getEnchantmentTreatment() != SmithingCombinationType.NONE) {
         EnchantListData data = (EnchantListData)mmoItem3.getData(ItemStats.ENCHANTS);
         if (data == null) {
            data = (EnchantListData)ItemStats.ENCHANTS.getClearStatData();
         }

         EnchantListData enchantListData = mmoItem != null ? (EnchantListData)mmoItem.getData(ItemStats.ENCHANTS) : Enchants.fromVanilla(item);
         EnchantListData enchantListData2 = mmoItem2 != null ? (EnchantListData)mmoItem2.getData(ItemStats.ENCHANTS) : Enchants.fromVanilla(item2);

         for (Enchantment enchantment : Enchantment.values()) {
            int level = data.getLevel(enchantment);
            int level2 = enchantListData.getLevel(enchantment);
            int level3 = enchantListData2.getLevel(enchantment);
            int count;
            switch (this.getEnchantmentTreatment()) {
               case ADDITIVE:
                  count = level2 + level3 + level;
                  break;
               case MAXIMUM:
                  if (level == 0) {
                     level = level2;
                  }

                  count = Math.max(level, Math.max(level2, level3));
                  break;
               case MINIMUM:
                  if (level == 0) {
                     level = level2;
                  }

                  count = Math.max(level, Math.min(level2, level3));
                  break;
               default:
                  if (level == 0) {
                     count = SilentNumbers.ceil((level2 + level3) / 2.0);
                  } else {
                     count = SilentNumbers.ceil((level2 + level3 + level) / 3.0);
                  }
            }

            data.addEnchant(enchantment, count);
         }

         mmoItem3.setData(ItemStats.ENCHANTS, data);
      }

      if (mmoItem3.hasUpgradeTemplate() && this.getUpgradeTreatment() != SmithingCombinationType.NONE) {
         int count = 0;
         if (mmoItem != null) {
            count = mmoItem.getUpgradeLevel();
         }

         int count2 = 0;
         if (mmoItem2 != null) {
            count2 = mmoItem2.getUpgradeLevel();
         }

         int count3;
         switch (this.getUpgradeTreatment()) {
            case ADDITIVE:
               count3 = count + count2;
               break;
            case MAXIMUM:
               count3 = Math.max(count, count2);
               break;
            case MINIMUM:
               count3 = Math.min(count, count2);
               break;
            default:
               count3 = SilentNumbers.ceil((count + count2) / 2.0);
         }

         mmoItem3.getUpgradeTemplate().upgradeTo(mmoItem3, Math.min(count3, mmoItem3.getMaxUpgradeLevel()));
      }

      return mmoItem3;
   }
}
