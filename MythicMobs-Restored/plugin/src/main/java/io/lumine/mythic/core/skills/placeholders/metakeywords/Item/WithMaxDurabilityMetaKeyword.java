package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "withMaxDurability"
)
public class WithMaxDurabilityMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute DURABILITY = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("DURABILITY")
      .defaultValue("1")
      .names(new String[]{"durability", "dur"})
      .build();

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      handler.value().maxDurability(handler.getInt(DURABILITY));
      return handler.value();
   }
}
