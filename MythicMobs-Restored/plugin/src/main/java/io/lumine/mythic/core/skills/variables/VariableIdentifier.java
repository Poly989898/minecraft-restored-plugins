package io.lumine.mythic.core.skills.variables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VariableIdentifier(String name, @Nullable String defaultValue) {
   public boolean hasDefault() {
      return this.defaultValue != null;
   }

   @NotNull
   public String getDefault() {
      return this.hasDefault() ? this.defaultValue : "UNDEFINED";
   }

   @NotNull
   public static VariableIdentifier of(String name) {
      int separator = name.indexOf(124);
      return separator < 0 ? new VariableIdentifier(name, null) : new VariableIdentifier(name.substring(0, separator), name.substring(separator + 1));
   }
}
