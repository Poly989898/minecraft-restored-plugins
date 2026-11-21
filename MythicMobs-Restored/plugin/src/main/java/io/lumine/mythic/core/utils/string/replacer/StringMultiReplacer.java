package io.lumine.mythic.core.utils.string.replacer;

import com.google.common.collect.Maps;
import java.util.Map;

public class StringMultiReplacer {
   Map<Class<? extends StringReplacer>, StringReplacer> replacers = Maps.newConcurrentMap();

   public <T extends StringReplacer> void addReplacer(Class<T> replacer, T stringReplacer) {
      this.replacers.put(replacer, stringReplacer);
   }

   public <T extends StringReplacer> String useReplacer(Class<T> replacer, String text) throws RuntimeException {
      if (this.replacers.containsKey(replacer)) {
         return this.replacers.get(replacer).replaceText(text);
      } else {
         throw new RuntimeException("No such replacer: " + replacer);
      }
   }

   public void register(String key, String replacement) {
      for (StringReplacer replacer : this.replacers.values()) {
         replacer.register(key, replacement);
      }
   }

   public void unregister(String key) {
      for (StringReplacer replacer : this.replacers.values()) {
         replacer.unregister(key);
      }
   }

   public void compile() {
      for (StringReplacer replacer : this.replacers.values()) {
         replacer.compile();
      }
   }
}
