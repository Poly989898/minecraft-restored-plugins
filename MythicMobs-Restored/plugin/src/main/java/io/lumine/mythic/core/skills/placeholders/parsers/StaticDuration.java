package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.utils.numbers.Duration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class StaticDuration extends PlaceholderDurationImpl {
   private final Duration value;

   public StaticDuration(String string, DurationUnit defaultUnit) {
      DurationUnit unit = defaultUnit == null ? DurationUnit.SECONDS : defaultUnit;
      this.strValue = string;
      this.strUnparsedValue = string;

      Duration parsed;
      try {
         parsed = Duration.parse(string, unit);
      } catch (IllegalArgumentException e) {
         parsed = new Duration(0.0, unit);
      }

      this.value = parsed;
   }

   public Duration get() {
      return this.value;
   }

   public Duration get(PlaceholderMeta meta) {
      return this.value;
   }

   public Duration get(AbstractEntity entity) {
      return this.value;
   }

   public Duration get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.value;
   }

   public Duration get(SkillCaster caster) {
      return this.value;
   }

   public Duration get(PlaceholderContext context) {
      return this.value;
   }

   @Override
   public boolean isStatic() {
      return true;
   }

   @Override
   public boolean isStaticallyEqualTo(Duration value) {
      return this.value.equals(value);
   }
}
