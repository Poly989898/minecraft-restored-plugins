package io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations;

import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class BoldTag extends AdventureTag {
   public BoldTag() {
      super("bold", (src, argumentQueue) -> "§l", true, false, "b");
   }
}
