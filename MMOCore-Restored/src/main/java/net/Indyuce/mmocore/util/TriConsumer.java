package net.Indyuce.mmocore.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
   void accept(A value, B value2, C value3);
}
