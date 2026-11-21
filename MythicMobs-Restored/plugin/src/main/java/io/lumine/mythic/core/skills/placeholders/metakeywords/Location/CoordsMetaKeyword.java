package io.lumine.mythic.core.skills.placeholders.metakeywords.Location;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.variables.types.ListVariable;
import java.util.List;

@MythicMetaKeyword(
   name = "coords"
)
public class CoordsMetaKeyword extends AbstractMetaKeyword<AbstractLocation, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<AbstractLocation, List<String>> handler) {
      return ListVariable.fromString(handler.value().getX() + "," + handler.value().getY() + "," + handler.value().getZ());
   }
}
