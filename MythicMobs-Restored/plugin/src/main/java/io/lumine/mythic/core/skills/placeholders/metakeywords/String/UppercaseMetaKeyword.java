package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "uppercase"
)
public class UppercaseMetaKeyword extends AbstractMetaKeyword<String, String> {
   public String extract(MetaKeywords.ExtractionHandler<String, String> handler) {
      return handler.value().toUpperCase();
   }
}
