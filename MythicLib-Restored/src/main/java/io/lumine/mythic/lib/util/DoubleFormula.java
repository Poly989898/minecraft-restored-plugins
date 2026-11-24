package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.formula.NumericalExpression;
import java.util.logging.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleFormula {
   @Nullable
   private final String value;
   @Nullable
   private final Double constant;
   public static final DoubleFormula ZERO = constant(0.0);

   public DoubleFormula(@NotNull String inputFormula) {
      String value = null;
      Double constant = null;

      try {
         constant = Double.valueOf(inputFormula);
      } catch (IllegalArgumentException exception) {
         value = inputFormula;
      }

      this.value = value;
      this.constant = constant;
   }

   public DoubleFormula(double trivialValue) {
      this.value = null;
      this.constant = trivialValue;
   }

   public double evaluate(@NotNull SkillMetadata meta) {
      if (this.constant != null) {
         return this.constant;
      }

      try {
         return NumericalExpression.eval(meta.parseString(this.value));
      } catch (Exception exception) {
         MythicLib.plugin
            .getLogger()
            .log(
               Level.WARNING,
               "Could not evaluate '" + this.value + "' while casting skill '" + meta.getCast().getHandler().getId() + "': " + exception.getMessage()
            );
         return 0.0;
      }
   }

   public static DoubleFormula constant(double value) {
      return new DoubleFormula(value);
   }
}
