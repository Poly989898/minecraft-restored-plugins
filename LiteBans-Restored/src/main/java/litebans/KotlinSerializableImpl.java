package litebans;

import java.io.Serializable;

public final class KotlinSerializableImpl implements Serializable {
   public Object obj;

   @Override
   public String toString() {
      return String.valueOf(this.obj);
   }
}
