package io.lumine.mythic.bukkit.utils;

import io.lumine.mythic.bukkit.utils.text.LevenshteinDistance;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtils {
   public static List<String> filterMatches(String lookup, Collection<String> collection) {
      return filterMatches(lookup, collection, 0);
   }

   public static List<String> filterMatches(final String lookup, Collection<String> collection, int similarityDepth) {
      List<String> matches = collection.stream()
         .filter(item -> io.lumine.mythic.bukkit.utils.lib.lang3.StringUtils.containsIgnoreCase(item, lookup))
         .sorted(new Comparator<String>() {
            public int compare(String s1, String s2) {
               boolean s1ExactStartsWith = s1.startsWith(lookup);
               boolean s2ExactStartsWith = s2.startsWith(lookup);
               boolean s1StartsWith = io.lumine.mythic.bukkit.utils.lib.lang3.StringUtils.startsWithIgnoreCase(s1, lookup);
               boolean s2StartsWith = io.lumine.mythic.bukkit.utils.lib.lang3.StringUtils.startsWithIgnoreCase(s2, lookup);
               if (s1ExactStartsWith && !s2ExactStartsWith) {
                  return -1;
               } else if (!s1ExactStartsWith && s2ExactStartsWith) {
                  return 1;
               } else if (s1StartsWith && !s2StartsWith) {
                  return -1;
               } else {
                  return !s1StartsWith && s2StartsWith ? 1 : 0;
               }
            }
         })
         .collect(Collectors.toList());
      if (matches.isEmpty() && similarityDepth > 0) {
         for (int i = 1; i <= similarityDepth; i++) {
            LevenshteinDistance ld = new LevenshteinDistance(i);
            List<String> nearMatches = collection.stream()
               .filter(item -> ld.apply(item.toLowerCase(), lookup.toLowerCase()) != -1)
               .collect(Collectors.toList());
            if (!nearMatches.isEmpty()) {
               return nearMatches;
            }
         }
      }

      return matches;
   }
}
