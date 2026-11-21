package io.lumine.mythic.bukkit.utils.events;

import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;

public interface MergedSubscription<T> extends Subscription {
   @Nonnull
   Class<? super T> getHandledClass();

   @Nonnull
   Set<Class<? extends Event>> getEventClasses();
}
