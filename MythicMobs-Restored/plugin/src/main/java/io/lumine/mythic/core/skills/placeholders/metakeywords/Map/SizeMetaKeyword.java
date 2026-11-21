package io.lumine.mythic.core.skills.placeholders.metakeywords.Map;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Map;

@MythicMetaKeyword(
   name = "size"
)
public class SizeMetaKeyword extends AbstractMetaKeyword<Map<String, String>, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<Map<String, String>, Integer> handler) {
      return handler.value().size();
   }
}
