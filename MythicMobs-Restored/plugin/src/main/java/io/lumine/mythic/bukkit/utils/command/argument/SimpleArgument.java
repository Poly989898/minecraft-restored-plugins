package io.lumine.mythic.bukkit.utils.command.argument;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@NonnullByDefault
public class SimpleArgument implements Argument {
   protected final int index;
   protected final Optional<String> value;

   public SimpleArgument(int index, @Nullable String value) {
      this.index = index;
      this.value = Optional.ofNullable(value);
   }

   @Override
   public int index() {
      return this.index;
   }

   @Nonnull
   @Override
   public Optional<String> value() {
      return this.value;
   }

   @Override
   public boolean isPresent() {
      return this.value.isPresent();
   }
}
