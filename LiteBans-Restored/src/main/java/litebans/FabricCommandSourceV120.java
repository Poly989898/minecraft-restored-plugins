package litebans;

import net.minecraft.class_3222;

public class FabricCommandSourceV120 extends FabricCommandSourceV121 {
   public FabricCommandSourceV120(Platform var1) {
      super(var1);
   }

   @Override
   public Object d_1(Object var1) {
      return var1 instanceof class_3222 ? ((class_3222)var1).method_5671() : var1;
   }
}
