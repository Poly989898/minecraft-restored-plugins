package io.lumine.mythic.core.skills.placeholders.metakeywords.Integer;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "sub"
)
public class SubMetaKeyword extends AbstractMetaKeyword<Integer, Integer> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = CommonAttributes.AMOUNT;

   public Integer extract(MetaKeywords.ExtractionHandler<Integer, Integer> handler) {
      return handler.value() - handler.getInt(AMOUNT);
   }
}
