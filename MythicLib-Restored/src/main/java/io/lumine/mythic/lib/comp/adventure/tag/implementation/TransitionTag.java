package io.lumine.mythic.lib.comp.adventure.tag.implementation;

import io.lumine.mythic.lib.comp.adventure.resolver.implementation.TransitionResolver;
import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;

public class TransitionTag extends AdventureTag {
   public TransitionTag() {
      super("transition", new TransitionResolver(), false, true);
   }
}
