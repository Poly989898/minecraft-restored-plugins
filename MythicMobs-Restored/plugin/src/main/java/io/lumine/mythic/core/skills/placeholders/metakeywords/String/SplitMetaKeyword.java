package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "split"
)
public class SplitMetaKeyword extends AbstractMetaKeyword<String, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute REGEX = CommonAttributes.REGEX;
   @MythicMetaKeywordAttribute(
      index = 2
   )
   public static final MetaKeywords.MetaKeywordsAttribute WITH = CommonAttributes.WITH.toBuilder().defaultValue(",").build();

   public String extract(MetaKeywords.ExtractionHandler<String, String> handler) {
      return String.join(handler.getString(WITH), handler.value().split(handler.getString(REGEX)));
   }
}
