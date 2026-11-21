package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class StaticBoolean extends PlaceholderBooleanImpl {
   private boolean value;

   public StaticBoolean(String string) {
      if (!string.equalsIgnoreCase("true") && !string.equals("1")) {
         this.value = false;
      } else {
         this.value = true;
      }
   }

   @Override
   public boolean get() {
      return this.value;
   }

   @Override
   public boolean get(PlaceholderMeta meta) {
      return this.value;
   }

   @Override
   public boolean get(AbstractEntity entity) {
      return this.value;
   }

   @Override
   public boolean get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   @Override
   public boolean get(SkillCaster caster) {
      return this.value;
   }

   @Override
   public boolean get(PlaceholderContext context) {
      return this.value;
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }

   @Override
   public String serialize() {
      return String.valueOf(this.value);
   }

   @Override
   public boolean isStatic() {
      return true;
   }

   @Override
   public boolean isStaticallyEqualTo(Boolean value) {
      return this.value == value;
   }
}
