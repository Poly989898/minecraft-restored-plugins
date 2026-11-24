package io.lumine.mythic.lib.api.crafting.recipes;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MythicCachedResult {
   @NotNull
   static final HashMap<UUID, MythicCachedResult> results = new HashMap<>();
   public static final String cacheNBTTagPath = "MYTHICLIB_CRAFTING";
   @NotNull
   final UUID operationIdentifier;
   @NotNull
   final MythicBlueprintInventory resultOfOperation;
   @NotNull
   final MythicRecipeBlueprint operation;
   int times;

   public void unload() {
      results.remove(this.operationIdentifier);
   }

   @NotNull
   public UUID getOperationIdentifier() {
      return this.operationIdentifier;
   }

   @NotNull
   public MythicBlueprintInventory getResultOfOperation() {
      return this.resultOfOperation;
   }

   @NotNull
   public MythicRecipeBlueprint getOperation() {
      return this.operation;
   }

   @Nullable
   public static MythicCachedResult get(@Nullable ItemStack item) {
      if (item == null) {
         return null;
      }

      NBTItem asNBT = NBTItem.get(item);
      ItemTag tag = ItemTag.getTagAtPath("MYTHICLIB_CRAFTING", asNBT, SupportedNBTTagValues.STRING);
      if (tag == null) {
         return null;
      }

      item.setAmount(0);
      UUID uuid = SilentNumbers.UUIDParse((String)tag.getValue());
      if (uuid == null) {
         return null;
      }

      MythicCachedResult ret = results.get(uuid);
      if (ret == null) {
         return null;
      }

      results.remove(uuid);
      return ret;
   }

   public int getTimes() {
      return this.times;
   }

   public void setTimes(int t) {
      this.times = t;
   }

   public MythicCachedResult(
      @NotNull UUID operationIdentifier,
      @NotNull MythicBlueprintInventory resultOfOperation,
      @NotNull MythicRecipeBlueprint operation,
      @NotNull MythicRecipeInventory resultInventoryDisplay,
      int times
   ) {
      this.operationIdentifier = operationIdentifier;
      this.resultOfOperation = resultOfOperation;
      this.operation = operation;
      this.times = times;
      results.put(operationIdentifier, this);

      for (int h = 0; h < resultInventoryDisplay.getHeight(); h++) {
         for (int w = 0; w < resultInventoryDisplay.getWidth(); w++) {
            ItemStack item = resultInventoryDisplay.getItemAt(w, -h);
            if (item != null) {
               NBTItem nbt = NBTItem.get(item);
               nbt.addTag(new ItemTag("MYTHICLIB_CRAFTING", operationIdentifier.toString()));
               resultInventoryDisplay.setItemAt(w, -h, nbt.toItem());
            }
         }
      }
   }
}
