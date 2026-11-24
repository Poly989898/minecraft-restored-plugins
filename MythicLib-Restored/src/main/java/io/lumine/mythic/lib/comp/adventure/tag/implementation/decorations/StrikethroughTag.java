package io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations;

import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class StrikethroughTag extends AdventureTag {
   public StrikethroughTag() {
      super("strikethrough", (src, argumentQueue) -> "§m", true, false, "st");
   }
}
