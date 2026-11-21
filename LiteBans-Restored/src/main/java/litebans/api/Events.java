package litebans.api;

import litebans.api.exception.MissingImplementationException;
import lombok.Generated;

public abstract class Events {
   private static Events instance;

   public static Events get() {
      if (instance == null) {
         throw new MissingImplementationException();
      } else {
         return instance;
      }
   }

   public abstract void register(Events$Listener events$Listener);

   public abstract void unregister(Events$Listener events$Listener);

   @Generated
   public static void setInstance(Events events) {
      instance = events;
   }
}
