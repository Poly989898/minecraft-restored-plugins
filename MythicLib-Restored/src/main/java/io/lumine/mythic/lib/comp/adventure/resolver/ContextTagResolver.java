package io.lumine.mythic.lib.comp.adventure.resolver;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ContextTagResolver extends AdventureTagResolver {
   @Nullable
   String resolve(@NotNull String text, @NotNull AdventureArgumentQueue adventureArgumentQueue, @NotNull String text2, @NotNull List<String> texts);

   @Nullable
   @Override
   default String resolve(@NotNull String src, @NotNull AdventureArgumentQueue argumentQueue) {
      return null;
   }
}
