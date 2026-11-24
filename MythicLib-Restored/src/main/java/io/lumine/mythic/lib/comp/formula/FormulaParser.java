package io.lumine.mythic.lib.comp.formula;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.util.formula.BooleanExpression;
import io.lumine.mythic.lib.util.formula.NumericalExpression;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class FormulaParser {
   private static FormulaParser instance;

   private FormulaParser() {
   }

   public static FormulaParser getInstance() {
      if (instance == null) {
         instance = new FormulaParser();
      }

      return instance;
   }

   @Deprecated
   @NotNull
   public Object eval(@NotNull String str) {
      try {
         return NumericalExpression.eval(str);
      } catch (Exception ignored) {
         try {
            return BooleanExpression.eval(str);
         } catch (Exception exception) {
            throw new RuntimeException("could not evaluate string '" + exception.getMessage() + "'", exception);
         }
      }
   }

   @Deprecated
   @NotNull
   public Object evaluate(@NotNull String str) {
      return this.eval(str);
   }

   @Deprecated
   @NotNull
   public Object eval(@Nullable OfflinePlayer player, @NotNull String str) {
      return this.evaluate(MythicLib.plugin.getPlaceholderParser().parse(player, str));
   }

   @Deprecated
   public double evaluateAsDouble(@NotNull OfflinePlayer player, @NotNull String str) {
      return this.evaluateAsDouble(MythicLib.plugin.getPlaceholderParser().parse(player, str));
   }

   @Deprecated
   public double evaluateAsDouble(@NotNull String str) {
      return this.evaluateAs(str, Number.class).doubleValue();
   }

   @Deprecated
   public int evaluateAsInt(@NotNull String str) {
      return this.evaluateAs(str, Number.class).intValue();
   }

   @Deprecated
   public long evaluateAsLong(@NotNull String str) {
      return this.evaluateAs(str, Number.class).longValue();
   }

   @Deprecated
   @NotNull
   public <O> O evaluateAs(@NotNull String str, @NotNull Class<O> targetClass) {
      Object result = this.evaluate(str);
      if (!targetClass.isInstance(result)) {
         throw new IllegalArgumentException("Expression did not return a " + targetClass.getSimpleName());
      } else {
         return (O)result;
      }
   }
}
