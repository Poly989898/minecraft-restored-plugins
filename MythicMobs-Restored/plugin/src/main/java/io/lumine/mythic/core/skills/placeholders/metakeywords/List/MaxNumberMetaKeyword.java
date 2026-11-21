package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.List;

@MythicMetaKeyword(
   name = "maxNumber"
)
public class MaxNumberMetaKeyword extends AbstractMetaKeyword<List<String>, Double> {
   public Double extract(MetaKeywords.ExtractionHandler<List<String>, Double> handler) {
      return handler.value().stream().mapToDouble(Double::parseDouble).max().orElse(-1.0);
   }
}
