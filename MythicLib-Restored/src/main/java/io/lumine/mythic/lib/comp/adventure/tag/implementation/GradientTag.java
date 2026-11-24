package io.lumine.mythic.lib.comp.adventure.tag.implementation;

import io.lumine.mythic.lib.comp.adventure.resolver.implementation.GradientResolver;
import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class GradientTag extends AdventureTag {
   public GradientTag() {
      super("gradient", new GradientResolver(), false, true);
   }
}
