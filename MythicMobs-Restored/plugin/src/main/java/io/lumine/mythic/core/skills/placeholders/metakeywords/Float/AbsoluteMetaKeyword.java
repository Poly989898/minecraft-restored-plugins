package io.lumine.mythic.core.skills.placeholders.metakeywords.Float;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "abs"
)
public class AbsoluteMetaKeyword extends AbstractMetaKeyword<Float, Float> {
   public Float extract(MetaKeywords.ExtractionHandler<Float, Float> handler) {
      return Math.abs(handler.value());
   }
}
