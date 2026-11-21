package litebans;

import java.io.Serializable;

class KotlinSerializableImpl2 implements Serializable {
   private static final KotlinSerializableImpl2 return3 = new KotlinSerializableImpl2();

   private KotlinSerializableImpl2() {
   }

   private final Object obtainObj() {
      return return3;
   }

   static KotlinSerializableImpl2 obtainKotlinSerializableImpl() {
      return return3;
   }
}
