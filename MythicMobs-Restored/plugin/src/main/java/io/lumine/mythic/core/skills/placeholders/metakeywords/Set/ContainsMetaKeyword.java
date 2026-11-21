package io.lumine.mythic.core.skills.placeholders.metakeywords.Set;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.Set;

@MythicMetaKeyword(
   name = "contains"
)
public class ContainsMetaKeyword extends AbstractMetaKeyword<Set<String>, Boolean> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute WITH = CommonAttributes.WITH.toBuilder().index(1).build();

   public Boolean extract(MetaKeywords.ExtractionHandler<Set<String>, Boolean> handler) {
      return handler.value().contains(handler.getString(WITH));
   }
}
