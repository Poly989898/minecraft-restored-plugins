package io.lumine.mythic.lib.api.itemtype;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Objects;
import org.bukkit.inventory.ItemStack;

public class MMOItemType implements ItemType {
   private final String type;
   private final String id;

   public MMOItemType(String type, String id) {
      this.type = Objects.requireNonNull(UtilityMethods.enumName(type), "Type cannot be null");
      this.id = Objects.requireNonNull(UtilityMethods.enumName(id), "ID cannot be null");
   }

   @Override
   public boolean matches(ItemStack stack) {
      NBTItem nbt = NBTItem.get(stack);
      return nbt.getString("MMOITEMS_ITEM_TYPE").equalsIgnoreCase(this.type) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(this.id);
   }

   @Override
   public String display() {
      return this.type + "." + this.id;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.id);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MMOItemType that = (MMOItemType)o;
         return this.type.equals(that.type) && this.id.equals(that.id);
      } else {
         return false;
      }
   }
}
