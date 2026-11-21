package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.List;

@MythicMetaKeyword(
   name = "sliceFrom"
)
public class SliceFromMetaKeyword extends AbstractMetaKeyword<List<String>, List<String>> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute FROM = CommonAttributes.FROM;

   public List<String> extract(MetaKeywords.ExtractionHandler<List<String>, List<String>> handler) {
      return handler.value().subList(handler.getInt(FROM), handler.value().size());
   }
}
