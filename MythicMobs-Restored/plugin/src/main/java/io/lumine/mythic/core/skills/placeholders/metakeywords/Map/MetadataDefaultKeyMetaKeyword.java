package io.lumine.mythic.core.skills.placeholders.metakeywords.Map;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.variables.types.MapVariable;
import java.util.Map;

@MythicMetaKeyword(
   name = "default"
)
public class MetadataDefaultKeyMetaKeyword extends AbstractMetaKeyword<Map<String, String>, String> {
   public String extract(MetaKeywords.ExtractionHandler<Map<String, String>, String> handler) {
      return MapVariable.getWithPipeDefault(handler.value(), handler.context().getString(0));
   }
}
