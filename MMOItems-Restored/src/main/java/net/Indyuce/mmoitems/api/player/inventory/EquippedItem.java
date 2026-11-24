package net.Indyuce.mmoitems.api.player.inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.util.Lazy;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import org.bukkit.inventory.ItemStack;

public abstract class EquippedItem {
   private final NBTItem item;
   private final EquipmentSlot slot;
   private final Lazy<VolatileMMOItem> cached;

   public EquippedItem(ItemStack item, EquipmentSlot equipmentSlot) {
      this(NBTItem.get(item), equipmentSlot);
   }

   public EquippedItem(NBTItem nbtItem, EquipmentSlot equipmentSlot) {
      this.item = nbtItem;
      this.slot = equipmentSlot;
      this.cached = Lazy.of(() -> new VolatileMMOItem(nbtItem));
   }

   public VolatileMMOItem getCached() {
      return (VolatileMMOItem)this.cached.get();
   }

   public NBTItem getNBT() {
      return this.item;
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }

   public boolean isPlacementLegal() {
      String mMOITEMSITEMTYPE = this.item.getString("MMOITEMS_ITEM_TYPE");
      if (mMOITEMSITEMTYPE == null) {
         return false;
      }

      Type type = MMOItems.plugin.getTypes().get(mMOITEMSITEMTYPE);
      if (type == null) {
         return false;
      }

      ModifierSource modifierSource = type.getModifierSource();
      return EquipmentSlot.OFF_HAND.isCompatible(modifierSource, this.slot) || EquipmentSlot.MAIN_HAND.isCompatible(modifierSource, this.slot);
   }

   public abstract void setItem(@Nullable ItemStack item);
}
