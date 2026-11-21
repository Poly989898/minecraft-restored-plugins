package io.lumine.mythic.core.utils.string.replacer;

import org.apache.commons.lang3.StringUtils;

public class ReplaceEachReplacer extends StringReplacer {
   protected volatile String[] keys = new String[0];
   protected volatile String[] values = new String[0];

   @Override
   public synchronized void compile() {
      this.keys = this.dictionary.keySet().toArray(new String[0]);
      this.values = this.dictionary.values().toArray(new String[0]);
   }

   @Override
   public synchronized void uncompile() {
      this.keys = new String[0];
      this.values = new String[0];
   }

   @Override
   public synchronized void clear() {
      this.dictionary.clear();
      this.uncompile();
   }

   @Override
   public String replaceText(String text) {
      return StringUtils.replaceEach(text, this.keys, this.values);
   }
}
