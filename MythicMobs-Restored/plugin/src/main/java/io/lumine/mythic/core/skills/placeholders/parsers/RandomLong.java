package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public class RandomLong extends PlaceholderLongImpl {
   private final io.lumine.mythic.bukkit.utils.numbers.RandomLong value;

   public RandomLong(String string) {
      super(string);
      this.strValue = string;
      this.strUnparsedValue = string;
      this.value = new io.lumine.mythic.bukkit.utils.numbers.RandomLong(string);
   }

   public Long get() {
      return this.value.get();
   }

   public Long get(PlaceholderMeta meta) {
      return this.value.get();
   }

   public Long get(AbstractEntity entity) {
      return this.value.get();
   }

   public Long get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value.get();
   }

   public Long get(SkillCaster caster) {
      return this.value.get();
   }

   public Long get(MythicSpawner caster) {
      return this.value.get();
   }

   public Long get(PlaceholderContext context) {
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
