package io.lumine.mythic.lib.api.item;

import io.lumine.mythic.lib.MythicLib;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class NBTItem {
   protected final ItemStack item;

   public NBTItem(ItemStack item) {
      this.item = item;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public abstract Object get(String var1);

   public abstract String getString(String var1);

   public abstract boolean hasTag(String var1);

   public abstract boolean getBoolean(String var1);

   public abstract double getDouble(String var1);

   public abstract int getInteger(String var1);

   public abstract NBTCompound getNBTCompound(String var1);

   public abstract NBTItem addTag(List<ItemTag> var1);

   public abstract NBTItem removeTag(String... var1);

   public abstract Set<String> getTags();

   public abstract ItemStack toItem();

   public abstract int getTypeId(String var1);

   public NBTItem addTag(ItemTag... tags) {
      return this.addTag(Arrays.asList(tags));
   }

   public double getStat(String stat) {
      return this.getDouble("MMOITEMS_" + stat);
   }

   public boolean hasType() {
      return this.hasTag("MMOITEMS_ITEM_TYPE");
   }

   @Deprecated
   public void setCanMine(Collection<Material> blocks) {
      throw new NotImplementedException("Not implemented in <1.21");
   }

   public String getType() {
      String tag = this.getString("MMOITEMS_ITEM_TYPE");
      return !tag.equals("") ? tag : null;
   }

   public static NBTItem get(ItemStack item) {
      return MythicLib.plugin.getVersion().getWrapper().getNBTItem(item);
   }
}
