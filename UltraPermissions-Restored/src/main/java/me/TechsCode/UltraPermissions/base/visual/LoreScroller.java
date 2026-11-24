package me.TechsCode.UltraPermissions.base.visual;

import java.util.ArrayList;
import java.util.List;

public class LoreScroller {
   public LoreScroller() {
   }

   public static List<String> scroller(List<String> texts, int count, long value) {
      if (texts.size() <= count) {
         return texts;
      }

      ArrayList list = new ArrayList();
      list.addAll(texts);
      list.addAll(texts);
      list.addAll(texts);
      long elapsed = System.currentTimeMillis() - value;
      int offset = Math.round((float)(elapsed / 150L)) % texts.size();
      return list.subList(offset, offset + count);
   }
}
