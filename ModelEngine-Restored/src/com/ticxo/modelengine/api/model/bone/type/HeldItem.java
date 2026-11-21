package com.ticxo.modelengine.api.model.bone.type;

import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface HeldItem {
   ItemStack EMPTY = new ItemStack(Material.AIR);

   Vector3f getLocation();

   Quaternionf getRotation();

   ItemDisplayTransform getDisplay();

   void setDisplay(ItemDisplayTransform display);

   HeldItem.ItemStackSupplier getItemProvider();

   void setItemProvider(Supplier<ItemStack> stackSupplier);

   void setItemProvider(HeldItem.ItemStackSupplier stackSupplier);

   void clearItemProvider();

   ItemStack getItem();

   @AllArgsConstructor
   class EquipmentSupplier implements HeldItem.ItemStackSupplier {
      private LivingEntity target;
      private EquipmentSlot slot;

      public EquipmentSupplier() {
      }

      @Override
      public ItemStack supply() {
         if (this.target != null && this.slot != null) {
            EntityEquipment eq = this.target.getEquipment();
            return eq == null ? HeldItem.EMPTY : eq.getItem(this.slot);
         } else {
            return HeldItem.EMPTY;
         }
      }

      @Override
      public void save(SavedData data) {
         data.putString("type", "equipment");
         data.putUUID("target", this.target.getUniqueId());
         data.put("slot", this.slot.name());
      }

      @Override
      public void load(SavedData data) {
         UUID uuid = data.getUUID("target");
         String slotString = data.getString("slot");
         if (uuid != null && slotString != null) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
               try {
                  this.slot = EquipmentSlot.valueOf(slotString);
               } catch (Throwable throwable) {
                  TLogger.error(1, "Failed to load EquipmentSupplier: Invalid slot " + slotString + ".");
                  return;
               }

               this.target = livingEntity;
            } else {
               TLogger.error(1, "Failed to load EquipmentSupplier: Target entity does not exist or is not LivingEntity.");
            }
         }
      }
   }

   interface ItemStackSupplier extends DataIO {
      ItemStack supply();
   }

   @AllArgsConstructor
   class StaticItemStackSupplier implements HeldItem.ItemStackSupplier {
      private ItemStack itemStack;

      public StaticItemStackSupplier() {
      }

      @Override
      public ItemStack supply() {
         return this.itemStack;
      }

      @Override
      public void save(SavedData data) {
         data.putString("type", "static");
         data.putItemStack("item", this.itemStack);
      }

      @Override
      public void load(SavedData data) {
         this.itemStack = data.getItemStack("item", HeldItem.EMPTY);
      }
   }

   @AllArgsConstructor
   class TemporaryItemStackSupplier implements HeldItem.ItemStackSupplier {
      private final Supplier<ItemStack> stackSupplier;

      @Override
      public ItemStack supply() {
         return this.stackSupplier.get();
      }

      @Override
      public void save(SavedData data) {
      }

      @Override
      public void load(SavedData data) {
      }
   }
}
