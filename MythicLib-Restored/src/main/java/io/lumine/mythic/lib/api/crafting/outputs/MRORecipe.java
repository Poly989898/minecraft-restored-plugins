package io.lumine.mythic.lib.api.crafting.outputs;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.ShapedIngredient;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCachedResult;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipe;
import io.lumine.mythic.lib.api.crafting.recipes.ShapedRecipe;
import io.lumine.mythic.lib.api.crafting.recipes.VanillaBookableOutput;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import io.lumine.mythic.lib.api.crafting.uifilters.IngredientUIFilter;
import io.lumine.mythic.lib.api.crafting.uifilters.UIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MRORecipe extends MythicRecipeOutput implements VanillaBookableOutput {
   @NotNull
   final ShapedRecipe output;
   @NotNull
   final String recipeFFPPrefix;
   @Nullable
   MythicRecipe mainInputConsumption;
   @NotNull
   final HashMap<String, MythicRecipe> sideInputConsumptions = new HashMap<>();
   boolean random = false;
   @Nullable
   MythicRecipeInventory determinateDisplay = null;
   @Nullable
   MythicRecipeInventory determinateResult = null;

   @NotNull
   public ShapedRecipe getOutput() {
      return this.output;
   }

   public MRORecipe(@NotNull ShapedRecipe output) {
      this.output = output;
      this.recipeFFPPrefix = "Mythic Recipe $u" + this.getOutput().getName();
      this.analyzeDetermination();
   }

   public MRORecipe(@NotNull ShapedRecipe output, @Nullable ShapedRecipe mainInput) {
      this(output);
      this.mainInputConsumption = mainInput;
   }

   @Nullable
   public MythicRecipe getMainInputConsumption() {
      return this.mainInputConsumption;
   }

   public void setMainInputConsumption(@Nullable MythicRecipe mic) {
      this.mainInputConsumption = this.ifEmptyNull(mic);
      this.analyzeDetermination();
   }

   public boolean hasInputConsumption() {
      return this.sideInputConsumptions.size() > 0 || this.mainInputConsumption != null;
   }

   @NotNull
   public ArrayList<String> getSideConsumptionNames() {
      return new ArrayList<>(this.sideInputConsumptions.keySet());
   }

   @NotNull
   public MythicRecipe getSideConsumption(@NotNull String ofName) {
      Validate.isTrue(this.hasSideConsumption(ofName), "You may not query for a side recipe that does not exist.");
      return this.sideInputConsumptions.get(ofName);
   }

   public boolean hasSideConsumption(@NotNull String ofName) {
      return this.sideInputConsumptions.containsKey(ofName);
   }

   public void setSideConsumption(@NotNull String ofName, @Nullable MythicRecipe recipe) {
      if (this.ifEmptyNull(recipe) == null) {
         this.sideInputConsumptions.remove(ofName);
      } else {
         this.sideInputConsumptions.put(ofName, recipe);
         this.analyzeDetermination();
      }
   }

   @NotNull
   MythicRecipeInventory generateResultOf(@NotNull MythicRecipe mythicRecipe) {
      HashMap<Integer, ItemStack[]> rowsInformation = new HashMap<>();

      for (MythicRecipeIngredient mmIngredient : mythicRecipe.getIngredients()) {
         if (mmIngredient != null) {
            ShapedIngredient shaped = (ShapedIngredient)mmIngredient;
            MythicIngredient ingredient = mmIngredient.getIngredient();
            if (ingredient.isDefinesItem()) {
               FriendlyFeedbackProvider ffp = new FriendlyFeedbackProvider(FFPMythicLib.get());
               ffp.activatePrefix(true, this.recipeFFPPrefix);
               ItemStack gen = mmIngredient.getIngredient().getRandomSubstituteItem(ffp);
               if (gen != null) {
                  ItemStack[] row = rowsInformation.get(-shaped.getVerticalOffset());
                  if (row == null) {
                     row = new ItemStack[shaped.getHorizontalOffset() + 1];
                  }

                  if (row.length < shaped.getHorizontalOffset() + 1) {
                     ItemStack[] newRow = new ItemStack[shaped.getHorizontalOffset() + 1];

                     for (int r = 0; r < row.length; r++) {
                        newRow[r] = row[r];
                     }

                     row = newRow;
                  }

                  row[shaped.getHorizontalOffset()] = gen;
                  rowsInformation.put(-shaped.getVerticalOffset(), row);
               } else {
                  ffp.sendTo(FriendlyFeedbackCategory.ERROR, MythicLib.plugin.getServer().getConsoleSender());
               }
            }
         }
      }

      MythicRecipeInventory ret = new MythicRecipeInventory();

      for (Integer h : rowsInformation.keySet()) {
         ret.setRow(h, rowsInformation.get(h));
      }

      return ret;
   }

   @Nullable
   public MythicRecipe ifEmptyNull(@Nullable MythicRecipe mic) {
      if (mic == null) {
         return null;
      }

      for (MythicRecipeIngredient mri : mic.getIngredients()) {
         if (mri != null && mri.getIngredient().isDefinesItem()) {
            return mic;
         }
      }

      return null;
   }

   public boolean isRandom() {
      return this.random;
   }

   public void analyzeDetermination() {
      ArrayList<MythicRecipeIngredient> ultimateOutput = new ArrayList<>(this.getOutput().getIngredients());
      if (this.hasInputConsumption()) {
         if (this.getMainInputConsumption() != null) {
            ultimateOutput.addAll(this.getMainInputConsumption().getIngredients());
         }

         for (String sideName : this.getSideConsumptionNames()) {
            MythicRecipe sideConsumption = this.getSideConsumption(sideName);
            ultimateOutput.addAll(sideConsumption.getIngredients());
         }
      }

      for (MythicRecipeIngredient mIngredient : ultimateOutput) {
         ShapedIngredient shaped = (ShapedIngredient)mIngredient;
         if (shaped != null) {
            MythicIngredient ingredient = shaped.getIngredient();
            if (ingredient.isDefinesItem()) {
               if (ingredient.getSubstitutes().size() > 1) {
                  this.random = true;
                  this.determinateDisplay = null;
                  return;
               }

               ProvidedUIFilter poof = ingredient.getRandomSubstitute(false);
               if (poof != null) {
                  UIFilter filter = poof.getParent();
                  if (!filter.determinateGeneration() && !filter.partialDeterminateGeneration(poof.getArgument(), poof.getData())) {
                     this.random = true;
                     this.determinateDisplay = null;
                     return;
                  }
               }
            }
         }
      }

      this.random = false;
   }

   @Nullable
   MythicRecipeInventory getDeterminateDisplay() {
      if (this.isRandom()) {
         this.determinateDisplay = null;
         return null;
      }

      if (this.determinateDisplay != null) {
         return this.determinateDisplay.clone();
      }

      this.determinateDisplay = this.generateDisplay();
      return this.determinateDisplay.clone();
   }

   @NotNull
   MythicRecipeInventory generateDisplay() {
      HashMap<Integer, ItemStack[]> rowsInformation = new HashMap<>();

      for (MythicRecipeIngredient mmIngredient : this.getOutput().getIngredients()) {
         if (mmIngredient != null) {
            ShapedIngredient shaped = (ShapedIngredient)mmIngredient;
            MythicIngredient ingredient = mmIngredient.getIngredient();
            if (ingredient.isDefinesItem()) {
               FriendlyFeedbackProvider ffp = new FriendlyFeedbackProvider(FFPMythicLib.get());
               ffp.activatePrefix(true, this.recipeFFPPrefix);
               ItemStack gen = mmIngredient.getIngredient().getRandomDisplayItem(ffp);
               if (ingredient.getSubstitutes().size() > 1) {
                  ItemStack result = SilentNumbers.setItemName(new ItemStack(gen.getType()), SilentNumbers.findItemName(gen));
                  ArrayList<String> desc = new ArrayList<>();

                  for (String str : new IngredientUIFilter().getDescription(ingredient.getName(), "")) {
                     desc.add(MythicLib.plugin.parseColors(FriendlyFeedbackProvider.quickForPlayer(FFPMythicLib.get(), str)));
                  }

                  gen = ItemFactory.of(result).lore(desc).build();
               }

               ItemStack[] row = rowsInformation.get(-shaped.getVerticalOffset());
               if (row == null) {
                  row = new ItemStack[shaped.getHorizontalOffset() + 1];
               }

               if (row.length < shaped.getHorizontalOffset() + 1) {
                  ItemStack[] newRow = new ItemStack[shaped.getHorizontalOffset() + 1];

                  for (int r = 0; r < row.length; r++) {
                     newRow[r] = row[r];
                  }

                  row = newRow;
               }

               row[shaped.getHorizontalOffset()] = gen;
               rowsInformation.put(-shaped.getVerticalOffset(), row);
            }
         }
      }

      MythicRecipeInventory ret = new MythicRecipeInventory();

      for (Integer h : rowsInformation.keySet()) {
         ret.setRow(h, rowsInformation.get(h));
      }

      return ret;
   }

   @Nullable
   MythicRecipeInventory getDeterminateResult() {
      if (this.isRandom()) {
         this.determinateResult = null;
         return null;
      }

      if (this.determinateResult != null) {
         return this.determinateResult.clone();
      }

      this.determinateResult = this.generateResult();
      return this.determinateResult.clone();
   }

   @NotNull
   MythicRecipeInventory generateResult() {
      return this.generateResultOf(this.getOutput());
   }

   @NotNull
   @Override
   public MythicRecipeInventory applyDisplay(
      @NotNull MythicBlueprintInventory inventory, @NotNull InventoryClickEvent eventTrigger, @NotNull VanillaInventoryMapping mapping
   ) {
      MythicRecipeInventory result = this.getDeterminateDisplay();
      if (result == null) {
         result = this.generateDisplay();
      }

      MythicRecipeInventory ret = inventory.getResultInventory().clone();

      for (int h = 0; h < inventory.getResultInventory().getHeight(); h++) {
         for (int w = 0; w < inventory.getResultInventory().getWidth(); w++) {
            ItemStack item = result.getItemAt(w, -h);
            ret.setItemAt(w, -h, item);
         }
      }

      return ret;
   }

   @Override
   public void applyResult(
      @NotNull MythicRecipeInventory resultInventory,
      @NotNull MythicBlueprintInventory otherInventories,
      @NotNull MythicCachedResult cache,
      @NotNull InventoryClickEvent eventTrigger,
      @NotNull VanillaInventoryMapping map,
      int times
   ) {
      eventTrigger.setCancelled(true);
      if (eventTrigger.getWhoClicked() instanceof Player) {
         MythicCraftItemEvent preEvent = new MythicCraftItemEvent(eventTrigger, resultInventory, map, otherInventories, cache);
         Bukkit.getPluginManager().callEvent(preEvent);
         if (!preEvent.isCancelled()) {
            if (times == 1 && eventTrigger.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
               ItemStack currentInCursor = eventTrigger.getCursor();
               MythicRecipeInventory result = this.getDeterminateResult();
               if (result == null) {
                  result = this.generateResult();
               }

               this.processInventory(resultInventory, result, times);
               ItemStack cursor = resultInventory.getItemAt(map.getResultWidth(eventTrigger.getSlot()), map.getResultHeight(eventTrigger.getSlot()));
               if (cursor == null) {
                  cursor = new ItemStack(Material.AIR);
               }

               ItemStack actualCursor = cursor.clone();
               if (!SilentNumbers.isAir(currentInCursor)) {
                  if (!currentInCursor.isSimilar(actualCursor)) {
                     return;
                  }

                  int cAmount = currentInCursor.getAmount();
                  int aAmount = actualCursor.getAmount();
                  int maxAmount = actualCursor.getMaxStackSize();
                  if (cAmount + aAmount > maxAmount) {
                     return;
                  }

                  actualCursor.setAmount(cAmount + aAmount);
               }

               cursor.setAmount(0);
               map.applyToResultInventory(eventTrigger.getInventory(), resultInventory, false);
               VersionUtils.getView(eventTrigger).setCursor(actualCursor);
               this.consumeIngredients(otherInventories, cache, eventTrigger.getInventory(), map, times);
               if (this.hasInputConsumption()) {
                  ArrayList<ItemStack> inputConsumptionOverflow = new ArrayList<>();
                  if (this.getMainInputConsumption() != null) {
                     MythicRecipeInventory mainRead = map.getMainMythicInventory(eventTrigger.getInventory());
                     MythicRecipeInventory addedStuff = this.generateResultOf(this.getMainInputConsumption());
                     inputConsumptionOverflow.addAll(stackWhatsPossible(mainRead, addedStuff));
                     map.applyToMainInventory(eventTrigger.getInventory(), mainRead, false);
                  }

                  for (String sideName : this.getSideConsumptionNames()) {
                     if (map.getSideInventoryNames().contains(sideName)) {
                        MythicRecipeInventory sideRead = map.getSideMythicInventory(sideName, eventTrigger.getInventory());
                        MythicRecipeInventory addedStuff = this.generateResultOf(this.getSideConsumption(sideName));
                        inputConsumptionOverflow.addAll(stackWhatsPossible(sideRead, addedStuff));
                        map.applyToSideInventory(eventTrigger.getInventory(), sideRead, sideName, false);
                     }
                  }

                  distributeInInventoryOrDrop(eventTrigger.getWhoClicked().getInventory(), inputConsumptionOverflow, eventTrigger.getWhoClicked().getLocation());
               }
            } else {
               HashMap<Integer, ItemStack> modifiedInventory = null;
               Inventory inven = eventTrigger.getWhoClicked().getInventory();
               int trueTimes = 0;

               for (int t = 1; t <= times; trueTimes = t++) {
                  MythicRecipeInventory localResult = this.getDeterminateResult();
                  if (localResult == null) {
                     localResult = this.generateResult();
                  }

                  ArrayList<ItemStack> localOutput = toItemsList(localResult);
                  if (this.hasInputConsumption()) {
                     if (this.getMainInputConsumption() != null) {
                        MythicRecipeInventory addedStuff = this.generateResultOf(this.getMainInputConsumption());
                        localOutput.addAll(toItemsList(addedStuff));
                     }

                     for (String sideName : this.getSideConsumptionNames()) {
                        if (map.getSideInventoryNames().contains(sideName)) {
                           MythicRecipeInventory addedStuff = this.generateResultOf(this.getSideConsumption(sideName));
                           localOutput.addAll(toItemsList(addedStuff));
                        }
                     }
                  }

                  HashMap<Integer, ItemStack> localIterationResult = distributeInInventory(inven, localOutput, modifiedInventory);
                  if (localIterationResult == null) {
                     break;
                  }

                  modifiedInventory = localIterationResult;
               }

               if (trueTimes == 0) {
                  return;
               }

               times = trueTimes;

               for (Integer s : modifiedInventory.keySet()) {
                  ItemStack putt = modifiedInventory.get(s);
                  inven.setItem(s, putt);
               }

               this.consumeIngredients(otherInventories, cache, eventTrigger.getInventory(), map, times);
            }
         }
      }
   }

   public static ArrayList<ItemStack> toItemsList(@NotNull MythicRecipeInventory inventory) {
      ArrayList<ItemStack> ret = new ArrayList<>();

      for (int h = 0; h < inventory.getHeight(); h++) {
         for (int w = 0; w < inventory.getWidth(); w++) {
            ItemStack observed = inventory.getItemAt(w, -h);
            if (observed != null && !SilentNumbers.isAir(observed)) {
               ret.add(observed);
            }
         }
      }

      return ret;
   }

   @Nullable
   public static HashMap<Integer, ItemStack> distributeInInventory(
      @NotNull Inventory inven, @NotNull ArrayList<ItemStack> contents, @Nullable HashMap<Integer, ItemStack> olderResult
   ) {
      ArrayList<ItemStack> out = notRepeated(contents);
      HashMap<Integer, ItemStack> result = new HashMap<>();
      HashMap<Integer, ItemStack> invenItems = new HashMap<>();
      if (olderResult == null) {
         olderResult = new HashMap<>();
      }

      for (int s = 0; s < 36; s++) {
         ItemStack i = olderResult.get(s);
         if (!SilentNumbers.isAir(i)) {
            result.put(s, i);
            invenItems.put(s, i);
         } else {
            invenItems.put(s, inven.getItem(s));
         }
      }

      int completelyResolved = 0;

      for (int s = 0; s < 36 && completelyResolved < out.size(); s++) {
         ItemStack i = invenItems.get(s);
         if (!SilentNumbers.isAir(i)) {
            for (ItemStack itm : out) {
               if (itm.isSimilar(i) && itm.getType().getMaxStackSize() > i.getAmount()) {
                  int iCurrent = i.getAmount();
                  int iAdding = itm.getAmount();
                  int iMaxAdd = itm.getType().getMaxStackSize() - iCurrent;
                  int iAdded = Math.min(iMaxAdd, iAdding);
                  int iRem = iAdding - iAdded;
                  ItemStack put = asAmount(i, iCurrent + iAdded);
                  itm.setAmount(iRem);
                  result.put(s, put);
                  if (iRem == 0) {
                     completelyResolved++;
                  }
                  break;
               }
            }
         }
      }

      out = notRepeated(out);
      int o = out.size() - 1;

      for (int s = 0; s < 36 && o >= 0; s++) {
         if (completelyResolved == out.size()) {
            o = -1;
            break;
         }

         ItemStack i = invenItems.get(s);
         if (SilentNumbers.isAir(i)) {
            ItemStack oStack = out.get(o);
            if (oStack.getAmount() > 0) {
               result.put(s, oStack.clone());
               completelyResolved++;
            }

            o--;
         }
      }

      return o >= 0 ? null : result;
   }

   public static void distributeInInventoryOrDrop(@NotNull Inventory inven, @NotNull ArrayList<ItemStack> contents, @NotNull Location dropLocation) {
      if (inven.getSize() < 35) {
         throw new IndexOutOfBoundsException("Passing non-player inventory to this method; Expected a player's inventory");
      }

      HashMap<Integer, ItemStack> localIterationResult = distributeInInventory(inven, contents, null);
      if (localIterationResult != null) {
         for (Integer s : localIterationResult.keySet()) {
            ItemStack putt = localIterationResult.get(s);
            if (!SilentNumbers.isAir(putt)) {
               inven.setItem(s, putt);
            }
         }
      } else {
         for (ItemStack item : contents) {
            if (item != null && item.getType().isItem()) {
               dropLocation.getWorld().dropItem(dropLocation, item);
            }
         }
      }
   }

   @NotNull
   public static ArrayList<ItemStack> notRepeated(@NotNull ArrayList<ItemStack> contents) {
      ArrayList<ItemStack> ret = new ArrayList<>();

      for (ItemStack itm : contents) {
         if (!SilentNumbers.isAir(itm)) {
            boolean stacked = false;

            for (int c = 0; c < ret.size(); c++) {
               ItemStack comp = ret.get(c);
               Material mat = comp.getType();
               if (comp.isSimilar(itm) && comp.getAmount() < mat.getMaxStackSize()) {
                  ret.remove(c);
                  int current = comp.getAmount();
                  int added = itm.getAmount();

                  int total;
                  for (total = current + added; total > mat.getMaxStackSize(); total -= mat.getMaxStackSize()) {
                     ret.add(asAmount(comp, mat.getMaxStackSize()));
                  }

                  if (total > 0) {
                     ret.add(asAmount(comp, total));
                  }

                  stacked = true;
                  break;
               }
            }

            if (!stacked) {
               ret.add(itm);
            }
         }
      }

      return ret;
   }

   @NotNull
   public static ItemStack asAmount(@NotNull ItemStack comp, int amount) {
      ItemStack s = comp.clone();
      s.setAmount(amount);
      return s;
   }

   @NotNull
   @Override
   public ItemStack getBukkitRecipeResult() throws IllegalArgumentException {
      MythicRecipeInventory localResult = this.getDeterminateDisplay();
      if (localResult == null) {
         localResult = this.generateDisplay();
      }

      ItemStack first = localResult.getFirst();
      Validate.isTrue(first != null, "MRORecipe output cannot just not generate any Item Stacks");
      return first;
   }

   @NotNull
   public static ArrayList<ItemStack> stackWhatsPossible(@NotNull MythicRecipeInventory receivingInventory, @NotNull MythicRecipeInventory addedStuff) {
      ArrayList<ItemStack> inputConsumptionOverflow = new ArrayList<>();

      for (int h = 0; h < addedStuff.getHeight(); h++) {
         for (int w = 0; w < addedStuff.getWidth(); w++) {
            ItemStack resultItem = addedStuff.getItemAt(w, -h);
            if (!SilentNumbers.isAir(resultItem)) {
               ItemStack currentItem = receivingInventory.getItemAt(w, -h);
               if (!SilentNumbers.isAir(currentItem)) {
                  if (currentItem.isSimilar(resultItem)) {
                     int cAmount = currentItem.getAmount();
                     int aAmount = resultItem.getAmount();
                     int maxAmount = resultItem.getMaxStackSize();
                     if (cAmount + aAmount > maxAmount) {
                        currentItem.setAmount(maxAmount);
                        receivingInventory.setItemAt(w, -h, currentItem);
                        int addedAmount = maxAmount - cAmount;
                        int remainder = aAmount - addedAmount;
                        if (remainder > 0) {
                           resultItem.setAmount(remainder);
                           inputConsumptionOverflow.add(resultItem);
                        }
                     } else {
                        resultItem.setAmount(cAmount + aAmount);
                        receivingInventory.setItemAt(w, -h, resultItem);
                     }
                  } else {
                     inputConsumptionOverflow.add(resultItem);
                  }
               } else {
                  receivingInventory.setItemAt(w, -h, resultItem);
               }
            }
         }
      }

      return inputConsumptionOverflow;
   }
}
