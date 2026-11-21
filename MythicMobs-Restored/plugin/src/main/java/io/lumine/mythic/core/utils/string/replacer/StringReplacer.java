package io.lumine.mythic.core.utils.string.replacer;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class StringReplacer {
   protected final Map<String, String> dictionary = Maps.newConcurrentMap();

   public void register(String key, String replacement) {
      this.dictionary.put(key, replacement);
   }

   public void unregister(String key) {
      this.dictionary.remove(key);
   }

   public abstract String replaceText(String str);

   public abstract void compile();

   public abstract void uncompile();

   public abstract void clear();
}
