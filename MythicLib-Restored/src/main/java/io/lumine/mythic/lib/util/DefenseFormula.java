package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.util.formula.NumericalExpression;
import java.util.logging.Level;

public class DefenseFormula {
   @Deprecated
   private final boolean elemental;

   @Deprecated
   public DefenseFormula() {
      this(false);
   }

   @Deprecated
   public DefenseFormula(boolean elemental) {
      this.elemental = elemental;
   }

   @Deprecated
   public double getAppliedDamage(double defense, double damage) {
      return calculateDamage(this.elemental, defense, damage);
   }

   public static double calculateDamage(boolean elemental, double defense, double damage) {
      String formula = elemental ? MythicLib.plugin.getMMOConfig().elementalDefenseFormula : MythicLib.plugin.getMMOConfig().naturalDefenseFormula;
      String expression = formula.replace("#defense#", String.valueOf(defense)).replace("#damage#", String.valueOf(damage));

      try {
         return Math.max(0.0, NumericalExpression.eval(expression));
      } catch (Exception exception) {
         MythicLib.inst().getLogger().log(Level.WARNING, "Could not evaluate defense formula (please check config): " + exception.getMessage());
         return damage;
      }
   }
}
