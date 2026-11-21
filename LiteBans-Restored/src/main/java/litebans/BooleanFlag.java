package litebans;

import java.io.Serializable;

public final class BooleanFlag implements Serializable {
   public boolean flag;

   @Override
   public String toString() {
      return String.valueOf(this.flag);
   }
}
