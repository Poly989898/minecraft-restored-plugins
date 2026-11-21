package io.lumine.mythic.core.skills.placeholders.metakeywords.Double;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "min"
)
public class MinMetaKeyword extends AbstractMetaKeyword<Double, Double> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = CommonAttributes.AMOUNT;

   public Double extract(MetaKeywords.ExtractionHandler<Double, Double> handler) {
      return Math.max(handler.value(), handler.getObject(AMOUNT, PolymorphicPlaceholder::getDouble));
   }
}
