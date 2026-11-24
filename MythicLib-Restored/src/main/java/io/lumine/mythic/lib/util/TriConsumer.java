package io.lumine.mythic.lib.util;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
   void accept(T value, U value2, V value3);
}
