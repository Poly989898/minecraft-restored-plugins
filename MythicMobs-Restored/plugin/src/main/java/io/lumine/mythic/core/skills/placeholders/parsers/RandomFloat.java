package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class RandomFloat extends PlaceholderFloatImpl {
   private io.lumine.mythic.bukkit.utils.numbers.RandomFloat value;

   public RandomFloat(String string) {
      this.strValue = string;
      this.strUnparsedValue = string;
      this.value = new io.lumine.mythic.bukkit.utils.numbers.RandomFloat(string);
   }

   public RandomFloat(io.lumine.mythic.bukkit.utils.numbers.RandomFloat amount) {
      this.value = amount;
   }

   @Override
   public float get() {
      return this.value.get();
   }

   @Override
   public float get(PlaceholderMeta meta) {
      return this.value.get();
   }

   @Override
   public float get(AbstractEntity entity) {
      return this.value.get();
   }

   @Override
   public float get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value.get();
   }

   @Override
   public float get(SkillCaster caster) {
      return this.value.get();
   }

   @Override
   public float get(PlaceholderContext context) {
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
