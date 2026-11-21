package litebans;

import java.util.concurrent.CopyOnWriteArrayList;
import litebans.api.Events;
import litebans.api.Events$Listener;
import org.jetbrains.annotations.NotNull;

public final class KotlinEventsSub extends Events {
   private final CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();

   public final CopyOnWriteArrayList getCopyOnWriteArrayList() {
      return this.copyOnWriteArrayList;
   }

   @Override
   public void register(@NotNull Events$Listener events$Listener) {
      if (this.copyOnWriteArrayList.contains(events$Listener)) {
         String text = "Listener is already registered: " + events$Listener;
         throw new IllegalArgumentException(text.toString());
      }

      this.copyOnWriteArrayList.add(events$Listener);
   }

   @Override
   public void unregister(@NotNull Events$Listener events$Listener) {
      this.copyOnWriteArrayList.remove(events$Listener);
   }

   @Override
   public String toString() {
      return "Events";
   }
}
