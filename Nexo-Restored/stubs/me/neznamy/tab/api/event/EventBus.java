package me.neznamy.tab.api.event;

/** Compile-only stub (not packaged). */
public interface EventBus {
   <E> void register(Class<E> eventClass, EventHandler<E> handler);
}
