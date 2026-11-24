package io.lumine.mythic.lib.comp.adventure.tag.implementation;

import io.lumine.mythic.lib.comp.adventure.resolver.implementation.RainbowResolver;
import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class RainbowTag extends AdventureTag {
   public RainbowTag() {
      super("rainbow", new RainbowResolver(), false, true);
   }
}
