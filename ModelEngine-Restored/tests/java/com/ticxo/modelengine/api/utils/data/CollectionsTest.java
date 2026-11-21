package com.ticxo.modelengine.api.utils.data;

import static org.junit.jupiter.api.Assertions.*;

import com.ticxo.modelengine.api.utils.MemoizeSupplier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class CollectionsTest {
   @Test
   void tripleStoresAndUpdatesItsThreeValues() {
      Triple<String, Integer, Boolean> triple = new Triple<>("a", 1, true);
      assertEquals("a", triple.getFirst());
      assertEquals(1, triple.getSecond());
      assertTrue(triple.getThird());
      triple.setFirst("b");
      triple.setSecond(2);
      triple.setThird(false);
      assertEquals("b", triple.getFirst());
      assertEquals(2, triple.getSecond());
      assertFalse(triple.getThird());
   }

   @Test
   void nullableHashSetIgnoresNulls() {
      NullableHashSet<String> set = new NullableHashSet<>();
      assertFalse(set.add(null));
      assertTrue(set.add("x"));
      assertFalse(set.add("x"));
      assertEquals(1, set.size());
   }

   @Test
   void constSetIdentifiesElementsByHashCode() {
      ConstSet<String> set = new ConstSet<>();
      assertTrue(set.add("a"));
      assertFalse(set.add("a"));
      assertEquals(1, set.size());
      assertTrue(set.contains("a"));
      assertFalse(set.contains("b"));
   }

   @Test
   void memoizeSupplierCallsSupplierOnce() {
      AtomicInteger calls = new AtomicInteger();
      MemoizeSupplier<Integer> supplier = MemoizeSupplier.of(calls::incrementAndGet);
      assertEquals(1, supplier.get());
      assertEquals(1, supplier.get());
      assertEquals(1, calls.get());
   }

   @Test
   void memoizeSupplierAlsoCachesNull() {
      AtomicInteger calls = new AtomicInteger();
      MemoizeSupplier<String> supplier = MemoizeSupplier.of(() -> {
         calls.incrementAndGet();
         return null;
      });
      assertNull(supplier.get());
      assertNull(supplier.get());
      assertEquals(1, calls.get());
   }

   @Test
   void cachedImmutableMapCopiesOnceAndCanBeInvalidated() {
      Map<String, Integer> source = new HashMap<>();
      source.put("a", 1);
      AtomicInteger calls = new AtomicInteger();
      CachedImmutableMap<String, Integer> cached = new CachedImmutableMap<>(() -> {
         calls.incrementAndGet();
         return source;
      });
      assertNull(cached.getIfCached());
      Map<String, Integer> first = cached.get();
      assertEquals(Map.of("a", 1), first);
      assertSame(first, cached.get());
      assertEquals(1, calls.get());
      assertThrows(UnsupportedOperationException.class, () -> first.put("b", 2));

      source.put("b", 2);
      cached.invalidate();
      assertNull(cached.getIfCached());
      assertEquals(Map.of("a", 1, "b", 2), cached.get());
      assertEquals(2, calls.get());
   }

   @Test
   void cachedImmutableMapTreatsNullSupplierResultAsEmpty() {
      CachedImmutableMap<String, Integer> cached = new CachedImmutableMap<>(() -> null);
      assertTrue(cached.get().isEmpty());
   }
}
