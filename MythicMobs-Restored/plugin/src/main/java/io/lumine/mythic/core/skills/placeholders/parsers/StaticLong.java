package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class StaticLong extends PlaceholderLongImpl {
   private final long value;

   public StaticLong(String string) {
      super(string);
      this.value = Long.parseLong(string);
   }

   public Long get() {
      return this.value;
   }

   public Long get(PlaceholderMeta meta) {
      return this.value;
   }

   public Long get(AbstractEntity entity) {
      return this.value;
   }

   public Long get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   public Long get(SkillCaster caster) {
      return this.value;
   }

   public Long get(PlaceholderContext context) {
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
   public boolean isStaticallyEqualTo(Long value) {
      return this.value == value;
   }
}
