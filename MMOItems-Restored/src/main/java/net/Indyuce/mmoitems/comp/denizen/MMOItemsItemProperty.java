package net.Indyuce.mmoitems.comp.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.util.identify.IdentifiedItem;

public class MMOItemsItemProperty implements Property {
   private final ItemTag item;
   public static final String[] handledMechs = new String[0];

   public MMOItemsItemProperty(ItemTag itemTag) {
      this.item = itemTag;
   }

   public String getPropertyString() {
      return null;
   }

   public String getPropertyId() {
      return "MMOItemsItem";
   }

   public boolean isMMOItem() {
      return NBTItem.get(this.item.getItemStack()).hasTag("MMOITEMS_TYPE");
   }

   public MMOItemTag getMMOItem() {
      return new MMOItemTag(this.item);
   }

   public void adjust(Mechanism mechanism) {
   }

   public static boolean describes(ObjectTag objectTag) {
      return objectTag instanceof ItemTag;
   }

   public static MMOItemsItemProperty getFrom(ObjectTag objectTag) {
      return objectTag instanceof ItemTag ? new MMOItemsItemProperty((ItemTag)objectTag) : null;
   }

   public static void registerTags() {
      PropertyParser.<MMOItemsItemProperty>registerTag("is_mmoitem", (attribute, mmoItemsItemProperty) -> new ElementTag(mmoItemsItemProperty.isMMOItem()), new String[0]);
      PropertyParser.<MMOItemsItemProperty>registerTag(
         "is_unidentified_item", (attribute, mmoItemsItemProperty) -> new ElementTag(NBTItem.get(mmoItemsItemProperty.item.getItemStack()).hasTag("MMOITEMS_UNIDENTIFIED_ITEM")), new String[0]
      );
      PropertyParser.<MMOItemsItemProperty>registerTag(
         "identify", (attribute, mmoItemsItemProperty) -> new MMOItemTag(new ItemTag(new IdentifiedItem(NBTItem.get(mmoItemsItemProperty.item.getItemStack())).identify())), new String[0]
      );
   }
}
