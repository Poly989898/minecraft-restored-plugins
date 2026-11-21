package io.lumine.mythic.core.skills.placeholders.metakeywords.Map;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MythicMetaKeyword(
   name = "values"
)
public class ValuesMetaKeyword extends AbstractMetaKeyword<Map<String, String>, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<Map<String, String>, List<String>> handler) {
      return new ArrayList<>(handler.value().values());
   }
}
