package io.lumine.mythic.lib.comp.adventure.resolver.implementation;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.resolver.AdventureTagResolver;
import io.lumine.mythic.lib.util.AdventureUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaColorResolver implements AdventureTagResolver {
   @Nullable
   @Override
   public String resolve(@NotNull String tag, @NotNull AdventureArgumentQueue argumentQueue) {
      return AdventureUtils.getByName(tag).map(chatColor -> String.format("%c%c", '§', chatColor.getChar())).orElse(null);
   }
}
