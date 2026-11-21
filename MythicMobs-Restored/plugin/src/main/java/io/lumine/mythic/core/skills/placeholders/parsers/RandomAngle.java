package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.utils.numbers.Angle;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class RandomAngle extends PlaceholderAngleImpl {
   private io.lumine.mythic.bukkit.utils.numbers.RandomAngle value;

   public RandomAngle(String string, AngleUnit defaultUnit) {
      this.strValue = string;
      this.strUnparsedValue = string;
      this.value = new io.lumine.mythic.bukkit.utils.numbers.RandomAngle(string, defaultUnit);
   }

   public RandomAngle(io.lumine.mythic.bukkit.utils.numbers.RandomAngle amount) {
      this.value = amount;
   }

   public Angle get() {
      return this.value.get();
   }

   @Override
   public Angle get(PlaceholderMeta meta) {
      return this.value.get();
   }

   @Override
   public Angle get(AbstractEntity entity) {
      return this.value.get();
   }

   public Angle get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value.get();
   }

   @Override
   public Angle get(SkillCaster caster) {
      return this.value.get();
   }

   @Override
   public String toString() {
      return this.value.toString();
   }

   @Override
   public String serialize() {
      return this.toString();
   }
}
