package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

public class PlaceholderColorImpl implements PlaceholderColor {
   private final Object color;

   public PlaceholderColorImpl(PlaceholderString input) {
      if (input.isStatic()) {
         this.color = Chroma.of(input.get());
      } else {
         this.color = input;
      }
   }

   public Chroma get() {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get()) : (Chroma)this.color;
   }

   /** @deprecated */
   public Chroma get(PlaceholderMeta meta) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(meta)) : (Chroma)this.color;
   }

   /** @deprecated */
   public Chroma get(AbstractEntity entity) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(entity)) : (Chroma)this.color;
   }

   /** @deprecated */
   public Chroma get(PlaceholderMeta meta, AbstractEntity entity) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(meta, entity)) : (Chroma)this.color;
   }

   /** @deprecated */
   public Chroma get(PlaceholderMeta meta, AbstractLocation location) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(meta, location)) : (Chroma)this.color;
   }

   /** @deprecated */
   public Chroma get(SkillCaster caster) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(caster)) : (Chroma)this.color;
   }

   public Chroma get(PlaceholderContext context) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(context)) : (Chroma)this.color;
   }

   public boolean isStaticallyEqualTo(Chroma value) {
      return false;
   }

   public Chroma get(PlaceholderMeta meta, AbstractItemStack itemStack) {
      return this.color instanceof PlaceholderString placeholderString ? Chroma.of(placeholderString.get(meta, itemStack)) : (Chroma)this.color;
   }

   @Override
   public String toString() {
      return this.color instanceof PlaceholderString placeholderString ? this.color.toString() : ((Chroma)this.color).serializeShortForm();
   }

   @Override
   public boolean isStatic() {
      return this.color instanceof Chroma;
   }

   @Override
   public boolean isPresent() {
      return this.color != null;
   }

   @Override
   public String serialize() {
      if (this.color instanceof Chroma chroma) {
         return chroma.serializeShortForm();
      } else {
         return this.color instanceof PlaceholderString placeholderString ? placeholderString.get() : "";
      }
   }
}
