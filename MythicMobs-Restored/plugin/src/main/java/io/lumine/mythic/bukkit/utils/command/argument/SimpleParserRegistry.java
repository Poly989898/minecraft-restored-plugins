package io.lumine.mythic.bukkit.utils.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;

public class SimpleParserRegistry implements ArgumentParserRegistry {
   private final Map<TypeToken<?>, List<ArgumentParser<?>>> parsers = new ConcurrentHashMap<>();

   @Nonnull
   @Override
   public <T> Optional<ArgumentParser<T>> find(@Nonnull TypeToken<T> type) {
      Objects.requireNonNull(type, "type");
      List<ArgumentParser<?>> parsers = this.parsers.get(type);
      return parsers != null && !parsers.isEmpty() ? Optional.of((ArgumentParser<T>)parsers.get(0)) : Optional.empty();
   }

   @Nonnull
   @Override
   public <T> Collection<ArgumentParser<T>> findAll(@Nonnull TypeToken<T> type) {
      Objects.requireNonNull(type, "type");
      List<ArgumentParser<?>> parsers = this.parsers.get(type);
      return (Collection<ArgumentParser<T>>)(parsers != null && !parsers.isEmpty() ? Collections.unmodifiableList(parsers) : ImmutableList.of());
   }

   @Override
   public <T> void register(@Nonnull TypeToken<T> type, @Nonnull ArgumentParser<T> parser) {
      Objects.requireNonNull(type, "type");
      Objects.requireNonNull(parser, "parser");
      List<ArgumentParser<?>> list = this.parsers.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>());
      if (!list.contains(parser)) {
         list.add(parser);
      }
   }
}
