package io.lumine.mythic.lib.comp.adventure.tag.implementation;

import io.lumine.mythic.lib.comp.adventure.resolver.implementation.HexColorResolver;
import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class HexColorTag extends AdventureTag {
   public HexColorTag() {
      super("HEX", new HexColorResolver(), false, true, "#");
   }
}
