package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.List;

@MythicMetaKeyword(
   name = "size"
)
public class SizeMetaKeyword extends AbstractMetaKeyword<List<String>, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<List<String>, Integer> handler) {
      return handler.value().size();
   }
}
