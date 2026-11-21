package io.lumine.mythic.core.skills.placeholders.metakeywords.Map;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.variables.types.MapVariable;
import java.util.Map;

@MythicMetaKeyword(
   name = "get"
)
public class GetMetaKeyword extends AbstractMetaKeyword<Map<String, String>, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute KEY = CommonAttributes.KEY;

   public String extract(MetaKeywords.ExtractionHandler<Map<String, String>, String> handler) {
      return MapVariable.getWithPipeDefault(handler.value(), handler.getString(KEY));
   }
}
