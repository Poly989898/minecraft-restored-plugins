package io.lumine.mythic.lib.comp.adventure.argument;

import java.util.Collections;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class EmptyArgumentQueue extends AdventureArgumentQueue {
   public EmptyArgumentQueue() {
      super(Collections.emptyList());
   }
}
