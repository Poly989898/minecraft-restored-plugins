package litebans;

import java.io.Serializable;

public final class InitializedLazyImpl implements Lazy, Serializable {
   private final Object obj9;

   public InitializedLazyImpl(Object obj) {
      this.obj9 = obj;
   }

   @Override
   public Object getValue() {
      return this.obj9;
   }

   @Override
   public String toString() {
      return String.valueOf(this.getValue());
   }

   @Override
   public boolean isInitialized() {
      return true;
   }
}
