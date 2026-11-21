package litebans.api;

import litebans.api.exception.MissingImplementationException;
import lombok.Generated;

public abstract class RandomID {
   public static final int RESULT_ERROR = -1;
   private static RandomID instance;

   public static RandomID get() {
      if (instance == null) {
         throw new MissingImplementationException();
      } else {
         return instance;
      }
   }

   public abstract String convert(long value);

   public abstract long reveal(String text);

   @Generated
   public static void setInstance(RandomID randomID) {
      instance = randomID;
   }
}
