package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "capitalize"
)
public class CapitalizeMetaKeyword extends AbstractMetaKeyword<String, String> {
   public String extract(MetaKeywords.ExtractionHandler<String, String> handler) {
      String value = handler.value();
      return value.isEmpty() ? value : value.substring(0, 1).toUpperCase() + value.substring(1);
   }
}
