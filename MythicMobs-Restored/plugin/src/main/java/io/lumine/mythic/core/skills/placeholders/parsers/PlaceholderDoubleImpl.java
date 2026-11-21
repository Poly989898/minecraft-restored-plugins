package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.utils.math.Functions;
import io.lumine.mythic.core.utils.math.MathExpressions;
import io.lumine.mythic.core.utils.math.Operators;
import java.util.Map;
import java.util.Map.Entry;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class PlaceholderDoubleImpl extends PlaceholderParser implements PlaceholderDouble {
   private final MathExpressions.NoVarMemo mathMemo = new MathExpressions.NoVarMemo();

   public PlaceholderDoubleImpl() {
   }

   public PlaceholderDoubleImpl(String string) {
      super(string);
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   @Override
   public double get() {
      return this.get(PlaceholderContext.of());
   }

   /** @deprecated */
   @Override
   public double get(PlaceholderMeta meta) {
      return this.get(meta, meta == null ? null : meta.getTrigger());
   }

   /** @deprecated */
   @Override
   public double get(AbstractEntity entity) {
      return this.get(null, entity);
   }

   /** @deprecated */
   @Override
   public double get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).entity(entity).build());
   }

   /** @deprecated */
   public double get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).item(itemStack).build());
   }

   /** @deprecated */
   @Override
   public double get(SkillCaster caster) {
      return this.get(new GenericPlaceholderMeta(caster, caster.getEntity()));
   }

   /** @deprecated */
   public double get(PlaceholderMeta meta, Map<String, Double> extraVariables) {
      return this.get(meta, meta == null ? null : meta.getTrigger(), extraVariables);
   }

   /** @deprecated */
   public double get(AbstractEntity entity, Map<String, Double> extraVariables) {
      return this.get(null, entity, extraVariables);
   }

   /** @deprecated */
   public double get(PlaceholderMeta meta, AbstractEntity entity, Map<String, Double> extraVariables) {
      return this.isStatic()
         ? this.get(PlaceholderContext.of(), extraVariables)
         : this.get(PlaceholderContext.builder().meta(meta).entity(entity).build(), extraVariables);
   }

   /** @deprecated */
   public double get(SkillCaster caster, Map<String, Double> extraVariables) {
      return this.get(new GenericPlaceholderMeta(caster, caster.getEntity()), extraVariables);
   }

   @Override
   public double get(PlaceholderContext context) {
      return this.get(context, Map.of());
   }

   public double get(PlaceholderContext context, Map<String, Double> extraVariables) {
      String string = this.parse(context);
      return this.handleParsing(string, extraVariables);
   }

   private double handleParsing(String string, Map<String, Double> extraVariables) {
      if (this.doMath) {
         try {
            if (extraVariables.isEmpty()) {
               return this.mathMemo.evaluate(string);
            }

            ExpressionBuilder expressionBuilder = new ExpressionBuilder(string)
               .operator(Operators.operators)
               .functions(Functions.functions)
               .variables(extraVariables.keySet());
            Expression expression = expressionBuilder.build();

            for (Entry<String, Double> entry : extraVariables.entrySet()) {
               String key = entry.getKey();
               Double value = entry.getValue();
               expression.setVariable(key, value);
            }

            return expression.evaluate();
         } catch (Exception e) {
            MythicLogger.error("Invalid math operation on PlaceholderDouble: " + this.strValue);
            MythicLogger.handleMinorError(e);
         }
      }

      try {
         return Double.parseDouble(string);
      } catch (NumberFormatException ex) {
         return 1.0;
      }
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public static PlaceholderDoubleImpl of(String string) {
      try {
         if (string == null) {
            return null;
         } else if (io.lumine.mythic.bukkit.utils.numbers.RandomDouble.matchesRange(string)) {
            return new RandomDouble(string);
         } else if (Numbers.parseDouble(string).isPresent()) {
            return new StaticDouble(string);
         } else if (!MythicBukkit.isVolatile()) {
            MythicLogger.error(
               "Failed to parse Placeholder '"
                  + string
                  + "': Math and variables in numeric values require premium! Consider getting MythicMobs Premium at www.mythiccraft.io"
            );
            return new StaticDouble("1");
         } else {
            return new PlaceholderDoubleImpl(string);
         }
      } catch (Exception e) {
         MythicLogger.error("Failed to parse Placeholder '" + string + "': invalid syntax.");
         return new StaticDouble("1");
      }
   }

   @Override
   public boolean isStaticallyEqualTo(double value) {
      return this.isStatic() && this.get() == value;
   }
}
