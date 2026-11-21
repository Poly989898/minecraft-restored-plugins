package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public class RandomInt extends PlaceholderIntImpl {
   private io.lumine.mythic.bukkit.utils.numbers.RandomInt value;

   public RandomInt(String string) {
      this.strValue = string;
      this.strUnparsedValue = string;
      this.value = new io.lumine.mythic.bukkit.utils.numbers.RandomInt(string);
   }

   @Override
   public int get() {
      return this.value.get();
   }

   @Override
   public int get(PlaceholderMeta meta) {
      return this.value.get();
   }

   @Override
   public int get(AbstractEntity entity) {
      return this.value.get();
   }

   @Override
   public int get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value.get();
   }

   @Override
   public int get(SkillCaster caster) {
      return this.value.get();
   }

   @Override
   public int get(MythicSpawner caster) {
      return this.value.get();
   }

   @Override
   public int get(PlaceholderContext context) {
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
