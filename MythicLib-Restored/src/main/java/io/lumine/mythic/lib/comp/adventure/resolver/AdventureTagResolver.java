package io.lumine.mythic.lib.comp.adventure.resolver;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface AdventureTagResolver {
   @Nullable
   String resolve(@NotNull String text, @NotNull AdventureArgumentQueue adventureArgumentQueue);
}
