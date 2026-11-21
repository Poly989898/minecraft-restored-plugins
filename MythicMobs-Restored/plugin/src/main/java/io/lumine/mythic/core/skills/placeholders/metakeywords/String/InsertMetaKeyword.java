package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "insert"
)
public class InsertMetaKeyword extends AbstractMetaKeyword<String, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute INDEX = CommonAttributes.INDEX;
   @MythicMetaKeywordAttribute(
      index = 2
   )
   public static final MetaKeywords.MetaKeywordsAttribute VALUE = CommonAttributes.VALUE;

   public String extract(MetaKeywords.ExtractionHandler<String, String> handler) {
      int idx = handler.getInt(INDEX);
      return handler.value().substring(0, idx) + handler.getString(VALUE) + handler.value().substring(idx);
   }
}
