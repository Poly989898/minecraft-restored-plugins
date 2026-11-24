package io.lumine.mythic.lib.script.variable;

import org.jetbrains.annotations.NotNull;

public interface VariableRegistry<T> {
   @NotNull
   Variable<?> accessVariable(@NotNull T var1, @NotNull String var2);

   boolean hasVariable(String var1);
}
