package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "replace"
)
public class ReplaceMetaKeyword extends AbstractMetaKeyword<String, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute FROM = CommonAttributes.FROM;
   @MythicMetaKeywordAttribute(
      index = 2
   )
   public static final MetaKeywords.MetaKeywordsAttribute TO = CommonAttributes.TO;

   public String extract(MetaKeywords.ExtractionHandler<String, String> handler) {
      return handler.value().replace(handler.getString(FROM), handler.getString(TO));
   }
}
