package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "size"
)
public class SizeMetaKeyword extends AbstractMetaKeyword<String, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<String, Integer> handler) {
      return handler.value().length();
   }
}
