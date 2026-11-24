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
      @NotNull MMOItemTemplate var1, boolean var2, @NotNull SmithingCombinationType var3, @NotNull SmithingCombinationType var4, int var5
   ) {
      this.outputItem = var1;
      this.dropGemstones = var2;
      this.enchantmentTreatment = var3;
      this.upgradeTreatment = var4;
      this.outputAmount = var5;
   }

   @Nullable
   public MythicRecipe getMainInputConsumption() {
      return this.mainInputConsumption;
   }

   public void setMainInputConsumption(@Nullable MythicRecipe var1) {
      this.mainInputConsumption = this.nullifyIfEmpty(var1);
   }

   public boolean hasInputConsumption() {
      return this.ingotInputConsumption != null || this.mainInputConsumption != null;
   }

   @Nullable
   public MythicRecipe nullifyIfEmpty(@Nullable MythicRecipe var1) {
      if (var1 == null) {
         return null;
      }

      for (MythicRecipeIngredient var3 : var1.getIngredients()) {
         if (var3 != null && var3.getIngredient().isDefinesItem()) {
            return var1;
         }
      }

      return null;
   }

   @Nullable
   public MythicRecipe getIngotInputConsumption() {
      return this.ingotInputConsumption;
   }

   public void setIngotInputConsumption(@Nullable MythicRecipe var1) {
      this.ingotInputConsumption = this.nullifyIfEmpty(var1);
   }

   @NotNull
   MythicRecipeInventory generateResultOf(@NotNull MythicRecipe var1) {
      HashMap<Integer, ItemStack[]> var2 = new HashMap<>();

      for (MythicRecipeIngredient var4 : var1.getIngredients()) {
         if (var4 != null) {
            ShapedIngredient var5 = (ShapedIngredient)var4;
            MythicIngredient var6 = var4.getIngredient();
            if (var6.isDefinesItem()) {
               FriendlyFeedbackProvider var7 = new FriendlyFeedbackProvider(FFPMMOItems.get());
               var7.activatePrefix(true, "Recipe of " + this.getOutputItem().getType().getId() + " " + this.getOutputItem().getId());
               ItemStack var8 = var4.getIngredient().getRandomSubstituteItem(var7);
               if (var8 != null) {
                  ItemStack[] var9 = (ItemStack[])var2.get(-var5.getVerticalOffset());
                  if (var9 == null) {
                     var9 = new ItemStack[var5.getHorizontalOffset() + 1];
                  }

                  if (var9.length < var5.getHorizontalOffset() + 1) {
                     ItemStack[] var10 = new ItemStack[var5.getHorizontalOffset() + 1];

                     for (int var11 = 0; var11 < var9.length; var11++) {
                        var10[var11] = var9[var11];
                     }

                     var9 = var10;
                  }

                  var9[var5.getHorizontalOffset()] = var8;
                  var2.put(-var5.getVerticalOffset(), var9);
               } else {
                  var7.sendTo(FriendlyFeedbackCategory.ERROR, MythicLib.plugin.getServer().getConsoleSender());
               }
            }
         }
      }

      MythicRecipeInventory var12 = new MythicRecipeInventory();

      for (Integer var14 : var2.keySet()) {
         var12.setRow(var14, (ItemStack[])var2.get(var14));
      }

      return var12;
   }

   public int getOutputAmount() {
      return this.outputAmount;
   }

   public void setOutputAmount(int var1) {
      this.outputAmount = var1;
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
   MMOItem fromStack(@Nullable ItemStack var1) {
      if (var1 == null) {
         return null;
      }

      NBTItem var2 = NBTItem.get(var1);
      return MMOItems.getType(var2) != null ? new LiveMMOItem(var1) : null;
   }

   @Nullable
   ItemStack firstFromFirstSide(@NotNull MythicBlueprintInventory var1) {
      return var1.getSideInventoryNames().size() == 0 ? null : var1.getSideInventory((String)var1.getSideInventoryNames().get(0)).getFirst();
   }

   @NotNull
   public MythicRecipeInventory applyDisplay(@NotNull MythicBlueprintInventory var1, @NotNull InventoryClickEvent var2, @NotNull VanillaInventoryMapping var3) {
      if (!(var2.getWhoClicked() instanceof Player)) {
         return var1.getResultInventory();
      }

      MythicBlueprintInventory var4 = var3.extractFrom(VersionUtils.getView(var2).getTopInventory());
      ItemStack var5 = var4.getMainInventory().getFirst();
      ItemStack var6 = this.firstFromFirstSide(var4);
      MMOItem var7 = this.fromCombinationWith(var5, var6, (Player)var2.getWhoClicked(), null);
      MythicRecipeInventory var8 = var1.getResultInventory().clone();
      var8.setItemAt(var3.getResultWidth(var3.getResultInventoryStart()), var3.getResultHeight(var3.getResultInventoryStart()), var7.newBuilder().build());
      return var8;
   }

   public void applyResult(
      @NotNull MythicRecipeInventory var1,
      @NotNull MythicBlueprintInventory var2,
      @NotNull MythicCachedResult var3,
      @NotNull InventoryClickEvent var4,
      @NotNull VanillaInventoryMapping var5,
      int var6
   ) {
      var4.setCancelled(true);
      if (var4.getWhoClicked() instanceof Player) {
         Player var7 = (Player)var4.getWhoClicked();
         ItemStack var8 = var2.getMainInventory().getFirst();
         ItemStack var9 = this.firstFromFirstSide(var2);
         Ref<ArrayList<ItemStack>> var10 = new Ref<>();
         MMOItem var11 = this.fromCombinationWith(var8, var9, var7, var10);
         if (var6 == 1 && var4.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            MythicRecipeInventory var20 = var2.getResultInventory().clone();
            var20.setItemAt(
               var5.getResultWidth(var5.getResultInventoryStart()), var5.getResultHeight(var5.getResultInventoryStart()), var11.newBuilder().build()
            );
            ItemStack var22 = var4.getCursor();
            this.processInventory(var1, var20, 1);
            ItemStack var24 = var1.getItemAt(var5.getResultWidth(var4.getSlot()), var5.getResultHeight(var4.getSlot()));
            if (var24 == null) {
               var24 = new ItemStack(Material.AIR);
            }

            ItemStack var26 = var24.clone();
            if (!SilentNumbers.isAir(var22)) {
               if (!var22.isSimilar(var26)) {
                  return;
               }

               int var28 = var22.getAmount();
               int var31 = var26.getAmount();
               int var36 = var26.getMaxStackSize();
               if (var28 + var31 > var36) {
                  return;
               }

               var26.setAmount(var28 + var31);
            }

            var24.setAmount(0);
            var5.applyToResultInventory(var4.getInventory(), var1, false);
            VersionUtils.getView(var4).setCursor(var26);
            this.consumeIngredients(var2, var3, var4.getInventory(), var5, 1);
            if (this.hasInputConsumption()) {
               ArrayList var29 = new ArrayList();
               if (this.getMainInputConsumption() != null) {
                  MythicRecipeInventory var32 = var5.getMainMythicInventory(var4.getInventory());
                  MythicRecipeInventory var37 = this.generateResultOf(this.getMainInputConsumption());
                  var29.addAll(MRORecipe.stackWhatsPossible(var32, var37));
                  var5.applyToMainInventory(var4.getInventory(), var32, false);
               }

               if (this.getIngotInputConsumption() != null) {
                  MythicRecipeInventory var33 = var5.getSideMythicInventory("ingot", var4.getInventory());
                  MythicRecipeInventory var38 = this.generateResultOf(this.getIngotInputConsumption());
                  var29.addAll(MRORecipe.stackWhatsPossible(var33, var38));
                  var5.applyToSideInventory(var4.getInventory(), var33, "ingot", false);
               }

               MRORecipe.distributeInInventoryOrDrop(var4.getWhoClicked().getInventory(), var29, var4.getWhoClicked().getLocation());
            }
         } else {
            HashMap<Integer, ItemStack> var12 = null;
            PlayerInventory var13 = var7.getInventory();
            int var14 = 0;

            for (int var15 = 1; var15 <= var6; var14 = var15++) {
               MythicRecipeInventory var16 = var2.getResultInventory().clone();
               var16.setItemAt(
                  var5.getResultWidth(var5.getResultInventoryStart()), var5.getResultHeight(var5.getResultInventoryStart()), var11.newBuilder().build()
               );
               ArrayList var17 = MRORecipe.toItemsList(var16);
               if (this.hasInputConsumption()) {
                  if (this.getMainInputConsumption() != null) {
                     MythicRecipeInventory var18 = this.generateResultOf(this.getMainInputConsumption());
                     var17.addAll(MRORecipe.toItemsList(var18));
                  }

                  if (this.getIngotInputConsumption() != null) {
                     MythicRecipeInventory var34 = this.generateResultOf(this.getIngotInputConsumption());
                     var17.addAll(MRORecipe.toItemsList(var34));
                  }
               }

               HashMap<Integer, ItemStack> var35 = MRORecipe.distributeInInventory(var13, var17, var12);
               if (var35 == null) {
                  break;
               }

               var12 = var35;
            }

            if (var14 == 0) {
               return;
            }

            var6 = var14;

            for (Integer var27 : var12.keySet()) {
               ItemStack var30 = (ItemStack)var12.get(var27);
               var13.setItem(var27, var30);
            }

            this.consumeIngredients(var2, var3, var4.getInventory(), var5, var6);
         }

         if (this.isDropGemstones() && var10.getValue() != null && var7.getLocation().getWorld() != null) {
            for (ItemStack var23 : var7.getInventory().addItem(var10.getValue().toArray(new ItemStack[0])).values()) {
               if (!SilentNumbers.isAir(var23)) {
                  var7.getWorld().dropItem(var7.getLocation(), var23);
               }
            }
         }
      }
   }

   @NotNull
   MMOItem fromCombinationWith(@Nullable ItemStack var1, @Nullable ItemStack var2, @NotNull Player var3, @Nullable Ref<ArrayList<ItemStack>> var4) {
      MMOItem var5 = this.fromStack(var1);
      MMOItem var6 = this.fromStack(var2);
      MMOItem var7 = this.getOutputItem().newBuilder(0, null).build();
      ArrayList<MMOItem> var8 = new ArrayList();
      if (var5 != null) {
         var5.extractGemstones().forEach(var1x -> var8.add(var1x.getValue()));
      }

      if (var6 != null) {
         var6.extractGemstones().forEach(var1x -> var8.add(var1x.getValue()));
      }

      ArrayList var9 = new ArrayList();

      for (MMOItem var11 : var8) {
         GemSocketsData var12 = (GemSocketsData)var7.getData(ItemStats.GEM_SOCKETS);
         if (var12 != null && var12.getEmptySlots().size() != 0) {
            GemStone var13 = new GemStone(PlayerData.get(var3), var11.newBuilder().buildNBT());
            GemStone.ApplyResult var14 = var13.applyOntoItem(var7, var7.getType(), "", false, true);
            if (var14.getType() == GemStone.ResultType.SUCCESS && var14.getResultAsMMOItem() != null) {
               var7 = var14.getResultAsMMOItem();
            } else {
               var9.add(var11.newBuilder().build());
            }
         } else {
            var9.add(var11.newBuilder().build());
         }
      }

      Ref.setValue(var4, var9);
      if (this.getEnchantmentTreatment() != SmithingCombinationType.NONE) {
         EnchantListData var21 = (EnchantListData)var7.getData(ItemStats.ENCHANTS);
         if (var21 == null) {
            var21 = (EnchantListData)ItemStats.ENCHANTS.getClearStatData();
         }

         EnchantListData var23 = var5 != null ? (EnchantListData)var5.getData(ItemStats.ENCHANTS) : Enchants.fromVanilla(var1);
         EnchantListData var25 = var6 != null ? (EnchantListData)var6.getData(ItemStats.ENCHANTS) : Enchants.fromVanilla(var2);

         for (Enchantment var16 : Enchantment.values()) {
            int var17 = var21.getLevel(var16);
            int var18 = var23.getLevel(var16);
            int var19 = var25.getLevel(var16);
            int var20;
            switch (this.getEnchantmentTreatment()) {
               case ADDITIVE:
                  var20 = var18 + var19 + var17;
                  break;
               case MAXIMUM:
                  if (var17 == 0) {
                     var17 = var18;
                  }

                  var20 = Math.max(var17, Math.max(var18, var19));
                  break;
               case MINIMUM:
                  if (var17 == 0) {
                     var17 = var18;
                  }

                  var20 = Math.max(var17, Math.min(var18, var19));
                  break;
               default:
                  if (var17 == 0) {
                     var20 = SilentNumbers.ceil((var18 + var19) / 2.0);
                  } else {
                     var20 = SilentNumbers.ceil((var18 + var19 + var17) / 3.0);
                  }
            }

            var21.addEnchant(var16, var20);
         }

         var7.setData(ItemStats.ENCHANTS, var21);
      }

      if (var7.hasUpgradeTemplate() && this.getUpgradeTreatment() != SmithingCombinationType.NONE) {
         int var22 = 0;
         if (var5 != null) {
            var22 = var5.getUpgradeLevel();
         }

         int var24 = 0;
         if (var6 != null) {
            var24 = var6.getUpgradeLevel();
         }

         int var26;
         switch (this.getUpgradeTreatment()) {
            case ADDITIVE:
               var26 = var22 + var24;
               break;
            case MAXIMUM:
               var26 = Math.max(var22, var24);
               break;
            case MINIMUM:
               var26 = Math.min(var22, var24);
               break;
            default:
               var26 = SilentNumbers.ceil((var22 + var24) / 2.0);
         }

         var7.getUpgradeTemplate().upgradeTo(var7, Math.min(var26, var7.getMaxUpgradeLevel()));
      }

      return var7;
   }
}
