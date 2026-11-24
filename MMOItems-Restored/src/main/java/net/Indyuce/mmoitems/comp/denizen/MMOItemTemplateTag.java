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

   public MMOItemTemplateTag(Type type, String text) {
      this.type = type;
      this.id = text;
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

   public ObjectTag getObjectAttribute(Attribute attribute) {
      return tagProcessor.getObjectAttribute(this, attribute);
   }

   public static void registerTags() {
      tagProcessor.registerTag("item_type", (attribute, mmoItemTemplateTag) -> new ElementTag(mmoItemTemplateTag.type.getName()), new String[0]);
      tagProcessor.registerTag("item_id", (attribute, mmoItemTemplateTag) -> new ElementTag(mmoItemTemplateTag.id), new String[0]);
      tagProcessor.registerTag(
         "generate",
         (attribute, mmoItemTemplateTag) -> {
            if (!attribute.hasContext(1)) {
               return new ItemTag(mmoItemTemplateTag.getTemplate().newBuilder().build().newBuilder().build());
            }

            MapTag mapTag = (MapTag)attribute.contextAsType(1, MapTag.class);
            if (mapTag == null) {
               attribute.echoError("Invalid MapTag input");
               return null;
            }

            ObjectTag player = mapTag.getObject("player");
            if (player != null && !(player instanceof PlayerTag)) {
               attribute.echoError("Bad player input type");
               return null;
            }

            ObjectTag level = mapTag.getObject("level");
            int count = -1;
            if (level != null) {
               try {
                  count = Integer.parseInt(level.toString());
               } catch (IllegalArgumentException exception) {
                  attribute.echoError("Bad level input: " + level + " is not a valid integer");
                  return null;
               }
            }

            ObjectTag matchLevel = mapTag.getObject("match-level");
            boolean flag = matchLevel != null && Boolean.parseBoolean(matchLevel.toString());
            ObjectTag tier = mapTag.getObject("tier");
            ItemTier itemTier = null;
            if (tier != null) {
               try {
                  itemTier = MMOItems.plugin.getTiers().getOrThrow(tier.toString().toUpperCase().replace("-", "_"));
               } catch (IllegalArgumentException exception) {
                  attribute.echoError(exception.getMessage());
               }
            }

            int count2 = count >= 0
               ? count
               : (
                  flag && player != null
                     ? MMOItems.plugin.getTemplates().rollLevel(PlayerData.get(((PlayerTag)player).getPlayerEntity()).getRPG().getLevel())
                     : 1 + random.nextInt(100)
               );
            ItemTier itemTier2 = itemTier != null ? itemTier : MMOItems.plugin.getTemplates().rollTier();
            return new ItemTag(mmoItemTemplateTag.getTemplate().newBuilder(count2, itemTier2).build().newBuilder().build());
         },
         new String[0]
      );
   }

   public static MMOItemTemplateTag valueOf(String text, TagContext tagContext) {
      if (text == null) {
         return null;
      }

      try {
         String[] texts = text.substring("mmoitem_template@".length()).split("\\.");
         String text2 = texts[0];
         String text3 = texts[1];
         Type type = MMOItems.plugin.getTypes().getOrThrow(text2);
         MMOItems.plugin.getTemplates().getTemplateOrThrow(type, text3);
         return new MMOItemTemplateTag(type, text3);
      } catch (Exception exception) {
         return null;
      }
   }
}
