package io.lumine.mythic.lib.util.formula;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import io.lumine.mythic.lib.util.formula.preprocess.ExpressionPreprocessor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NumericalExpression<C> {
   private static final Function RANDOM_DOUBLE = new Function("random", 0) {
      @Override
      public double apply(double... doubles) {
         return NumericalExpression.RANDOM.nextDouble();
      }
   };
   private static final Function ATAN2 = new Function("atan2", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.atan2(doubles[0], doubles[1]);
      }
   };
   private static final Function POW = new Function("pow", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.pow(doubles[0], doubles[1]);
      }
   };
   private static final Function MIN = new Function("min", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.min(doubles[0], doubles[1]);
      }
   };
   private static final Function MAX = new Function("max", 2) {
      @Override
      public double apply(double... doubles) {
         return Math.max(doubles[0], doubles[1]);
      }
   };
   private static final Function NON_ZERO = new Function("non_zero", 2) {
      @Override
      public double apply(double... doubles) {
         return doubles[0] == 0.0 ? doubles[1] : doubles[0];
      }
   };
   private static final Function[] FUNCTIONS = new Function[]{RANDOM_DOUBLE, ATAN2, POW, MIN, MAX, NON_ZERO};
   private static final Random RANDOM = new Random();
   private static final Map<String, Double> CONSTANTS;
   @Nullable
   private final Expression precompiled;
   private final String expression;
   private final ExpressionPreprocessor<C> preprocessor;

   public NumericalExpression(@NotNull String expression, @NotNull ExpressionPreprocessor<C> preprocessor) {
      this.expression = expression;
      this.preprocessor = preprocessor;
      Expression expressionObject = null;

      try {
         expressionObject = this.decorateAndCompile(new ExpressionBuilder(preprocessor.preprocess(expression)));
      } catch (Exception exception) {
      }

      this.precompiled = expressionObject;
   }

   public double evaluate(@NotNull C context) {
      if (this.precompiled != null) {
         this.preprocessor.process(this.precompiled, context);
         return this.precompiled.evaluate();
      } else {
         return this.decorateAndCompile(new ExpressionBuilder(this.preprocessor.quickProcess(this.expression, context))).evaluate();
      }
   }

   @NotNull
   private Expression decorateAndCompile(@NotNull ExpressionBuilder builder) {
      return builder.implicitMultiplication(false).functions(FUNCTIONS).variables(CONSTANTS.keySet()).build().setVariables(CONSTANTS);
   }

   public static double eval(@NotNull String expression) {
      return new NumericalExpression<>(expression, ExpressionPreprocessor.EMPTY).evaluate(null);
   }

   static {
      Map<String, Double> constants = new HashMap<>();
      constants.put("PI", Math.PI);
      constants.put("Pi", Math.PI);
      double phi = 0.5 * (1.0 + Math.sqrt(5.0));
      constants.put("phi", phi);
      constants.put("Phi", phi);
      constants.put("PHI", phi);
      CONSTANTS = Collections.unmodifiableMap(constants);
   }
}
