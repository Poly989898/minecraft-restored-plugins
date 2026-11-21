package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey;
import java.util.Optional;

public class RandomDouble extends PlaceholderDoubleImpl {
   private io.lumine.mythic.bukkit.utils.numbers.RandomDouble value;

   public RandomDouble(String string) {
      this.strValue = string;
      this.strUnparsedValue = string;
      this.value = new io.lumine.mythic.bukkit.utils.numbers.RandomDouble(string);
   }

   public RandomDouble(io.lumine.mythic.bukkit.utils.numbers.RandomDouble amount) {
      this.value = amount;
   }

   @Override
   public double get() {
      return this.value.get();
   }

   @Override
   public double get(PlaceholderMeta meta) {
      Optional<Object> maybeBias = meta.getMetadata(PlaceholderMetadataKey.RANDOM_BIAS);
      return maybeBias.isPresent() ? this.value.get((Double)maybeBias.get()) : this.value.get();
   }

   @Override
   public double get(AbstractEntity entity) {
      return this.value.get();
   }

   @Override
   public double get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.get(meta);
   }

   @Override
   public double get(PlaceholderMeta meta, AbstractItemStack entity) {
      return this.get(meta);
   }

   @Override
   public double get(SkillCaster caster) {
      return this.value.get();
   }

   @Override
   public double get(PlaceholderContext context) {
      return this.value.get();
   }

   public double getMin() {
      return this.value.getMin();
   }

   public double getMax() {
      return this.value.getMax();
   }

   @Override
   public String toString() {
      return this.value.getMin() + "to" + this.value.getMax();
   }

   @Override
   public String serialize() {
      return this.toString();
   }
}
