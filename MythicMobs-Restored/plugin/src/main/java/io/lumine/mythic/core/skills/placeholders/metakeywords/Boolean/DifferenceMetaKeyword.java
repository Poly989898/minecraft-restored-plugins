package io.lumine.mythic.core.skills.placeholders.metakeywords.Boolean;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "difference"
)
public class DifferenceMetaKeyword extends AbstractMetaKeyword<Boolean, Boolean> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute WITH = CommonAttributes.WITH;

   public Boolean extract(MetaKeywords.ExtractionHandler<Boolean, Boolean> handler) {
      return handler.value() && !handler.getObject(WITH, PolymorphicPlaceholder::getBoolean);
   }
}
