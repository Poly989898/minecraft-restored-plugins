package io.lumine.mythic.core.skills.placeholders.metakeywords.Vector;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "z"
)
public class ZMetaKeyword extends AbstractMetaKeyword<AbstractVector, Double> {
   public Double extract(MetaKeywords.ExtractionHandler<AbstractVector, Double> handler) {
      return handler.value().getZ();
   }
}
