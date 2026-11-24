package io.lumine.mythic.lib.util.formula.preprocess;

import io.lumine.mythic.lib.UtilityMethods;
import net.objecthunter.exp4j.Expression;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

@Deprecated
public abstract class PlaceholderPreprocessor<T> implements ExpressionPreprocessor<T> {
   private final Pattern placeholderPattern;
   private static final char PAPI = '%';
   private Map<String, String> internal = new HashMap<>();
   private Map<String, String> papi = new HashMap<>();
   private int variableCount;

   public PlaceholderPreprocessor(char start, char end) {
      this.placeholderPattern = UtilityMethods.internalPlaceholderPattern(start, end);
   }

   private String newVariableName() {
      return "var_" + this.variableCount++;
   }

   @NotNull
   @Override
   public String preprocess(@NotNull String expression) {
      return expression;
   }

   @Override
   public void process(@NotNull Expression expression, @NotNull T context) {
   }
}
