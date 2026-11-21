package io.lumine.mythic.core.skills.placeholders.metakeywords.Double;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.math.BigDecimal;
import java.math.RoundingMode;

@MythicMetaKeyword(
   name = "precision"
)
public class PrecisionMetaKeyword extends AbstractMetaKeyword<Double, Double> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = CommonAttributes.AMOUNT;

   public Double extract(MetaKeywords.ExtractionHandler<Double, Double> handler) {
      BigDecimal bigDecimal = new BigDecimal(handler.value());
      return bigDecimal.setScale(handler.getInt(AMOUNT), RoundingMode.HALF_UP).doubleValue();
   }
}
