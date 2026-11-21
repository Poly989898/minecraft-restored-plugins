package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.utils.math.MathExpressions;

public class PlaceholderFloatImpl extends PlaceholderParser implements PlaceholderFloat {
   private final MathExpressions.NoVarMemo mathMemo = new MathExpressions.NoVarMemo();

   public PlaceholderFloatImpl() {
   }

   public PlaceholderFloatImpl(String string) {
      super(string);
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   @Override
   public float get() {
      return this.get(PlaceholderContext.of());
   }

   /** @deprecated */
   @Override
   public float get(PlaceholderMeta meta) {
      return this.get(meta, meta == null ? null : meta.getTrigger());
   }

   /** @deprecated */
   @Override
   public float get(AbstractEntity entity) {
      return this.get(null, entity);
   }

   /** @deprecated */
   @Override
   public float get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).entity(entity).build());
   }

   /** @deprecated */
   public float get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).item(itemStack).build());
   }

   /** @deprecated */
   @Override
   public float get(SkillCaster caster) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().caster(caster).build());
   }

   @Override
   public float get(PlaceholderContext context) {
      String string = this.parse(context);
      return this.handleParsing(string);
   }

   private float handleParsing(String string) {
      if (this.doMath) {
         try {
            return (float)this.mathMemo.evaluate(string);
         } catch (Exception e) {
            MythicLogger.error("Invalid math operation on PlaceholderFloat: " + this.strValue);
            MythicLogger.handleMinorError(e);
         }
      }

      try {
         return Float.parseFloat(string);
      } catch (NumberFormatException ex) {
         return 1.0F;
      }
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public static PlaceholderFloatImpl of(String string) {
      try {
         if (string == null) {
            return null;
         } else if (io.lumine.mythic.bukkit.utils.numbers.RandomFloat.matches(string)) {
            return new RandomFloat(string);
         } else if (string.matches("-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)")) {
            return new StaticFloat(string);
         } else if (!MythicBukkit.isVolatile()) {
            MythicLogger.error(
               "Failed to parse Placeholder '"
                  + string
                  + "': Math and variables in numeric values require premium! Consider getting MythicMobs Premium at www.mythiccraft.io"
            );
            return new StaticFloat("1");
         } else {
            return new PlaceholderFloatImpl(string);
         }
      } catch (Exception e) {
         MythicLogger.error("Failed to parse Placeholder '" + string + "': invalid syntax.");
         return new StaticFloat("1");
      }
   }

   @Override
   public boolean isStaticallyEqualTo(float value) {
      return this.isStatic() && this.get() == value;
   }
}
