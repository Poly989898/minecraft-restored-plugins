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

   public EquippedItem(ItemStack var1, EquipmentSlot var2) {
      this(NBTItem.get(var1), var2);
   }

   public EquippedItem(NBTItem var1, EquipmentSlot var2) {
      this.item = var1;
      this.slot = var2;
      this.cached = Lazy.of(() -> new VolatileMMOItem(var1));
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
      String var1 = this.item.getString("MMOITEMS_ITEM_TYPE");
      if (var1 == null) {
         return false;
      }

      Type var2 = MMOItems.plugin.getTypes().get(var1);
      if (var2 == null) {
         return false;
      }

      ModifierSource var3 = var2.getModifierSource();
      return EquipmentSlot.OFF_HAND.isCompatible(var3, this.slot) || EquipmentSlot.MAIN_HAND.isCompatible(var3, this.slot);
   }

   public abstract void setItem(@Nullable ItemStack var1);
}
