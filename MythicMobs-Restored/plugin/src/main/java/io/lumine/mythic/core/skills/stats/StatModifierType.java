package io.lumine.mythic.core.skills.stats;

import lombok.Getter;


@Getter
public enum StatModifierType {
   ADDITIVE("Additive"),
   ADDITIVE_MULTIPLIER("Multiply"),
   COMPOUND_MULTIPLIER("Compound"),
   SETTER("Set");

   private final String display;

   private StatModifierType(String display, String... aliases) {
      this.display = display;
   }

   public static StatModifierType get(String input) {
      switch (input) {
         case "0":
         case "ADD":
         case "ADD_NUMBER":
         case "ADDITIVE":
            return ADDITIVE;
         case "1":
         case "MULTIPLY":
         case "MULTIPLY_BASE":
         case "MULTIPLY_ADD":
         case "MULTIPLY_ADDITIVE":
         case "ADD_SCALAR":
         case "ADD_MULTIPLY":
         case "ADD_MULTIPLIER":
         case "ADDITIVE_MULTIPLY":
         case "ADDITIVE_MULTIPLIER":
            return ADDITIVE_MULTIPLIER;
         case "2":
         case "COMPOUND":
         case "MULTIPLY_SCALAR":
         case "MULTIPLY_COMPOUND":
         case "COMPOUND_MULTIPLY":
         case "COMPOUND_MULTIPLIER":
            return COMPOUND_MULTIPLIER;
         case "3":
         case "SET":
         case "SETTER":
         case "OVERRIDE":
            return SETTER;
         default:
            return null;
      }
   }
}
