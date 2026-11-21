package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Angle;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderParser;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.math.MathExpressions;
import java.util.regex.Pattern;

public class PlaceholderAngleImpl extends GenericPlaceholderParser<Angle> implements PlaceholderAngle {
   private final MathExpressions.NoVarMemo mathMemo = new MathExpressions.NoVarMemo();
   private static final Pattern FIRST_UNIT = Pattern.compile("(-?\\d+(?:\\.\\d+)?)([a-zA-Z\u00b0]+)");
   private static final Pattern NUMBER_LITERAL = Pattern.compile("(?<![\\w.])(-?\\d+(?:\\.\\d+)?)(?![\\w.])");
   private AngleUnit defaultUnit = AngleUnit.DEGREES;

   public PlaceholderAngleImpl() {
      super("");
   }

   public PlaceholderAngleImpl(String string, AngleUnit defaultUnit) {
      super(string);
      this.defaultUnit = defaultUnit;
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   /** @deprecated */
   public Angle get(PlaceholderMeta meta) {
      return this.get(meta, meta == null ? null : meta.getTrigger());
   }

   /** @deprecated */
   public Angle get(AbstractEntity entity) {
      return this.get(null, entity);
   }

   protected Angle handleParsing(String input) {
      if (input != null && !input.isBlank()) {
         String s = input.trim();
         AngleUnit unit = this.defaultUnit;

         forEachLoop:
         for (AngleUnit u : AngleUnit.values()) {
            for (String suf : u.suffixes()) {
               if (s.toLowerCase().endsWith(suf.toLowerCase())) {
                  unit = u;
                  s = s.substring(0, s.length() - suf.length()).trim();
                  break forEachLoop;
               }
            }
         }

         double numeric;
         try {
            if (this.doMath) {
               numeric = this.mathMemo.evaluate(s);
            } else {
               numeric = Double.parseDouble(s);
            }
         } catch (Exception e) {
            MythicLogger.error("Invalid angle expression: " + this.strValue);
            MythicLogger.handleMinorError(e);
            return new Angle(0.0, unit);
         }

         return new Angle(numeric, unit);
      } else {
         MythicLogger.error("Empty angle string: " + this.strValue);
         return new Angle(0.0, this.defaultUnit);
      }
   }

   /** @deprecated */
   public Angle get(SkillCaster caster) {
      return this.get(new GenericPlaceholderMeta(caster, caster.getEntity()));
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public static PlaceholderAngleImpl of(String string, AngleUnit defaultUnit) {
      try {
         if (string == null) {
            return null;
         } else if (io.lumine.mythic.bukkit.utils.numbers.RandomAngle.matchesRange(string)) {
            return new RandomAngle(string, defaultUnit);
         } else if (Angle.isAngle(string)) {
            return new StaticAngle(string, defaultUnit);
         } else if (!MythicBukkit.isVolatile()) {
            MythicLogger.error(
               "Failed to parse Placeholder '"
                  + string
                  + "': Math and variables in numeric values require premium! Consider getting MythicMobs Premium at www.mythiccraft.io"
            );
            return new StaticAngle("0", AngleUnit.DEGREES);
         } else {
            return new PlaceholderAngleImpl(string, defaultUnit);
         }
      } catch (Exception e) {
         MythicLogger.error("Failed to parse Placeholder '" + string + "': invalid syntax.");
         return new StaticAngle("0", AngleUnit.DEGREES);
      }
   }

   public boolean isStaticallyEqualTo(Angle value) {
      return this.isStatic() && this.get().equals(value);
   }
}
