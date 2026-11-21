package io.lumine.mythic.core.skills.placeholders.metakeywords.Time;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.variables.types.TimeVariable;
import java.time.Instant;

@MythicMetaKeyword(
   name = "duration"
)
public class DurationMetaKeyword extends AbstractMetaKeyword<Instant, String> {
   public String extract(MetaKeywords.ExtractionHandler<Instant, String> handler) {
      return TimeVariable.getFormattedDuration(handler.value().toEpochMilli());
   }
}
