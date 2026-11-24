package io.lumine.mythic.lib.script.variable;

import org.jetbrains.annotations.NotNull;

public interface VariableRegistry<T> {
   @NotNull
   Variable<?> accessVariable(@NotNull T value, @NotNull String text);

   boolean hasVariable(String text);
}
