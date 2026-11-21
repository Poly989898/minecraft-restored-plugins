package io.lumine.mythic.bukkit.utils.events;

import javax.annotation.Nonnull;
import org.bukkit.event.Event;

public interface SingleSubscription<T extends Event> extends Subscription {
   @Nonnull
   Class<T> getEventClass();
}
