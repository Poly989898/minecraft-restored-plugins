package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.skills.placeholders.PlaceholderTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderTimeImpl extends PlaceholderNumber<Instant> implements PlaceholderTime {
   public static final Map<Character, Integer> UnitConversionMap = new HashMap<>();

   public static PlaceholderTimeImpl of(String data) {
      return new PlaceholderTimeImpl(data);
   }

   public PlaceholderTimeImpl(String data) {
      super(data);
   }

   protected Instant convertNumberToSelfType(double number) {
      return Instant.ofEpochMilli((long)number);
   }

   protected Instant convertStringToSelfType(String string) {
      if (string != null && !string.isEmpty()) {
         char lastChar = string.charAt(string.length() - 1);
         Integer unitConversion = UnitConversionMap.get(lastChar);

         try {
            if (unitConversion != null) {
               String numericPart = string.substring(0, string.length() - 1);
               long value = Long.parseLong(numericPart);
               return Instant.ofEpochMilli(value * unitConversion.intValue());
            } else {
               return Instant.ofEpochMilli(Long.parseLong(string));
            }
         } catch (NumberFormatException e) {
            return this.getDefaultValue();
         }
      } else {
         return this.getDefaultValue();
      }
   }

   protected Instant getDefaultValue() {
      return Instant.now();
   }

   public boolean isStaticallyEqualTo(Instant value) {
      return false;
   }

   static {
      UnitConversionMap.put('t', 50);
      UnitConversionMap.put('s', 1000);
      UnitConversionMap.put('m', 60000);
      UnitConversionMap.put('h', 3600000);
      UnitConversionMap.put('d', 86400000);
      UnitConversionMap.put('w', 604800000);
   }
}
