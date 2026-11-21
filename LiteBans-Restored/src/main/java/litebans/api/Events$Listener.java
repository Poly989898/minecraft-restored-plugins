package litebans.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Events$Listener {
   public void broadcastSent(@NotNull String text, @Nullable String text2) {
   }

   public void entryAdded(Entry entry) {
   }

   public void entryRemoved(Entry entry) {
   }

   private static void $$$reportNull$$$0(int value) {
      throw new IllegalArgumentException(
         String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "message", "litebans/api/Events$Listener", "broadcastSent")
      );
   }
}
