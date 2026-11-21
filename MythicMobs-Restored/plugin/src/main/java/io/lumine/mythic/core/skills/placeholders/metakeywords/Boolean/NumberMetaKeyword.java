package io.lumine.mythic.core.skills.placeholders.metakeywords.Boolean;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Collection;
import java.util.List;

@MythicMetaKeyword(
   name = "number"
)
public class NumberMetaKeyword extends AbstractMetaKeyword<Boolean, Integer> {
   @Override
   public Collection<MetaKeywords.MetaKeywordsAttribute> getAttributes() {
      return List.of();
   }

   public Integer extract(MetaKeywords.ExtractionHandler<Boolean, Integer> handler) {
      return handler.value() ? 1 : 0;
   }
}
