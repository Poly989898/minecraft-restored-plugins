package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Collections;
import java.util.List;

@MythicMetaKeyword(
   name = "shuffle"
)
public class ShuffleMetaKeyword extends AbstractMetaKeyword<List<String>, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<List<String>, List<String>> handler) {
      Collections.shuffle(handler.value());
      return handler.value();
   }
}
