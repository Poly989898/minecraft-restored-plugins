package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.List;

@MythicMetaKeyword(
   name = "first"
)
public class FirstMetaKeyword extends AbstractMetaKeyword<List<String>, String> {
   public String extract(MetaKeywords.ExtractionHandler<List<String>, String> handler) {
      return handler.value().getFirst();
   }
}
