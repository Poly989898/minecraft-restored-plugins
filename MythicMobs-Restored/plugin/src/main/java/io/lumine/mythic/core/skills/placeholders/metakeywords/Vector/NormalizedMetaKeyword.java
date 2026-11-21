package io.lumine.mythic.core.skills.placeholders.metakeywords.Vector;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "normalized"
)
public class NormalizedMetaKeyword extends AbstractMetaKeyword<AbstractVector, AbstractVector> {
   public AbstractVector extract(MetaKeywords.ExtractionHandler<AbstractVector, AbstractVector> handler) {
      return handler.value().normalize();
   }
}
