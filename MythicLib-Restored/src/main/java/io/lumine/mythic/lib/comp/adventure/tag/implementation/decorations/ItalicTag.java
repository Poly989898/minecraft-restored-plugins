package io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations;

import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class ItalicTag extends AdventureTag {
   public ItalicTag() {
      super("italic", (src, argumentQueue) -> "§o", true, false, "i");
   }
}
