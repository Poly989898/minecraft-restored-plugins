package io.lumine.mythic.core.skills.placeholders.metakeywords.Vector;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.placeholders.serialization.serializers.VectorSerializer;

@MythicMetaKeyword(
   name = "div"
)
public class DivMetaKeyword extends AbstractMetaKeyword<AbstractVector, AbstractVector> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AMOUNT = CommonAttributes.AMOUNT.toBuilder().defaultValue("1,1,1").build();

   public AbstractVector extract(MetaKeywords.ExtractionHandler<AbstractVector, AbstractVector> handler) {
      return handler.value().clone().divide(VectorSerializer.toVector(handler.getString(AMOUNT)));
   }
}
