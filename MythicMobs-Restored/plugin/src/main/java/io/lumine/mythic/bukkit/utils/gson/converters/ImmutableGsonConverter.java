package io.lumine.mythic.bukkit.utils.gson.converters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList.Builder;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import javax.annotation.Nullable;

@NonnullByDefault
class ImmutableGsonConverter extends AbstractGsonConverter<ImmutableMap<String, Object>, ImmutableList<Object>, ImmutableSet<Object>> {
   public static final ImmutableGsonConverter INSTANCE = new ImmutableGsonConverter();

   private ImmutableGsonConverter() {
   }

   @Override
   protected AbstractGsonConverter.MapBuilder<ImmutableMap<String, Object>, String, Object> newMapBuilder() {
      return new ImmutableGsonConverter.ImmutableMapBuilder<>();
   }

   @Override
   protected AbstractGsonConverter.ListBuilder<ImmutableList<Object>, Object> newListBuilder() {
      return new ImmutableGsonConverter.ImmutableListBuilder<>();
   }

   @Override
   protected AbstractGsonConverter.SetBuilder<ImmutableSet<Object>, Object> newSetBuilder() {
      return new ImmutableGsonConverter.ImmutableSetBuilder<>();
   }

   private static final class ImmutableListBuilder<E> implements AbstractGsonConverter.ListBuilder<ImmutableList<E>, E> {
      private final Builder<E> builder = ImmutableList.builder();

      @Override
      public void add(@Nullable E element) {
         if (element != null) {
            this.builder.add(element);
         }
      }

      public ImmutableList<E> build() {
         return this.builder.build();
      }
   }

   private static final class ImmutableMapBuilder<K, V> implements AbstractGsonConverter.MapBuilder<ImmutableMap<K, V>, K, V> {
      private final com.google.common.collect.ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

      @Override
      public void put(@Nullable K key, @Nullable V value) {
         if (key != null && value != null) {
            this.builder.put(key, value);
         }
      }

      public ImmutableMap<K, V> build() {
         return this.builder.build();
      }
   }

   private static final class ImmutableSetBuilder<E> implements AbstractGsonConverter.SetBuilder<ImmutableSet<E>, E> {
      private final com.google.common.collect.ImmutableSet.Builder<E> builder = ImmutableSet.builder();

      @Override
      public void add(@Nullable E element) {
         if (element != null) {
            this.builder.add(element);
         }
      }

      public ImmutableSet<E> build() {
         return this.builder.build();
      }
   }
}
