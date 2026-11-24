package net.Indyuce.mmoitems.comp.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;

@Deprecated
public class MMOItemTag extends SimpleTag {
   private final ItemTag item;
   public static ObjectTagProcessor<MMOItemTag> tagProcessor = new ObjectTagProcessor();

   @Deprecated
   public MMOItemTag(ItemTag var1) {
      this.item = var1;
   }

   public boolean isUnique() {
      return true;
   }

   public String getObjectType() {
      return "MMOItem";
   }

   public String identify() {
      return "mmoitem@" + this.item.identify();
   }

   public String identifySimple() {
      return this.identify();
   }

   public ObjectTag getObjectAttribute(Attribute var1) {
      return tagProcessor.getObjectAttribute(this, var1);
   }

   public static void registerTags() {
      tagProcessor.registerTag("item", (var0, var1) -> var1.item, new String[0]);
   }
}
