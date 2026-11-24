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
   public StoredTagsData(ItemStack item) {
      this(NBTItem.get(item));
   }

   public StoredTagsData(List<ItemTag> itemTags) {
      this.tags.addAll(itemTags);
   }

   public StoredTagsData(NBTItem nbtItem) {
      for (String tag : nbtItem.getTags()) {
         if ((!tag.startsWith("MMOITEMS_") || SAVED_MMOITEMS_TAGS.contains(tag)) && !IGNORED_TAGS.contains(tag) && !tag.startsWith("HSTRY_")) {
            switch (this.getTagType(nbtItem.getTypeId(tag))) {
               case "double":
                  this.tags.add(new ItemTag(tag, nbtItem.getDouble(tag)));
                  break;
               case "int":
                  this.tags.add(new ItemTag(tag, nbtItem.getInteger(tag)));
                  break;
               case "byte":
                  this.tags.add(new ItemTag(tag, nbtItem.getBoolean(tag)));
                  break;
               case "string":
                  this.tags.add(new ItemTag(tag, nbtItem.getString(tag)));
            }
         }
      }
   }

   public void addTag(ItemTag itemTag) {
      this.tags.add(itemTag);
   }

   public List<ItemTag> getTags() {
      return this.tags;
   }

   private String getTagType(int count) {
      switch (count) {
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

   public void mergeWith(StoredTagsData storedTagsData) {
      this.tags.addAll(storedTagsData.tags);
   }

   public StoredTagsData clone() {
      return new StoredTagsData(this.getTags());
   }

   @Override
   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof StoredTagsData)) {
         return false;
      }

      if (((StoredTagsData)obj).getTags().size() != this.getTags().size()) {
         return false;
      }

      for (ItemTag tag : ((StoredTagsData)obj).getTags()) {
         if (tag != null) {
            boolean flag = true;

            for (ItemTag storedTag : this.getTags()) {
               if (tag.equals(storedTag)) {
                  flag = false;
                  break;
               }
            }

            if (flag) {
               return false;
            }
         }
      }

      return true;
   }
}
