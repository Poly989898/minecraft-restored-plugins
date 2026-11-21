package io.lumine.mythic.core.skills.placeholders.metakeywords.Set;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@MythicMetaKeyword(
   name = "sortNum"
)
public class SortNumMetaKeyword extends AbstractMetaKeyword<Set<String>, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<Set<String>, List<String>> handler) {
      return handler.value().stream().sorted(Comparator.comparingDouble(s -> {
         try {
            return Double.parseDouble(s);
         } catch (NumberFormatException e) {
            return Double.NaN;
         }
      })).toList();
   }
}
