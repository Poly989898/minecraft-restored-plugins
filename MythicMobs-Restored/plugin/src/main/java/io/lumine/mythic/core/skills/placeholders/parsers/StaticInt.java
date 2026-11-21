package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public class StaticInt extends PlaceholderIntImpl {
   private final int value;

   public StaticInt(String string) {
      this.value = Integer.parseInt(string);
   }

   @Override
   public int get() {
      return this.value;
   }

   @Override
   public int get(PlaceholderMeta meta) {
      return this.value;
   }

   @Override
   public int get(AbstractEntity entity) {
      return this.value;
   }

   @Override
   public int get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   @Override
   public int get(SkillCaster caster) {
      return this.value;
   }

   @Override
   public int get(MythicSpawner spawner) {
      return this.value;
   }

   @Override
   public int get(PlaceholderContext context) {
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
   public boolean isStaticallyEqualTo(int value) {
      return this.value == value;
   }
}
