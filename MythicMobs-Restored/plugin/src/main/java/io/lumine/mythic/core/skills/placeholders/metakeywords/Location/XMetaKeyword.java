package io.lumine.mythic.core.skills.placeholders.metakeywords.Location;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "x"
)
public class XMetaKeyword extends AbstractMetaKeyword<AbstractLocation, Double> {
   public Double extract(MetaKeywords.ExtractionHandler<AbstractLocation, Double> handler) {
      return handler.value().getX();
   }
}
