package io.lumine.mythic.lib.gui.editable.placeholder;

/** Every placeholder shows as ???, for items that could not be loaded. */
public class ErrorPlaceholders extends Placeholders {
   @Override
   public String parsePlaceholder(String key) {
      return "???";
   }
}
