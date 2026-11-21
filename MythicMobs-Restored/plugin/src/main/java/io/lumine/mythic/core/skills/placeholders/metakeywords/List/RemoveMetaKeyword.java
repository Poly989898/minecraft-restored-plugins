package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.List;

@MythicMetaKeyword(
   name = "remove"
)
public class RemoveMetaKeyword extends AbstractMetaKeyword<List<String>, List<String>> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute INDEX = CommonAttributes.INDEX;

   public List<String> extract(MetaKeywords.ExtractionHandler<List<String>, List<String>> handler) {
      handler.value().remove(handler.getInt(INDEX));
      return handler.value();
   }
}
