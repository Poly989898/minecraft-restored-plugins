package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.skills.placeholders.IGenericPlaceholder;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderParser;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.math.MathExpressions;

public abstract class PlaceholderNumber<T> extends GenericPlaceholderParser<T> implements IGenericPlaceholder<T> {
   private final MathExpressions.NoVarMemo mathMemo = new MathExpressions.NoVarMemo();

   public PlaceholderNumber(String string) {
      super(string);
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   protected abstract T convertNumberToSelfType(double value);

   protected abstract T convertStringToSelfType(String str);

   protected abstract T getDefaultValue();

   @Override
   protected T handleParsing(String string) {
      if (this.doMath) {
         try {
            return this.convertNumberToSelfType(this.mathMemo.evaluate(string));
         } catch (Exception e) {
            MythicLogger.error("Invalid math operation on PlaceholderInt: " + this.strValue);
            e.printStackTrace();
         }
      }

      return this.convertStringToSelfType(string);
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public boolean isStaticallyEqualTo(int value) {
      return this.isStatic() && this.get(PlaceholderContext.of()) == this.convertNumberToSelfType(value);
   }
}
