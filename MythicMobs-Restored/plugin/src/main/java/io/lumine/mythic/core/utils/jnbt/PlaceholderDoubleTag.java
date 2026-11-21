package io.lumine.mythic.core.utils.jnbt;

import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;

public final class PlaceholderDoubleTag extends Tag {
   private final PlaceholderDouble value;

   public PlaceholderDoubleTag(PlaceholderDouble value) {
      this.value = value;
   }

   public PlaceholderDouble getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "TAG_PlaceholderDouble(" + this.value + ")";
   }
}
