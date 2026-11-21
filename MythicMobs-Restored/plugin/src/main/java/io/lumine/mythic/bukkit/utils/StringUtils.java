package io.lumine.mythic.bukkit.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
   public static List<String> split(String str, char c) {
      List<String> strings = new ArrayList<>();
      int start = 0;

      for (int i = 0; i < str.length(); i++) {
         if (str.charAt(i) == c) {
            strings.add(str.substring(start, i));
            start = i + 1;
         }
      }

      strings.add(str.substring(start, str.length()));
      return strings;
   }
}
