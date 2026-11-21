package io.lumine.mythic.core.skills.placeholders.metakeywords.Map;

import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@MythicMetaKeyword(
   name = "sortByValue"
)
public class SortByValueMetaKeyword extends AbstractMetaKeyword<Map<String, String>, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<Map<String, String>, List<String>> handler) {
      boolean numericSort = handler.value().values().stream().allMatch(SortByValueMetaKeyword::isNumeric);
      Comparator<Entry<String, String>> comparator = numericSort
         ? Comparator.<Entry<String, String>>comparingDouble(entry -> Double.parseDouble(entry.getValue())).thenComparing(Entry::getKey)
         : Comparator.<Entry<String, String>, String>comparing(Entry::getValue, Comparator.nullsFirst(String::compareTo)).thenComparing(Entry::getKey);
      return handler.value().entrySet().stream().sorted(comparator).map(Entry::getKey).toList();
   }

   private static boolean isNumeric(String value) {
      if (value != null && !value.isEmpty()) {
         try {
            Double.parseDouble(value);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }
}
