package io.lumine.mythic.core.skills.placeholders.metakeywords.Integer;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "clamp"
)
public class ClampMetaKeyword extends AbstractMetaKeyword<Integer, Integer> {
   @MythicMetaKeywordAttribute
   public static final MetaKeywords.MetaKeywordsAttribute MIN = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("MIN")
      .names(new String[]{"min"})
      .defaultValue(String.valueOf(Integer.MIN_VALUE))
      .build();
   @MythicMetaKeywordAttribute
   public static final MetaKeywords.MetaKeywordsAttribute MAX = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("MAX")
      .names(new String[]{"max"})
      .defaultValue(String.valueOf(Integer.MAX_VALUE))
      .build();

   public Integer extract(MetaKeywords.ExtractionHandler<Integer, Integer> handler) {
      return Math.min(Math.max(handler.value(), handler.getInt(MIN)), handler.getInt(MAX));
   }
}
