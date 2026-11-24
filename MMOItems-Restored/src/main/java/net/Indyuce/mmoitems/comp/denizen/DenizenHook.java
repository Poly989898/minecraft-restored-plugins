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
      TagManager.registerTagHandler("mmoitem_template", var0 -> {
         if (!var0.hasContext(1)) {
            var0.echoError("Please provide an item type and ID.");
            return null;
         }

         MapTag var1 = (MapTag)var0.contextAsType(1, MapTag.class);
         if (var1 == null) {
            var0.echoError("Invalid MapTag input");
            return null;
         }

         ObjectTag var2 = var1.getObject("type");
         ObjectTag var3 = var1.getObject("id");
         if (var2 != null && var3 != null) {
            String var4 = var2.toString().replace("-", "_").toUpperCase();
            Type var5 = MMOItems.plugin.getTypes().get(var4);
            if (var5 == null) {
               var0.echoError("Invalid type - cannot find type with name '" + var4 + "'");
               return null;
            } else {
               String var6 = var3.toString().replace("-", "_").toUpperCase();
               if (!MMOItems.plugin.getTemplates().hasTemplate(var5, var6)) {
                  var0.echoError("Invalid template ID - cannot find template with name '" + var6 + "'");
                  return null;
               } else {
                  return new MMOItemTemplateTag(var5, var6);
               }
            }
         } else {
            var0.echoError("Invalid MapTag input - missing 'type' or 'id'");
            return null;
         }
      });
   }
}
