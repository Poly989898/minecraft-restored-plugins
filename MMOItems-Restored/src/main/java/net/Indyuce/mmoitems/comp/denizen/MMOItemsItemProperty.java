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

   public MMOItemsItemProperty(ItemTag var1) {
      this.item = var1;
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

   public void adjust(Mechanism var1) {
   }

   public static boolean describes(ObjectTag var0) {
      return var0 instanceof ItemTag;
   }

   public static MMOItemsItemProperty getFrom(ObjectTag var0) {
      return var0 instanceof ItemTag ? new MMOItemsItemProperty((ItemTag)var0) : null;
   }

   public static void registerTags() {
      PropertyParser.<MMOItemsItemProperty>registerTag("is_mmoitem", (var0, var1) -> new ElementTag(var1.isMMOItem()), new String[0]);
      PropertyParser.<MMOItemsItemProperty>registerTag(
         "is_unidentified_item", (var0, var1) -> new ElementTag(NBTItem.get(var1.item.getItemStack()).hasTag("MMOITEMS_UNIDENTIFIED_ITEM")), new String[0]
      );
      PropertyParser.<MMOItemsItemProperty>registerTag(
         "identify", (var0, var1) -> new MMOItemTag(new ItemTag(new IdentifiedItem(NBTItem.get(var1.item.getItemStack())).identify())), new String[0]
      );
   }
}
