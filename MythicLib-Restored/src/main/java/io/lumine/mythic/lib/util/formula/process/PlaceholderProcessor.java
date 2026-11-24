package io.lumine.mythic.lib.util.formula.process;

import org.jetbrains.annotations.NotNull;

@Deprecated
@FunctionalInterface
public interface PlaceholderProcessor {
   @NotNull
   void processPlaceholder(@NotNull String text);
}
