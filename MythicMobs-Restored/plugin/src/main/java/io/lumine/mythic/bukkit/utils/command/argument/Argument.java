package io.lumine.mythic.bukkit.utils.command.argument;

import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.Commands;
import io.lumine.mythic.bukkit.utils.command.CommandInterruptException;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface Argument {
   int index();

   @Nonnull
   Optional<String> value();

   @Nonnull
   default <T> Optional<T> parse(@Nonnull ArgumentParser<T> parser) {
      return parser.parse(this);
   }

   @Nonnull
   default <T> T parseOrFail(@Nonnull ArgumentParser<T> parser) throws CommandInterruptException {
      return parser.parseOrFail(this);
   }

   @Nonnull
   default <T> Optional<T> parse(@Nonnull TypeToken<T> type) {
      return Commands.parserRegistry().<T>find(type).flatMap(this::parse);
   }

   @Nonnull
   default <T> T parseOrFail(@Nonnull TypeToken<T> type) throws CommandInterruptException {
      ArgumentParser<T> parser = Commands.parserRegistry().<T>find(type).orElse(null);
      if (parser == null) {
         throw new RuntimeException("Unable to find ArgumentParser for " + type);
      } else {
         return this.parseOrFail(parser);
      }
   }

   @Nonnull
   default <T> Optional<T> parse(@Nonnull Class<T> clazz) {
      return Commands.parserRegistry().find(clazz).flatMap(this::parse);
   }

   @Nonnull
   default <T> T parseOrFail(@Nonnull Class<T> clazz) throws CommandInterruptException {
      ArgumentParser<T> parser = Commands.parserRegistry().find(clazz).orElse(null);
      if (parser == null) {
         throw new RuntimeException("Unable to find ArgumentParser for " + clazz);
      } else {
         return this.parseOrFail(parser);
      }
   }

   boolean isPresent();

   default void assertPresent() throws CommandInterruptException {
      CommandInterruptException.makeAssertion(this.isPresent(), "&cArgument at index " + this.index() + " is not present.");
   }
}
