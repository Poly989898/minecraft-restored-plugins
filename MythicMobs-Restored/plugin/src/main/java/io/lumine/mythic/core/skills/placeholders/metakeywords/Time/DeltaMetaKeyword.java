package io.lumine.mythic.core.skills.placeholders.metakeywords.Time;

import io.lumine.mythic.api.skills.placeholders.PlaceholderTime;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.variables.types.TimeVariable;
import java.time.Instant;

@MythicMetaKeyword(
   name = "delta"
)
public class DeltaMetaKeyword extends AbstractMetaKeyword<Instant, Long> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute VALUE = CommonAttributes.VALUE;

   public Long extract(MetaKeywords.ExtractionHandler<Instant, Long> handler) {
      return TimeVariable.getTimeDelta(handler.value(), PlaceholderTime.of(handler.getString(VALUE)).get());
   }
}
