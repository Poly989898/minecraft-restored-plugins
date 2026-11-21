package io.lumine.mythic.core.skills.placeholders.metakeywords.Vector;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.placeholders.serialization.serializers.VectorSerializer;

@MythicMetaKeyword(
   name = "rotate"
)
public class RotateMetaKeyword extends AbstractMetaKeyword<AbstractVector, AbstractVector> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute AXIS = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("AXIS")
      .names(new String[]{"axis"})
      .defaultValue("0,0,0")
      .build();
   @MythicMetaKeywordAttribute(
      index = 2
   )
   public static final MetaKeywords.MetaKeywordsAttribute ANGLE = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("ANGLE")
      .names(new String[]{"angle"})
      .defaultValue("0")
      .build();

   public AbstractVector extract(MetaKeywords.ExtractionHandler<AbstractVector, AbstractVector> handler) {
      return handler.value()
         .clone()
         .rotateAroundAxis(VectorSerializer.toVector(handler.getString(AXIS)), handler.getObject(ANGLE, PolymorphicPlaceholder::getDouble));
   }
}
