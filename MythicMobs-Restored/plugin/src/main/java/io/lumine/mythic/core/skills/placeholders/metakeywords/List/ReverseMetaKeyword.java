package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.List;

@MythicMetaKeyword(
   name = "reverse"
)
public class ReverseMetaKeyword extends AbstractMetaKeyword<List<String>, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<List<String>, List<String>> handler) {
      return handler.value().reversed();
   }
}
