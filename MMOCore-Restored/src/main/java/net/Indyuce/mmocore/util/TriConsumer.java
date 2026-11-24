package net.Indyuce.mmocore.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
   void accept(A first, B second, C third);
}
