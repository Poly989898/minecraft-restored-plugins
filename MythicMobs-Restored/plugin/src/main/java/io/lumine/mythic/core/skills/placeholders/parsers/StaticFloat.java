package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class StaticFloat extends PlaceholderFloatImpl {
   private float value;

   public StaticFloat(String string) {
      this.value = Float.valueOf(string);
   }

   public StaticFloat(float amount) {
      this.value = amount;
   }

   @Override
   public float get() {
      return this.value;
   }

   @Override
   public float get(PlaceholderMeta meta) {
      return this.value;
   }

   @Override
   public float get(AbstractEntity entity) {
      return this.value;
   }

   @Override
   public float get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   @Override
   public float get(SkillCaster caster) {
      return this.value;
   }

   @Override
   public float get(PlaceholderContext context) {
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
   public boolean isStaticallyEqualTo(float value) {
      return this.value == value;
   }
}
