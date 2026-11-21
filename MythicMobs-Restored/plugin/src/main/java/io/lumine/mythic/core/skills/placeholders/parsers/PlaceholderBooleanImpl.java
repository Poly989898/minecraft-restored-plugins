package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.math.MathExpressions;

public class PlaceholderBooleanImpl extends PlaceholderParser implements PlaceholderBoolean {
   private final MathExpressions.NoVarMemo mathMemo = new MathExpressions.NoVarMemo();

   public PlaceholderBooleanImpl() {
   }

   public PlaceholderBooleanImpl(String string) {
      super(string);
      if (string != null && (string.startsWith("=") || string.contains("(") && string.contains(")"))) {
         this.doMath = true;
      }
   }

   @Override
   public boolean get() {
      return this.get(PlaceholderContext.of());
   }

   @Deprecated
   @Override
   public boolean get(PlaceholderMeta meta) {
      return this.get(meta, meta.getTrigger());
   }

   @Deprecated
   @Override
   public boolean get(AbstractEntity entity) {
      return this.get(null, entity);
   }

   @Deprecated
   @Override
   public boolean get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).entity(entity).build());
   }

   @Deprecated
   @Override
   public boolean get(SkillCaster caster) {
      return this.get(new GenericPlaceholderMeta(caster, caster.getEntity()));
   }

   @Deprecated
   public boolean get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).item(itemStack).build());
   }

   @Deprecated
   @Override
   public boolean get(MythicSpawner spawner) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().spawner(spawner).build());
   }

   @Override
   public boolean get(PlaceholderContext context) {
      String string = this.parse(context);
      return this.handleParsing(string);
   }

   private boolean handleParsing(String string) {
      if (string == null) {
         return false;
      } else {
         if (this.doMath) {
            try {
               string = String.valueOf((int)this.mathMemo.evaluate(string));
            } catch (Exception e) {
               MythicLogger.error("Invalid math operation on PlaceholderBoolean: " + this.strValue);
               e.printStackTrace();
            }
         }

         return string.equalsIgnoreCase("true") || string.equals("1") || string.equalsIgnoreCase("yes");
      }
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public static PlaceholderBooleanImpl of(String string) {
      try {
         if (string == null) {
            return null;
         } else if (string.matches("true|false|0|1")) {
            return new StaticBoolean(string);
         } else if (!MythicBukkit.isVolatile()) {
            MythicLogger.error(
               "Failed to parse Placeholder '" + string + "': Math and variables require premium! Consider getting MythicMobs Premium at www.mythiccraft.io"
            );
            return new StaticBoolean("0");
         } else {
            return new PlaceholderBooleanImpl(string);
         }
      } catch (Exception e) {
         e.printStackTrace();
         MythicLogger.error("Failed to parse Placeholder '" + string + "': invalid syntax.");
         return new StaticBoolean("0");
      }
   }

   @Override
   public boolean isStaticallyEqualTo(Boolean value) {
      return this.isStatic() && this.get(PlaceholderContext.of()) == value;
   }
}
