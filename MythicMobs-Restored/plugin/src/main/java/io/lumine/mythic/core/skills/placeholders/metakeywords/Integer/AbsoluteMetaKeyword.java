package io.lumine.mythic.core.skills.placeholders.metakeywords.Integer;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "abs"
)
public class AbsoluteMetaKeyword extends AbstractMetaKeyword<Integer, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<Integer, Integer> handler) {
      return Math.abs(handler.value());
   }
}
