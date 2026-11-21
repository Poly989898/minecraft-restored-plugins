package io.lumine.mythic.core.skills.placeholders.metakeywords.Float;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "round"
)
public class RoundMetaKeyword extends AbstractMetaKeyword<Float, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<Float, Integer> handler) {
      return Math.round(handler.value());
   }
}
