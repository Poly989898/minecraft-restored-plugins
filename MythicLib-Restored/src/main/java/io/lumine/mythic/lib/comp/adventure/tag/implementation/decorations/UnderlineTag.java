package io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations;

import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class UnderlineTag extends AdventureTag {
   public UnderlineTag() {
      super("underlined", (src, argumentQueue) -> "§n", true, false, "u", "underline");
   }
}
