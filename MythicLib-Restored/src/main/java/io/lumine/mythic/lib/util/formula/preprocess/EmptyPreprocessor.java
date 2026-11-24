package io.lumine.mythic.lib.util.formula.preprocess;

import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.NotNull;

public class EmptyPreprocessor implements ExpressionPreprocessor<Void> {
   @NotNull
   @Override
   public String preprocess(@NotNull String expression) {
      return expression;
   }

   public void process(@NotNull Expression expression, @NotNull Void context) {
   }

   @NotNull
   public String quickProcess(@NotNull String expression, @NotNull Void context) {
      return expression;
   }
}
