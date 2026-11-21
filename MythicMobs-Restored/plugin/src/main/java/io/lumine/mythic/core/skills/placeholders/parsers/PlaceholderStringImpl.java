package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderParser;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class PlaceholderStringImpl extends GenericPlaceholderParser<String> implements PlaceholderString {
   public PlaceholderStringImpl(String string) {
      super(string);
   }

   protected String handleParsing(String string) {
      return string;
   }

   @Override
   public String toString() {
      return this.strValue;
   }

   public static PlaceholderStringImpl of(String string) {
      return string == null ? null : new PlaceholderStringImpl(string);
   }

   @Override
   public String get() {
      return this.get(PlaceholderContext.of());
   }

   @Override
   public String get(AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().entity(entity).build());
   }

   @Override
   public String get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).entity(entity).build());
   }

   @Override
   public String get(PlaceholderMeta meta, AbstractLocation location) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().location(location).meta(meta).build());
   }

   @Override
   public String get(SkillCaster caster) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().caster(caster).build());
   }

   @Override
   public String get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).item(itemStack).build());
   }

   @Override
   public String get(PlaceholderMeta meta) {
      return this.isStatic() ? this.get(PlaceholderContext.of()) : this.get(PlaceholderContext.builder().meta(meta).build());
   }

   @Override
   public boolean isStaticallyEqualTo(String value) {
      return value == null && this.get() == null ? true : this.isStatic() && this.get().equals(value);
   }
}
