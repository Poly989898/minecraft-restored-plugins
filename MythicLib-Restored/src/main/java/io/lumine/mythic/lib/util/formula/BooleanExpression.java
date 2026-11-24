package io.lumine.mythic.lib.util.formula;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import io.lumine.mythic.lib.util.formula.preprocess.ExpressionPreprocessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanExpression<C> {
   private static final double EPSILON = 1.0E-10;
   private static final int EQUALITY_PRECEDENCE = 100;
   private static final int ORDER_PRECEDENCE = 300;
   private static final Operator LOGICAL_AND = new Operator("&&", 2, true, 1000) {
      @Override
      public double apply(double... args) {
         return args[0] != 0.0 && args[1] != 0.0 ? 1.0 : 0.0;
      }
   };
   private static final Operator LOGICAL_OR = new Operator("||", 2, true, 500) {
      @Override
      public double apply(double... args) {
         return args[0] == 0.0 && args[1] == 0.0 ? 0.0 : 1.0;
      }
   };
   private static final Operator LOGICAL_NOT = new Operator("!", 1, true, 500) {
      @Override
      public double apply(double... args) {
         return args[0] != 0.0 ? 0.0 : 1.0;
      }
   };
   private static final Operator LOGICAL_EQ = new Operator("==", 2, true, 100) {
      @Override
      public double apply(double... args) {
         return Math.abs(args[0] - args[1]) < 1.0E-10 ? 1.0 : 0.0;
      }
   };
   private static final Operator LOGICAL_NEQ = new Operator("!=", 2, true, 100) {
      @Override
      public double apply(double... args) {
         return Math.abs(args[0] - args[1]) > 1.0E-10 ? 1.0 : 0.0;
      }
   };
   private static final Operator LOWER_OR_EQUAL = new Operator("<=", 2, false, 300) {
      @Override
      public double apply(double... doubles) {
         double left = doubles[0];
         double right = doubles[1];
         return !(left <= right) && !(Math.abs(left - right) < 1.0E-10) ? 0.0 : 1.0;
      }
   };
   private static final Operator LOWER_THAN = new Operator("<", 2, false, 300) {
      @Override
      public double apply(double... doubles) {
         return doubles[0] < doubles[1] ? 1.0 : 0.0;
      }
   };
   private static final Operator GREATER_OR_EQUAL = new Operator(">=", 2, false, 300) {
      @Override
      public double apply(double... doubles) {
         double left = doubles[0];
         double right = doubles[1];
         return !(left >= right) && !(Math.abs(left - right) < 1.0E-10) ? 0.0 : 1.0;
      }
   };
   private static final Operator GREATER_THAN = new Operator(">", 2, false, 300) {
      @Override
      public double apply(double... doubles) {
         return doubles[0] > doubles[1] ? 1.0 : 0.0;
      }
   };
   private static final Operator[] OPERATORS = new Operator[]{
      LOGICAL_NOT, LOGICAL_OR, LOGICAL_AND, LOGICAL_EQ, LOGICAL_NEQ, LOWER_OR_EQUAL, LOWER_THAN, GREATER_OR_EQUAL, GREATER_THAN
   };
   private static final String[] CONSTANTS = new String[]{"true", "false"};
   @Nullable
   private final Expression precompiled;
   private final String expression;
   private final ExpressionPreprocessor<C> preprocessor;

   public BooleanExpression(@NotNull String expression, @NotNull ExpressionPreprocessor<C> preprocessor) {
      this.expression = expression;
      this.preprocessor = preprocessor;
      Expression expressionObject = null;

      try {
         expressionObject = this.decorateAndCompile(new ExpressionBuilder(preprocessor.preprocess(expression)));
      } catch (Exception exception) {
      }

      this.precompiled = expressionObject;
   }

   public boolean evaluate(@NotNull C context) {
      if (this.precompiled != null) {
         this.preprocessor.process(this.precompiled, context);
         return this.precompiled.evaluate() != 0.0;
      } else {
         return this.decorateAndCompile(new ExpressionBuilder(this.preprocessor.quickProcess(this.expression, context))).evaluate() != 0.0;
      }
   }

   @NotNull
   private Expression decorateAndCompile(@NotNull ExpressionBuilder builder) {
      return builder.implicitMultiplication(false).operator(OPERATORS).variables(CONSTANTS).build().setVariable("true", 1.0).setVariable("false", 0.0);
   }

   public static boolean eval(@NotNull String expression) {
      return new BooleanExpression<>(expression, ExpressionPreprocessor.EMPTY).evaluate(null);
   }
}
