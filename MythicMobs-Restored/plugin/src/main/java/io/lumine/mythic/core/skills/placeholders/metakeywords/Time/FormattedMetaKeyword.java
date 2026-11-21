package io.lumine.mythic.core.skills.placeholders.metakeywords.Time;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.variables.types.TimeVariable;
import java.time.Instant;

@MythicMetaKeyword(
   name = "formatted"
)
public class FormattedMetaKeyword extends AbstractMetaKeyword<Instant, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute VALUE = CommonAttributes.VALUE;

   public String extract(MetaKeywords.ExtractionHandler<Instant, String> handler) {
      return TimeVariable.getFormattedTime(handler.value().toEpochMilli(), handler.getString(VALUE));
   }
}
