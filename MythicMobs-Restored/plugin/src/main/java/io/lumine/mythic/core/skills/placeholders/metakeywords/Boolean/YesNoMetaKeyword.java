package io.lumine.mythic.core.skills.placeholders.metakeywords.Boolean;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "yesno"
)
public class YesNoMetaKeyword extends AbstractMetaKeyword<Boolean, String> {
   public String extract(MetaKeywords.ExtractionHandler<Boolean, String> handler) {
      return handler.value() ? "yes" : "no";
   }
}
