package io.lumine.mythic.lib.api.event.fake;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FakeEventCaller<E extends Event> {
   boolean isFake(@NotNull E value);
}
