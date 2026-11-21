package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.List;

@MythicMetaKeyword(
   name = "get"
)
public class GetMetaKeyword extends AbstractMetaKeyword<List<String>, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute INDEX = CommonAttributes.INDEX;

   public String extract(MetaKeywords.ExtractionHandler<List<String>, String> handler) {
      return handler.value().get(handler.getInt(INDEX));
   }
}
