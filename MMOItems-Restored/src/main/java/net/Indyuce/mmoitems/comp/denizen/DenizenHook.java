package net.Indyuce.mmoitems.comp.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.TagManager;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;

public class DenizenHook {
   public DenizenHook() {
      ObjectFetcher.registerWithObjectFetcher(MMOItemTemplateTag.class, MMOItemTemplateTag.tagProcessor);
      PropertyParser.registerProperty(MMOItemsItemProperty.class, ItemTag.class);
      TagManager.registerTagHandler("mmoitem_template", attribute -> {
         if (!attribute.hasContext(1)) {
            attribute.echoError("Please provide an item type and ID.");
            return null;
         }

         MapTag mapTag = (MapTag)attribute.contextAsType(1, MapTag.class);
         if (mapTag == null) {
            attribute.echoError("Invalid MapTag input");
            return null;
         }

         ObjectTag type = mapTag.getObject("type");
         ObjectTag id = mapTag.getObject("id");
         if (type != null && id != null) {
            String text = type.toString().replace("-", "_").toUpperCase();
            Type type2 = MMOItems.plugin.getTypes().get(text);
            if (type2 == null) {
               attribute.echoError("Invalid type - cannot find type with name '" + text + "'");
               return null;
            } else {
               String text2 = id.toString().replace("-", "_").toUpperCase();
               if (!MMOItems.plugin.getTemplates().hasTemplate(type2, text2)) {
                  attribute.echoError("Invalid template ID - cannot find template with name '" + text2 + "'");
                  return null;
               } else {
                  return new MMOItemTemplateTag(type2, text2);
               }
            }
         } else {
            attribute.echoError("Invalid MapTag input - missing 'type' or 'id'");
            return null;
         }
      });
   }
}
