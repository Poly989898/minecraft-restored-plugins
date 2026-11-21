package io.lumine.mythic.core.utils.string.replacer;

import com.roklenarcic.util.strings.AhoCorasickMap;
import com.roklenarcic.util.strings.MapMatchListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;

public class AhoCorasickReplacer extends StringReplacer {
   @Getter
   protected volatile boolean compiled = false;
   protected volatile AhoCorasickMap<String> replacer;

   @Override
   public void register(String key, String rep) {
      super.register(key, rep);
   }

   @Override
   public void unregister(String key) {
      super.unregister(key);
   }

   @Override
   public synchronized void compile() {
      this.uncompile();
      List<String> keys = new ArrayList<>();
      List<String> values = new ArrayList<>();

      for (Entry<String, String> entry : this.dictionary.entrySet()) {
         keys.add(entry.getKey());
         values.add(entry.getValue());
      }

      this.replacer = new AhoCorasickMap<>(keys, values, true);
      this.compiled = true;
   }

   @Override
   public synchronized void uncompile() {
      this.replacer = null;
      this.compiled = false;
   }

   @Override
   public synchronized void clear() {
      this.dictionary.clear();
      this.uncompile();
   }

   protected void assureCompiled() {
      if (!this.compiled || this.replacer == null) {
         synchronized (this) {
            if (!this.compiled || this.replacer == null) {
               this.compile();
            }
         }
      }
   }

   @Override
   public String replaceText(String text) {
      this.assureCompiled();
      StringBuilder result = new StringBuilder();
      int[] lastEndPosition = new int[]{0};
      MapMatchListener<String> listener = (haystack1, startPosition, endPosition, replacementValue) -> {
         result.append(haystack1, lastEndPosition[0], startPosition);
         result.append(replacementValue);
         lastEndPosition[0] = endPosition;
         return true;
      };
      this.replacer.match(text, listener);
      result.append(text.substring(lastEndPosition[0]));
      return result.toString();
   }
}
