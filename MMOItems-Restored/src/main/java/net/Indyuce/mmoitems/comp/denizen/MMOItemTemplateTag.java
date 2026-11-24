package net.Indyuce.mmoitems.comp.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import java.util.Random;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class MMOItemTemplateTag extends SimpleTag {
   private final Type type;
   private final String id;
   private static final Random random = new Random();
   public static ObjectTagProcessor<MMOItemTemplateTag> tagProcessor = new ObjectTagProcessor();

   public MMOItemTemplateTag(Type var1, String var2) {
      this.type = var1;
      this.id = var2;
   }

   public MMOItemTemplate getTemplate() {
      return MMOItems.plugin.getTemplates().getTemplate(this.type, this.id);
   }

   public boolean isUnique() {
      return true;
   }

   public String getObjectType() {
      return "MMOItemTemplate";
   }

   public String identify() {
      return "mmoitem_template@" + this.type.getId() + "." + this.id;
   }

   public String identifySimple() {
      return this.identify();
   }

   public ObjectTag getObjectAttribute(Attribute var1) {
      return tagProcessor.getObjectAttribute(this, var1);
   }

   public static void registerTags() {
      tagProcessor.registerTag("item_type", (var0, var1) -> new ElementTag(var1.type.getName()), new String[0]);
      tagProcessor.registerTag("item_id", (var0, var1) -> new ElementTag(var1.id), new String[0]);
      tagProcessor.registerTag(
         "generate",
         (var0, var1) -> {
            if (!var0.hasContext(1)) {
               return new ItemTag(var1.getTemplate().newBuilder().build().newBuilder().build());
            }

            MapTag var2 = (MapTag)var0.contextAsType(1, MapTag.class);
            if (var2 == null) {
               var0.echoError("Invalid MapTag input");
               return null;
            }

            ObjectTag var3 = var2.getObject("player");
            if (var3 != null && !(var3 instanceof PlayerTag)) {
               var0.echoError("Bad player input type");
               return null;
            }

            ObjectTag var4 = var2.getObject("level");
            int var5 = -1;
            if (var4 != null) {
               try {
                  var5 = Integer.parseInt(var4.toString());
               } catch (IllegalArgumentException var13) {
                  var0.echoError("Bad level input: " + var4 + " is not a valid integer");
                  return null;
               }
            }

            ObjectTag var6 = var2.getObject("match-level");
            boolean var7 = var6 != null && Boolean.parseBoolean(var6.toString());
            ObjectTag var8 = var2.getObject("tier");
            ItemTier var9 = null;
            if (var8 != null) {
               try {
                  var9 = MMOItems.plugin.getTiers().getOrThrow(var8.toString().toUpperCase().replace("-", "_"));
               } catch (IllegalArgumentException var12) {
                  var0.echoError(var12.getMessage());
               }
            }

            int var10 = var5 >= 0
               ? var5
               : (
                  var7 && var3 != null
                     ? MMOItems.plugin.getTemplates().rollLevel(PlayerData.get(((PlayerTag)var3).getPlayerEntity()).getRPG().getLevel())
                     : 1 + random.nextInt(100)
               );
            ItemTier var11 = var9 != null ? var9 : MMOItems.plugin.getTemplates().rollTier();
            return new ItemTag(var1.getTemplate().newBuilder(var10, var11).build().newBuilder().build());
         },
         new String[0]
      );
   }

   public static MMOItemTemplateTag valueOf(String var0, TagContext var1) {
      if (var0 == null) {
         return null;
      }

      try {
         String[] var2 = var0.substring("mmoitem_template@".length()).split("\\.");
         String var3 = var2[0];
         String var4 = var2[1];
         Type var5 = MMOItems.plugin.getTypes().getOrThrow(var3);
         MMOItems.plugin.getTemplates().getTemplateOrThrow(var5, var4);
         return new MMOItemTemplateTag(var5, var4);
      } catch (Exception var6) {
         return null;
      }
   }
}
