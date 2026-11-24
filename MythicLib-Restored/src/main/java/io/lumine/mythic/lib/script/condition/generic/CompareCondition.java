package io.lumine.mythic.lib.script.condition.generic;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.function.BiPredicate;

public class CompareCondition extends Condition {
   private final CompareCondition.Comparator comparator;
   private final DoubleFormula first;
   private final DoubleFormula second;
   private static final double SMALLEST_DIFFERENCE = 1.0E-7;

   public CompareCondition(ConfigObject config) {
      super(config);
      config.validateKeys("first", "comparator", "second");
      this.first = new DoubleFormula(config.getString("first"));
      this.second = new DoubleFormula(config.getString("second"));
      this.comparator = CompareCondition.Comparator.fromString(config.getString("comparator"));
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return this.comparator.test(this.first.evaluate(meta), this.second.evaluate(meta));
   }

   public enum Comparator {
      EQUALS("=", (d1, d2) -> Math.abs(d1 - d2) < 1.0E-7),
      LOWER("<=", (d1, d2) -> d1 < d2 + 1.0E-7),
      GREATER(">=", (d1, d2) -> d1 + 1.0E-7 > d2),
      STRICTLY_LOWER("<", (d1, d2) -> d1 < d2),
      STRICTLY_GREATER(">", (d1, d2) -> d1 > d2);

      private final String str;
      private final BiPredicate<Double, Double> predicate;

      Comparator(String str, BiPredicate<Double, Double> predicate) {
         this.str = str;
         this.predicate = predicate;
      }

      public boolean test(double d1, double d2) {
         return this.predicate.test(d1, d2);
      }

      @Override
      public String toString() {
         return this.str;
      }

      public static CompareCondition.Comparator fromString(String input) {
         for (CompareCondition.Comparator comp : values()) {
            if (comp.toString().equals(input) || comp.name().equals(input)) {
               return comp;
            }
         }

         throw new IllegalArgumentException("Could not read comparator from '" + input + "'");
      }
   }
}
