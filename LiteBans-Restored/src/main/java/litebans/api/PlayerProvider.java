package litebans.api;

import litebans.api.exception.MissingImplementationException;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerProvider {
   private static PlayerProvider instance;

   public static PlayerProvider get() {
      if (instance == null) {
         throw new MissingImplementationException();
      } else {
         return instance;
      }
   }

   @NotNull
   public abstract String provide(String text);

   @Generated
   public static void setInstance(PlayerProvider playerProvider) {
      instance = playerProvider;
   }
}
