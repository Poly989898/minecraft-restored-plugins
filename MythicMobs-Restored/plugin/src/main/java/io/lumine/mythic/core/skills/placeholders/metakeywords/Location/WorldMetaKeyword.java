package io.lumine.mythic.core.skills.placeholders.metakeywords.Location;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "world"
)
public class WorldMetaKeyword extends AbstractMetaKeyword<AbstractLocation, String> {
   public String extract(MetaKeywords.ExtractionHandler<AbstractLocation, String> handler) {
      return handler.value().getWorld().getName();
   }
}
