package me.neznamy.tab.api;

import me.neznamy.tab.api.event.EventBus;

/** Compile-only stub of the TAB API (not packaged). */
public abstract class TabAPI {
   public static TabAPI getInstance() { return null; }
   public abstract EventBus getEventBus();
}
