package io.lumine.mythic.lib.api.crafting.recipes;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.outputs.MythicRecipeOutput;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicRecipeBlueprint {
   @NotNull
   final NamespacedKey nk;
   final boolean canDisplayInRecipeBook;
   @NotNull
   final MythicRecipeOutput result;
   @NotNull
   final MythicRecipe mainCheck;
   @NotNull
   final HashMap<String, MythicRecipe> sideChecks = new HashMap<>();
   @NotNull
   final ArrayList<MythicRecipeStation> deployedFor = new ArrayList<>();
   @NotNull
   final ArrayList<String> requiredPermissions = new ArrayList<>();

   public MythicRecipeBlueprint(@NotNull MythicRecipe mainCheck, @NotNull MythicRecipeOutput result) {
      this.mainCheck = mainCheck;
      this.result = result;
      this.nk = new NamespacedKey(MythicLib.plugin, this.getMainCheck().getName());
      this.canDisplayInRecipeBook = this.getMainCheck() instanceof VanillaBookableRecipe && this.getResult() instanceof VanillaBookableOutput;
   }

   public MythicRecipeBlueprint(@NotNull MythicRecipe mainCheck, @NotNull MythicRecipeOutput result, @NotNull NamespacedKey key) {
      this.mainCheck = mainCheck;
      this.result = result;
      this.nk = key;
      this.canDisplayInRecipeBook = this.getMainCheck() instanceof VanillaBookableRecipe && this.getResult() instanceof VanillaBookableOutput;
   }

   @NotNull
   public NamespacedKey getNk() {
      return this.nk;
   }

   public boolean canDisplayInRecipeBook() {
      return this.canDisplayInRecipeBook;
   }

   @NotNull
   public MythicRecipeOutput getResult() {
      return this.result;
   }

   @NotNull
   public MythicRecipe getMainCheck() {
      return this.mainCheck;
   }

   @NotNull
   public ArrayList<String> getSideInventoryNames() {
      return new ArrayList<>(this.sideChecks.keySet());
   }

   @NotNull
   public MythicRecipe getSideCheck(@NotNull String ofName) {
      Validate.isTrue(this.hasSideInventory(ofName), "You may not query for a side recipe that does not exist.");
      return this.sideChecks.get(ofName);
   }

   public boolean hasSideInventory(@NotNull String ofName) {
      return this.sideChecks.containsKey(ofName);
   }

   public void addSideCheck(@NotNull String ofName, @NotNull MythicRecipe recipe) {
      this.sideChecks.put(ofName, recipe);
   }

   @Nullable
   public MythicBlueprintInventory matches(@NotNull MythicBlueprintInventory inventories, @Nullable Ref<Integer> maxTimes) {
      if (!SilentNumbers.hasAll(inventories.getSideInventoryNames(), this.getSideInventoryNames())) {
         return null;
      }

      Ref<Integer> mainTimes = new Ref<>();
      MythicRecipeInventory mainResult = this.getMainCheck().matches(inventories.getMainInventory(), mainTimes);
      if (mainResult == null) {
         return null;
      }

      MythicBlueprintInventory ret = new MythicBlueprintInventory(mainResult, inventories.getResultInventory());
      Integer limitingSideReactions = null;

      for (String side : this.getSideInventoryNames()) {
         Ref<Integer> sideTimes = new Ref<>();
         MythicRecipeInventory sideResult = this.getSideCheck(side).matches(inventories.getSideInventory(side), sideTimes);
         if (sideResult == null) {
            return null;
         }

         ret.addSideInventory(side, sideResult);
         if (limitingSideReactions == null) {
            limitingSideReactions = sideTimes.getValue();
         } else if (limitingSideReactions > sideTimes.getValue(32767)) {
            limitingSideReactions = sideTimes.getValue();
         }
      }

      if (maxTimes != null) {
         int limitingTimes;
         if (limitingSideReactions != null) {
            limitingTimes = Math.min(limitingSideReactions, mainTimes.getValue(1));
         } else {
            limitingTimes = mainTimes.getValue(1);
         }

         maxTimes.setValue(limitingTimes);
      }

      return ret;
   }

   public void deploy(@NotNull MythicRecipeStation forStation) {
      this.deploy(forStation, null);
   }

   public void deploy(@NotNull MythicRecipeStation forStation, @Nullable Ref<NamespacedKey> recipeName) {
      MythicCraftingManager.deployBlueprint(this, forStation);
      if (this.canDisplayInRecipeBook() && recipeName != null) {
         try {
            ItemStack recipeOutput = ((VanillaBookableOutput)this.getResult()).getBukkitRecipeResult();
            ItemMeta recipeOutputMeta = recipeOutput.getItemMeta();
            if (recipeOutputMeta != null && recipeOutputMeta.hasLore() && MythicLib.plugin.getMMOConfig().fixTooLargePackets) {
               recipeOutputMeta.setLore(new ArrayList());
               recipeOutput.setItemMeta(recipeOutputMeta);
            }

            Bukkit.addRecipe(((VanillaBookableRecipe)this.getMainCheck()).getBukkitRecipe(this.getNk(), recipeOutput));
            recipeName.setValue(this.nk);
         } catch (IllegalArgumentException | IllegalStateException runtimeException) {
            if (runtimeException instanceof IllegalStateException) {
               MythicLib.plugin
                  .getServer()
                  .getConsoleSender()
                  .sendMessage(
                     FriendlyFeedbackProvider.quickForConsole(
                        FFPMythicLib.get(), "Could not register recipe for crafting book: Recipe of name '$u{0}$b' already registered.", this.getNk().getKey()
                     )
                  );
            } else {
               MythicLib.plugin
                  .getServer()
                  .getConsoleSender()
                  .sendMessage(
                     FriendlyFeedbackProvider.quickForConsole(
                        FFPMythicLib.get(), "Could not register recipe for crafting book: ", ((IllegalArgumentException)runtimeException).getMessage()
                     )
                  );
            }
         }
      }
   }

   @NotNull
   public ArrayList<MythicRecipeStation> getDeployedFor() {
      return this.deployedFor;
   }

   protected void registerAsDeployed(@NotNull MythicRecipeStation st) {
      this.getDeployedFor().add(st);
   }

   public void disable() {
      for (MythicRecipeStation st : this.getDeployedFor()) {
         MythicCraftingManager.disableBlueprint(this, st);
      }

      this.deployedFor.clear();
   }

   @NotNull
   public ArrayList<String> getRequiredPermissions() {
      return this.requiredPermissions;
   }

   public void addRequiredPermission(@NotNull String perm) {
      this.requiredPermissions.add(perm);
   }

   public void clearRequiredPermissions() {
      this.requiredPermissions.clear();
   }

   public boolean checkPermissions(@NotNull Permissible player) {
      for (String perm : this.getRequiredPermissions()) {
         if (!player.hasPermission(perm)) {
            return false;
         }
      }

      return true;
   }
}
