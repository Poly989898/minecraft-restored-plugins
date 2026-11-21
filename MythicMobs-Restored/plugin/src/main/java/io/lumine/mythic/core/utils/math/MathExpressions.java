package io.lumine.mythic.core.utils.math;

import java.util.concurrent.ConcurrentHashMap;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public final class MathExpressions {
   private static final int TEMPLATE_CACHE_LIMIT = 512;
   private static final ConcurrentHashMap<String, Expression> TEMPLATES = new ConcurrentHashMap<>();

   private MathExpressions() {
   }

   private static ExpressionBuilder base(String string) {
      return new ExpressionBuilder(string).operator(Operators.operators).functions(Functions.functions);
   }

   public static Expression compileTemplate(String string, String... variables) {
      return base(string).variables(variables).build();
   }

   public static Expression instantiate(Expression template) {
      return new Expression(template);
   }

   public static Expression instantiateCached(String string, String... variables) {
      String key = string + String.join(",", variables);
      Expression template = TEMPLATES.get(key);
      if (template == null) {
         template = compileTemplate(string, variables);
         if (TEMPLATES.size() < 512) {
            Expression prev = TEMPLATES.putIfAbsent(key, template);
            if (prev != null) {
               template = prev;
            }
         }
      }

      return instantiate(template);
   }

   public static final class NoVarMemo {
      private volatile MathExpressions.NoVarMemo.Entry last;

      public double evaluate(String string) {
         MathExpressions.NoVarMemo.Entry e = this.last;
         if (e == null || !e.string().equals(string)) {
            e = new MathExpressions.NoVarMemo.Entry(string, MathExpressions.base(string).build());
            this.last = e;
         }

         return e.expression().evaluate();
      }

      private record Entry(String string, Expression expression) {
      }
   }

   public static final class TemplateMemo {
      private volatile MathExpressions.TemplateMemo.Entry last;

      public Expression instantiate(String string, String... variables) {
         MathExpressions.TemplateMemo.Entry e = this.last;
         if (e == null || !e.string().equals(string)) {
            e = new MathExpressions.TemplateMemo.Entry(string, MathExpressions.compileTemplate(string, variables));
            this.last = e;
         }

         return MathExpressions.instantiate(e.template());
      }

      private record Entry(String string, Expression template) {
      }
   }
}
