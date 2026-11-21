package io.lumine.mythic.core.skills.placeholders.metakeywords.Double;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "round"
)
public class RoundMetaKeyword extends AbstractMetaKeyword<Double, Long> {
   public Long extract(MetaKeywords.ExtractionHandler<Double, Long> handler) {
      return Math.round(handler.value());
   }
}
