package io.lumine.mythic.lib.comp.adventure.resolver.implementation;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.resolver.AdventureTagResolver;
import io.lumine.mythic.lib.util.AdventureUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HexColorResolver implements AdventureTagResolver {
   @Nullable
   @Override
   public String resolve(@NotNull String tag, @NotNull AdventureArgumentQueue args) {
      return args.hasNext() ? AdventureUtils.getByHex(args.pop().value()).map(chatColor -> "" + chatColor).orElse(null) : null;
   }
}
