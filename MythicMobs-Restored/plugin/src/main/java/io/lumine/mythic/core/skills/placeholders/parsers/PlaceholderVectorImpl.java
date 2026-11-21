package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderParser;
import io.lumine.mythic.core.utils.math.MathExpressions;

public final class PlaceholderVectorImpl extends GenericPlaceholderParser<AbstractVector> implements PlaceholderVector {
   private final MathExpressions.NoVarMemo mathMemoX = new MathExpressions.NoVarMemo();
   private final MathExpressions.NoVarMemo mathMemoY = new MathExpressions.NoVarMemo();
   private final MathExpressions.NoVarMemo mathMemoZ = new MathExpressions.NoVarMemo();

   public static PlaceholderVectorImpl of(String input) {
      if (input == null) {
         return null;
      } else {
         PlaceholderVectorImpl newPlaceholder = new PlaceholderVectorImpl(input);
         if (newPlaceholder.isDoMath() && !MythicBukkit.isVolatile()) {
            MythicLogger.error("Math and variables in numeric values require premium! Consider getting MythicMobs Premium at www.mythiccraft.io");
            newPlaceholder.setDoMath(false);
         }

         return newPlaceholder;
      }
   }

   public PlaceholderVectorImpl(String string) {
      super(string);
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   public boolean isStaticallyEqualTo(AbstractVector value) {
      return this.isStatic() && this.get().equals(value);
   }

   protected AbstractVector handleParsing(String string) {
      try {
         String[] split = string.split(",");
         double dx;
         double dy;
         double dz;
         if (this.doMath) {
            dx = this.mathMemoX.evaluate(split[0]);
            dy = this.mathMemoY.evaluate(split[1]);
            dz = this.mathMemoZ.evaluate(split[2]);
         } else {
            dx = Double.parseDouble(split[0]);
            dy = Double.parseDouble(split[1]);
            dz = Double.parseDouble(split[2]);
         }

         return new AbstractVector(dx, dy, dz);
      } catch (Error | Exception e) {
         MythicLogger.error(this.strValue + " is not a valid vector. Was parsed as '" + string + "'.");
         MythicLogger.handleMinorError(e);
         return null;
      }
   }
}
