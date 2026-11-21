package io.lumine.mythic.core.skills.variables.interfaces;

import io.lumine.mythic.core.utils.math.MathExpressions;

public interface NumericVariable {
   void executeMath(String str);

   default double parseMath(String eq, double val) {
      return MathExpressions.instantiateCached(eq, "x").setVariable("x", val).evaluate();
   }
}
