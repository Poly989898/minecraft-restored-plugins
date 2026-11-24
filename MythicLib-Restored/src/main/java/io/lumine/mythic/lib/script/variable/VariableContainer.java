package io.lumine.mythic.lib.script.variable;

import org.jetbrains.annotations.Nullable;

public interface VariableContainer {
   @Nullable
   Variable getVariable(String text);
}
