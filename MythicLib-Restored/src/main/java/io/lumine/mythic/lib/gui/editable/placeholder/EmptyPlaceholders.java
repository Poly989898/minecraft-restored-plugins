package io.lumine.mythic.lib.gui.editable.placeholder;

/** Placeholders for items that do not have any. */
public class EmptyPlaceholders extends Placeholders {
   @Override
   public String parsePlaceholder(String key) {
      return null;
   }
}
