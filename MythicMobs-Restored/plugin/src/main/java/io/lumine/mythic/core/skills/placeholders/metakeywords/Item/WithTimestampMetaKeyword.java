package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "withTimestamp"
)
public class WithTimestampMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute TIMESTAMP = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("TIMESTAMP")
      .defaultValue("0")
      .names(new String[]{"timestamp", "time"})
      .build();

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      return handler.value().timestamp(handler.getObject(TIMESTAMP, PolymorphicPlaceholder::getLong));
   }
}
