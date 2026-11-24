package net.Indyuce.mmoitems.api.util;

import java.util.HashMap;
import java.util.Map;

public class StringValue {
   private final String name;
   private final double value;

   public StringValue(String text, double value) {
      this.name = text;
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public double getValue() {
      return this.value;
   }

   @Deprecated
   public static Map<String, Double> readFromArray(StringValue... stringValues) {
      HashMap map = new HashMap();

      for (StringValue stringValue : stringValues) {
         map.put(stringValue.getName(), stringValue.getValue());
      }

      return map;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof StringValue && ((StringValue)obj).name.equals(this.name) && ((StringValue)obj).value == this.value;
   }
}
