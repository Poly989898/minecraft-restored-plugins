package io.lumine.mythic.bukkit.utils.command.argument;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.CommandInterruptException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;

@NonnullByDefault
public interface ArgumentParser<T> {
   static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction) {
      return parseFunction::apply;
   }

   static <T> ArgumentParser<T> of(
      final Function<String, Optional<T>> parseFunction, final Function<String, CommandInterruptException> generateExceptionFunction
   ) {
      return new ArgumentParser<T>() {
         @Override
         public Optional<T> parse(@Nonnull String t) {
            return parseFunction.apply(t);
         }

         @Override
         public CommandInterruptException generateException(@Nonnull String s) {
            return generateExceptionFunction.apply(s);
         }
      };
   }

   Optional<T> parse(@Nonnull String str);

   default CommandInterruptException generateException(@Nonnull String s) {
      return new CommandInterruptException("&cUnable to parse argument: " + s);
   }

   default CommandInterruptException generateException(int missingArgumentIndex) {
      return new CommandInterruptException("&cArgument at index " + missingArgumentIndex + " is missing.");
   }

   @Nonnull
   default T parseOrFail(@Nonnull String s) throws CommandInterruptException {
      Optional<T> ret = this.parse(s);
      if (!ret.isPresent()) {
         throw this.generateException(s);
      } else {
         return ret.get();
      }
   }

   @Nonnull
   default Optional<T> parse(@Nonnull Argument argument) {
      return argument.value().flatMap(this::parse);
   }

   @Nonnull
   default T parseOrFail(@Nonnull Argument argument) throws CommandInterruptException {
      Optional<String> value = argument.value();
      if (!value.isPresent()) {
         throw this.generateException(argument.index());
      } else {
         return this.parseOrFail(value.get());
      }
   }

   @Nonnull
   default ArgumentParser<T> thenTry(@Nonnull ArgumentParser<T> other) {
      return t -> {
         Optional<T> ret = this.parse(t);
         return ret.isPresent() ? ret : other.parse(t);
      };
   }
}
