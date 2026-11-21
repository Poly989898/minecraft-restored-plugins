package io.lumine.mythic.core.skills.placeholders.metakeywords.Long;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "clamp"
)
public class ClampMetaKeyword extends AbstractMetaKeyword<Long, Long> {
   @MythicMetaKeywordAttribute
   public static final MetaKeywords.MetaKeywordsAttribute MIN = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("MIN")
      .names(new String[]{"min"})
      .defaultValue(String.valueOf(Long.MIN_VALUE))
      .build();
   @MythicMetaKeywordAttribute
   public static final MetaKeywords.MetaKeywordsAttribute MAX = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("MAX")
      .names(new String[]{"max"})
      .defaultValue(String.valueOf(Long.MAX_VALUE))
      .build();

   public Long extract(MetaKeywords.ExtractionHandler<Long, Long> handler) {
      return Math.min(
         Math.max(handler.value(), handler.getObject(MIN, PolymorphicPlaceholder::getLong)), handler.getObject(MAX, PolymorphicPlaceholder::getLong)
      );
   }
}
