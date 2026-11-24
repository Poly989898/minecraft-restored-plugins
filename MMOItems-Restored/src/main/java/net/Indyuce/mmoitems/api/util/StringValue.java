package net.Indyuce.mmoitems.api.util;

import java.util.HashMap;
import java.util.Map;

public class StringValue {
   private final String name;
   private final double value;

   public StringValue(String var1, double var2) {
      this.name = var1;
      this.value = var2;
   }

   public String getName() {
      return this.name;
   }

   public double getValue() {
      return this.value;
   }

   @Deprecated
   public static Map<String, Double> readFromArray(StringValue... var0) {
      HashMap var1 = new HashMap();

      for (StringValue var5 : var0) {
         var1.put(var5.getName(), var5.getValue());
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof StringValue && ((StringValue)var1).name.equals(this.name) && ((StringValue)var1).value == this.value;
   }
}
