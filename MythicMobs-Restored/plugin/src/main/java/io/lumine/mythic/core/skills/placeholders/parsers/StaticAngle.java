package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.utils.numbers.Angle;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class StaticAngle extends PlaceholderAngleImpl {
   private Angle value;

   public StaticAngle(String string, AngleUnit defaultUnit) {
      this.value = Angle.parse(string, defaultUnit);
   }

   public StaticAngle(Angle amount) {
      this.value = amount;
   }

   public Angle get() {
      return this.value;
   }

   @Override
   public Angle get(PlaceholderMeta meta) {
      return this.value;
   }

   @Override
   public Angle get(AbstractEntity entity) {
      return this.value;
   }

   public Angle get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   @Override
   public Angle get(SkillCaster caster) {
      return this.value;
   }

   public Angle get(PlaceholderContext context) {
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
   public boolean isStaticallyEqualTo(Angle value) {
      return this.value.equals(value);
   }
}
