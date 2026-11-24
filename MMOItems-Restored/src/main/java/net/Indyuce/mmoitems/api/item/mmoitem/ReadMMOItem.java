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

   public ReadMMOItem(@NotNull NBTItem var1) {
      super(Type.get(var1.getType()), var1.getString("MMOITEMS_ITEM_ID"));
      this.item = var1;
   }

   @Deprecated
   @Override
   public int getDamage() {
      if (this.hasData(ItemStats.MAX_DURABILITY)) {
         return this.getNBT().hasTag("MMOITEMS_DURABILITY")
            ? this.getNBT().getInteger("MMOITEMS_MAX_DURABILITY") - this.getNBT().getInteger("MMOITEMS_DURABILITY")
            : 0;
      }

      ItemStack var1 = this.getNBT().getItem();
      if (var1.hasItemMeta()) {
         ItemMeta var2 = var1.getItemMeta();
         if (var2 instanceof Damageable) {
            return ((Damageable)var2).getDamage();
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
