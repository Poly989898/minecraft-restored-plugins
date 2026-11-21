package io.lumine.mythic.core.skills.placeholders.metakeywords.Double;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "abs"
)
public class AbsoluteMetaKeyword extends AbstractMetaKeyword<Double, Double> {
   public Double extract(MetaKeywords.ExtractionHandler<Double, Double> handler) {
      return Math.abs(handler.value());
   }
}
