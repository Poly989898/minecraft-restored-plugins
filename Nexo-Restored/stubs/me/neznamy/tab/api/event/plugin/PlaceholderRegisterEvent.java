package me.neznamy.tab.api.event.plugin;

import java.util.function.Supplier;

/** Compile-only stub (not packaged). */
public interface PlaceholderRegisterEvent {
   String getIdentifier();
   void setServerPlaceholder(Supplier<String> supplier);
}
