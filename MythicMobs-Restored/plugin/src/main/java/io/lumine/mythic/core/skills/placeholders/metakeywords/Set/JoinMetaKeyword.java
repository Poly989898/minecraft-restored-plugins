package io.lumine.mythic.core.skills.placeholders.metakeywords.Set;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.Set;

@MythicMetaKeyword(
   name = "join"
)
public class JoinMetaKeyword extends AbstractMetaKeyword<Set<String>, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute WITH = CommonAttributes.WITH.toBuilder().defaultValue(",").index(1).build();

   public String extract(MetaKeywords.ExtractionHandler<Set<String>, String> handler) {
      return String.join(handler.getString(WITH), handler.value());
   }
}
