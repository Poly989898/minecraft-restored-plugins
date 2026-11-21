package com.nexomc.nexo.nms.commons.furniture.bed;

import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;

// $FF: synthetic class
@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 176
)
public final class FurnitureBedPacket$inlined$sam$i$java_util_function_Predicate$0 implements Predicate<Object> {
   private final Function1 function;

   public FurnitureBedPacket$inlined$sam$i$java_util_function_Predicate$0(Function1 function) {
      super();
      this.function = function;
   }

   @Override
   public boolean test(Object o) {
      return (Boolean)this.function.invoke(o);
   }
}
