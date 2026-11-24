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

   public abstract Object get(String text);

   public abstract String getString(String text);

   public abstract boolean hasTag(String text);

   public abstract boolean getBoolean(String text);

   public abstract double getDouble(String text);

   public abstract int getInteger(String text);

   public abstract NBTCompound getNBTCompound(String text);

   public abstract NBTItem addTag(List<ItemTag> itemTags);

   public abstract NBTItem removeTag(String... texts);

   public abstract Set<String> getTags();

   public abstract ItemStack toItem();

   public abstract int getTypeId(String text);

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
