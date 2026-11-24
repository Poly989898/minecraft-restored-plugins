package io.lumine.mythic.lib.comp.adventure.resolver.implementation;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.resolver.AdventureTagResolver;
import io.lumine.mythic.lib.util.AdventureUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdventureColorResolver implements AdventureTagResolver {
   @Nullable
   @Override
   public String resolve(@NotNull String src, @NotNull AdventureArgumentQueue args) {
      return args.hasNext()
         ? AdventureUtils.getByName(args.peek().value()).map(c -> "" + c).orElse(AdventureUtils.getByHex(args.pop().value()).map(c -> "" + c).orElse(null))
         : null;
   }
}
