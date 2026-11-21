package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "withMythicType"
)
public class WithMythicTypeMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute TYPE = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("TYPE")
      .defaultValue("type")
      .names(new String[]{"type"})
      .build();

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      handler.value().mythicItemType(handler.getString(TYPE));
      return handler.value();
   }
}
