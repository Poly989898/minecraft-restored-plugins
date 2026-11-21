package io.lumine.mythic.core.skills.placeholders.metakeywords.Boolean;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Collection;
import java.util.List;

@MythicMetaKeyword(
   name = "inverse"
)
public class InverseMetaKeyword extends AbstractMetaKeyword<Boolean, Boolean> {
   @Override
   public Collection<MetaKeywords.MetaKeywordsAttribute> getAttributes() {
      return List.of();
   }

   public Boolean extract(MetaKeywords.ExtractionHandler<Boolean, Boolean> handler) {
      return !handler.value();
   }
}
