package io.lumine.mythic.bukkit.utils.gson.converters;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.annotation.Nullable;

@NonnullByDefault
class MutableGsonConverter extends AbstractGsonConverter<HashMap<String, Object>, ArrayList<Object>, HashSet<Object>> {
   public static final MutableGsonConverter INSTANCE = new MutableGsonConverter();

   private MutableGsonConverter() {
   }

   @Override
   protected AbstractGsonConverter.MapBuilder<HashMap<String, Object>, String, Object> newMapBuilder() {
      return new MutableGsonConverter.MutableMapBuilder<>();
   }

   @Override
   protected AbstractGsonConverter.ListBuilder<ArrayList<Object>, Object> newListBuilder() {
      return new MutableGsonConverter.MutableListBuilder<>();
   }

   @Override
   protected AbstractGsonConverter.SetBuilder<HashSet<Object>, Object> newSetBuilder() {
      return new MutableGsonConverter.MutableSetBuilder<>();
   }

   private static final class MutableListBuilder<E> implements AbstractGsonConverter.ListBuilder<ArrayList<E>, E> {
      private final ArrayList<E> builder = new ArrayList<>();

      @Override
      public void add(@Nullable E element) {
         this.builder.add(element);
      }

      public ArrayList<E> build() {
         return this.builder;
      }
   }

   private static final class MutableMapBuilder<K, V> implements AbstractGsonConverter.MapBuilder<HashMap<K, V>, K, V> {
      private final HashMap<K, V> builder = new HashMap<>();

      @Override
      public void put(@Nullable K key, @Nullable V value) {
         this.builder.put(key, value);
      }

      public HashMap<K, V> build() {
         return this.builder;
      }
   }

   private static final class MutableSetBuilder<E> implements AbstractGsonConverter.SetBuilder<HashSet<E>, E> {
      private final HashSet<E> builder = new HashSet<>();

      @Override
      public void add(@Nullable E element) {
         this.builder.add(element);
      }

      public HashSet<E> build() {
         return this.builder;
      }
   }
}
