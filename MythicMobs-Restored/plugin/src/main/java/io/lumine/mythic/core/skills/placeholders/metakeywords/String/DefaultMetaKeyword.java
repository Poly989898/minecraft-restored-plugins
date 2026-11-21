package io.lumine.mythic.core.skills.placeholders.metakeywords.String;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "default"
)
public class DefaultMetaKeyword extends AbstractMetaKeyword<String, String> {
   public String extract(MetaKeywords.ExtractionHandler<String, String> handler) {
      int index = handler.context().getInt(0);
      return index >= 0 && index < handler.value().length() ? String.valueOf(handler.value().charAt(index)) : "UNDEFINED";
   }
}
