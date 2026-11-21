package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.math.MathExpressions;

public class PlaceholderIntImpl extends PlaceholderParser implements PlaceholderInt {
   private final MathExpressions.NoVarMemo mathMemo = new MathExpressions.NoVarMemo();
   private static int num = 0;

   public PlaceholderIntImpl() {
   }

   public PlaceholderIntImpl(String string) {
      super(string);
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   @Override
   public int get() {
      return this.get(PlaceholderContext.of());
   }

   /** @deprecated */
   @Override
   public int get(PlaceholderMeta meta) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).build());
   }

   /** @deprecated */
   @Override
   public int get(AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().entity(entity).build());
   }

   /** @deprecated */
   @Override
   public int get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).entity(entity).build());
   }

   /** @deprecated */
   @Override
   public int get(SkillCaster caster) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().caster(caster).build());
   }

   /** @deprecated */
   public int get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).item(itemStack).build());
   }

   /** @deprecated */
   @Override
   public int get(MythicSpawner spawner) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().spawner(spawner).build());
   }

   @Override
   public int get(PlaceholderContext context) {
      String string = this.parse(context);
      return this.handleParsing(string);
   }

   private int handleParsing(String string) {
      if (this.doMath) {
         try {
            return (int)this.mathMemo.evaluate(string);
         } catch (Exception e) {
            MythicLogger.error("Invalid math operation on PlaceholderInt: " + this.strValue);
            e.printStackTrace();
         }
      }

      try {
         return string.contains(".") ? Numbers.floor(Double.parseDouble(string)) : Integer.parseInt(string);
      } catch (NumberFormatException ex) {
         return 1;
      }
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public static PlaceholderIntImpl of(String string) {
      try {
         if (string == null) {
            return null;
         } else if (io.lumine.mythic.bukkit.utils.numbers.RandomInt.matchesRange(string)) {
            return new RandomInt(string);
         } else if (string.matches("-?\\d+")) {
            return new StaticInt(string);
         } else if (!MythicBukkit.isVolatile()) {
            MythicLogger.error(
               "Failed to parse Placeholder '"
                  + string
                  + "': Math and variables in numeric values require premium! Consider getting MythicMobs Premium at www.mythiccraft.io"
            );
            return new StaticInt("1");
         } else {
            return new PlaceholderIntImpl(string);
         }
      } catch (Exception e) {
         e.printStackTrace();
         MythicLogger.error("Failed to parse Placeholder '" + string + "': invalid syntax.");
         return new StaticInt("1");
      }
   }

   @Override
   public boolean isStaticallyEqualTo(int value) {
      return this.isStatic() && this.get() == value;
   }
}
