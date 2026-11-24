package net.Indyuce.mmoitems.api.item.mmoitem;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class ReadMMOItem extends MMOItem {
   @NotNull
   private final NBTItem item;

   public ReadMMOItem(@NotNull NBTItem nbtItem) {
      super(Type.get(nbtItem.getType()), nbtItem.getString("MMOITEMS_ITEM_ID"));
      this.item = nbtItem;
   }

   @Deprecated
   @Override
   public int getDamage() {
      if (this.hasData(ItemStats.MAX_DURABILITY)) {
         return this.getNBT().hasTag("MMOITEMS_DURABILITY")
            ? this.getNBT().getInteger("MMOITEMS_MAX_DURABILITY") - this.getNBT().getInteger("MMOITEMS_DURABILITY")
            : 0;
      }

      ItemStack item = this.getNBT().getItem();
      if (item.hasItemMeta()) {
         ItemMeta itemMeta = item.getItemMeta();
         if (itemMeta instanceof Damageable) {
            return ((Damageable)itemMeta).getDamage();
         }
      }

      return 0;
   }

   @NotNull
   public NBTItem getNBT() {
      return this.item;
   }

   @Override
   public String getCooldownPath() {
      return MMOUtils.requireNonEmptyElse(this.item.getString("MMOITEMS_COOLDOWN_REFERENCE"), super.getCooldownPath());
   }
}
