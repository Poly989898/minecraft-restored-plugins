package io.lumine.mythic.core.skills.placeholders.metakeywords.Float;

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
public class PrecisionMetaKeyword extends AbstractMetaKeyword<Float, Float> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = CommonAttributes.AMOUNT;

   public Float extract(MetaKeywords.ExtractionHandler<Float, Float> handler) {
      BigDecimal bigDecimal = new BigDecimal((double)handler.value().floatValue());
      return bigDecimal.setScale(handler.getInt(AMOUNT), RoundingMode.HALF_UP).floatValue();
   }
}
