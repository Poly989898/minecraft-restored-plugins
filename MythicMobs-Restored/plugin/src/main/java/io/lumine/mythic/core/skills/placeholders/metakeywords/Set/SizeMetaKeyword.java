package io.lumine.mythic.core.skills.placeholders.metakeywords.Set;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Set;

@MythicMetaKeyword(
   name = "size"
)
public class SizeMetaKeyword extends AbstractMetaKeyword<Set<String>, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<Set<String>, Integer> handler) {
      return handler.value().size();
   }
}
