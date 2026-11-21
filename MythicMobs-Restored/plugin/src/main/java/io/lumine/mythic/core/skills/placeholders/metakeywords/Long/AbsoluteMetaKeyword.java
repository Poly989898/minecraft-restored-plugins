package io.lumine.mythic.core.skills.placeholders.metakeywords.Long;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "abs"
)
public class AbsoluteMetaKeyword extends AbstractMetaKeyword<Long, Long> {
   public Long extract(MetaKeywords.ExtractionHandler<Long, Long> handler) {
      return Math.abs(handler.value());
   }
}
