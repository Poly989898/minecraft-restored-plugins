package io.lumine.mythic.core.skills.placeholders.metakeywords.Long;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "sub"
)
public class SubtractMetaKeyword extends AbstractMetaKeyword<Long, Long> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = CommonAttributes.AMOUNT;

   public Long extract(MetaKeywords.ExtractionHandler<Long, Long> handler) {
      return handler.value() - handler.getObject(AMOUNT, PolymorphicPlaceholder::getLong);
   }
}
