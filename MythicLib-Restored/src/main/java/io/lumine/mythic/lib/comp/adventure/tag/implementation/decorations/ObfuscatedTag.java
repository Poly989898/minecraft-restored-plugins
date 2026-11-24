package io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations;

import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class ObfuscatedTag extends AdventureTag {
   public ObfuscatedTag() {
      super("obfuscated", (src, argumentQueue) -> "§k", true, false, "obf");
   }
}
