package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Objects;

public class PlaceholderLongImpl extends PlaceholderNumber<Long> implements PlaceholderLong {
   public static PlaceholderLongImpl of(String string) {
      try {
         if (string == null) {
            return null;
         } else if (io.lumine.mythic.bukkit.utils.numbers.RandomInt.matchesRange(string)) {
            return new RandomLong(string);
         } else if (string.matches("-?\\d+")) {
            return new StaticLong(string);
         } else if (!MythicBukkit.isVolatile()) {
            MythicLogger.error(
               "Failed to parse Placeholder '"
                  + string
                  + "': Math and variables in numeric values require premium! Consider getting MythicMobs Premium at www.mythiccraft.io"
            );
            return new StaticLong("1");
         } else {
            return new PlaceholderLongImpl(string);
         }
      } catch (Exception e) {
         e.printStackTrace();
         MythicLogger.error("Failed to parse Placeholder '" + string + "': invalid syntax.");
         return new StaticLong("1");
      }
   }

   PlaceholderLongImpl(String data) {
      super(data);
   }

   public boolean isStaticallyEqualTo(Long value) {
      return this.isStatic() && Objects.equals(this.get(), value);
   }

   protected Long convertNumberToSelfType(double number) {
      return (long)number;
   }

   protected Long convertStringToSelfType(String string) {
      return Long.parseLong(string);
   }

   protected Long getDefaultValue() {
      return 1L;
   }
}
