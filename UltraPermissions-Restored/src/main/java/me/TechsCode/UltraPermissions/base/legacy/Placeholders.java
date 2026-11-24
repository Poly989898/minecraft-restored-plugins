package me.TechsCode.UltraPermissions.base.legacy;

import java.util.HashMap;
import java.util.Map.Entry;

public class Placeholders extends HashMap<String, String> {
   public Placeholders() {
   }

   public String apply(String text) {
      String result = text;

      for (Entry entry : this.entrySet()) {
         result = result.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue());
      }

      return result;
   }

   public static Placeholders c(String text, String value) {
      Placeholders map = new Placeholders();
      map.put(text, value);
      return map;
   }
}
