package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.inventory.ItemStack;

public class StoredTagsData implements StatData, Mergeable<StoredTagsData> {
   private final List<ItemTag> tags = new ArrayList<>();
   private static final List<String> IGNORED_TAGS = Arrays.asList(
      "Unbreakable", "BlockEntityTag", "display", "Enchantments", "HideFlags", "Damage", "AttributeModifiers", "SkullOwner", "CanDestroy", "PickupDelay", "Age"
   );
   @Deprecated
   private static final List<String> SAVED_MMOITEMS_TAGS = Arrays.asList("MMOITEMS_SKIN_ID", "MMOITEMS_SKIN_TYPE");

   @Deprecated
   public StoredTagsData(ItemStack var1) {
      this(NBTItem.get(var1));
   }

   public StoredTagsData(List<ItemTag> var1) {
      this.tags.addAll(var1);
   }

   public StoredTagsData(NBTItem var1) {
      for (String var3 : var1.getTags()) {
         if ((!var3.startsWith("MMOITEMS_") || SAVED_MMOITEMS_TAGS.contains(var3)) && !IGNORED_TAGS.contains(var3) && !var3.startsWith("HSTRY_")) {
            switch (this.getTagType(var1.getTypeId(var3))) {
               case "double":
                  this.tags.add(new ItemTag(var3, var1.getDouble(var3)));
                  break;
               case "int":
                  this.tags.add(new ItemTag(var3, var1.getInteger(var3)));
                  break;
               case "byte":
                  this.tags.add(new ItemTag(var3, var1.getBoolean(var3)));
                  break;
               case "string":
                  this.tags.add(new ItemTag(var3, var1.getString(var3)));
            }
         }
      }
   }

   public void addTag(ItemTag var1) {
      this.tags.add(var1);
   }

   public List<ItemTag> getTags() {
      return this.tags;
   }

   private String getTagType(int var1) {
      switch (var1) {
         case 0:
            return "end";
         case 1:
            return "byte";
         case 2:
            return "short";
         case 3:
            return "int";
         case 4:
            return "long";
         case 5:
            return "float";
         case 6:
            return "double";
         case 7:
            return "bytearray";
         case 8:
            return "string";
         case 9:
            return "list";
         case 10:
            return "compound";
         case 11:
            return "intarray";
         default:
            return "unknown";
      }
   }

   public void mergeWith(StoredTagsData var1) {
      this.tags.addAll(var1.tags);
   }

   public StoredTagsData clone() {
      return new StoredTagsData(this.getTags());
   }

   @Override
   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof StoredTagsData)) {
         return false;
      }

      if (((StoredTagsData)var1).getTags().size() != this.getTags().size()) {
         return false;
      }

      for (ItemTag var3 : ((StoredTagsData)var1).getTags()) {
         if (var3 != null) {
            boolean var4 = true;

            for (ItemTag var6 : this.getTags()) {
               if (var3.equals(var6)) {
                  var4 = false;
                  break;
               }
            }

            if (var4) {
               return false;
            }
         }
      }

      return true;
   }
}
