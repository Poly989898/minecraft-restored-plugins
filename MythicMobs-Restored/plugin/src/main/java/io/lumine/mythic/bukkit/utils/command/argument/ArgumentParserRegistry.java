package io.lumine.mythic.bukkit.utils.command.argument;

import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ArgumentParserRegistry {
   @Nonnull
   <T> Optional<ArgumentParser<T>> find(@Nonnull TypeToken<T> typeToken);

   @Nonnull
   default <T> Optional<ArgumentParser<T>> find(@Nonnull Class<T> clazz) {
      return this.find(TypeToken.of(clazz));
   }

   @Nonnull
   <T> Collection<ArgumentParser<T>> findAll(@Nonnull TypeToken<T> typeToken);

   @Nonnull
   default <T> Collection<ArgumentParser<T>> findAll(@Nonnull Class<T> clazz) {
      return this.findAll(TypeToken.of(clazz));
   }

   <T> void register(@Nonnull TypeToken<T> typeToken, @Nonnull ArgumentParser<T> argumentParser);

   default <T> void register(@Nonnull Class<T> clazz, @Nonnull ArgumentParser<T> parser) {
      this.register(TypeToken.of(clazz), parser);
   }
}
