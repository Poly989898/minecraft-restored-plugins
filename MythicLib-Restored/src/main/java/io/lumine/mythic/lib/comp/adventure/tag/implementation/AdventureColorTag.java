package io.lumine.mythic.lib.comp.adventure.tag.implementation;

import io.lumine.mythic.lib.comp.adventure.resolver.implementation.AdventureColorResolver;
import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class AdventureColorTag extends AdventureTag {
   public AdventureColorTag() {
      super("color", new AdventureColorResolver(), true, true);
   }
}
