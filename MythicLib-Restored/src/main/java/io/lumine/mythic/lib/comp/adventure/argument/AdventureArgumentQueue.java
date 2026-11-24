package io.lumine.mythic.lib.comp.adventure.argument;

import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class AdventureArgumentQueue {
   private final List<AdventureArgument> args;
   private int ptr = 0;

   public AdventureArgumentQueue(@NotNull List<AdventureArgument> args) {
      this.args = args;
   }

   @NotNull
   public AdventureArgument pop() {
      if (!this.hasNext()) {
         throw new IllegalStateException("Missing argument for this tag!");
      } else {
         return this.args.get(this.ptr++);
      }
   }

   @NotNull
   public AdventureArgument popOr(@NotNull String errorMessage) {
      if (!this.hasNext()) {
         throw new IllegalArgumentException(errorMessage);
      } else {
         return this.args.get(this.ptr++);
      }
   }

   @NotNull
   public AdventureArgument popOr(@NotNull Supplier<String> errorMessage) {
      if (!this.hasNext()) {
         throw new IllegalArgumentException(errorMessage.get());
      } else {
         return this.args.get(this.ptr++);
      }
   }

   @NotNull
   public AdventureArgument peek() {
      return this.hasNext() ? this.args.get(this.ptr) : null;
   }

   public boolean hasNext() {
      return this.ptr < this.args.size();
   }

   public void reset() {
      this.ptr = 0;
   }

   @Override
   public String toString() {
      return this.args.toString();
   }
}
