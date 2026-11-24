package io.lumine.mythic.lib.comp.adventure.argument;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public class AdventureArgument {
   private final String value;

   public AdventureArgument(@NotNull String value) {
      this.value = value;
   }

   @NotNull
   public String toLowerCase() {
      return this.value.toLowerCase();
   }

   @NotNull
   public String value() {
      return this.value;
   }

   public boolean isTrue() {
      return "true".equals(this.value()) || "on".equals(this.value());
   }

   public boolean isFalse() {
      return "false".equals(this.value()) || "off".equals(this.value());
   }

   @NotNull
   public OptionalInt asInt() {
      try {
         return OptionalInt.of(Integer.parseInt(this.value()));
      } catch (NumberFormatException ex) {
         return OptionalInt.empty();
      }
   }

   @NotNull
   public OptionalDouble asDouble() {
      try {
         return OptionalDouble.of(Double.parseDouble(this.value()));
      } catch (NumberFormatException ex) {
         return OptionalDouble.empty();
      }
   }
}
