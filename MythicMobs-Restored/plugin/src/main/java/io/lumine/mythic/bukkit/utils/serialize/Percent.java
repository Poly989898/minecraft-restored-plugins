package io.lumine.mythic.bukkit.utils.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import javax.annotation.Nonnull;

public class Percent implements GsonSerializable {
   private double value;

   public static Percent of(String input) {
      return new Percent(input);
   }

   public static Percent of(double decimalValue) {
      return new Percent(String.valueOf(decimalValue));
   }

   public Percent(String input) {
      this.parse(input);
   }

   private void parse(String input) {
      if (input != null && !input.isEmpty()) {
         input = input.trim();

         try {
            if (input.endsWith("%")) {
               this.value = Double.parseDouble(input.substring(0, input.length() - 1).trim()) / 100.0;
            } else {
               double decimalValue = Double.parseDouble(input);
               this.value = decimalValue;
            }
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid percentage format: " + input, e);
         }
      } else {
         throw new IllegalArgumentException("Input cannot be null or empty");
      }
   }

   public double asDecimal() {
      return this.value;
   }

   public String asPercentString() {
      return String.format("%.2f%%", this.value * 100.0);
   }

   public boolean rollChance() {
      return Numbers.randomDouble() < this.value;
   }

   @Nonnull
   @Override
   public JsonElement serialize() {
      return new JsonPrimitive(this.value);
   }

   public static Percent deserialize(@Nonnull JsonElement element) {
      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
         double decimalValue = element.getAsDouble();
         return new Percent(String.valueOf(decimalValue));
      } else {
         throw new IllegalArgumentException("Invalid JSON for Percent: must be a number.");
      }
   }

   @Override
   public String toString() {
      return this.asPercentString();
   }
}
