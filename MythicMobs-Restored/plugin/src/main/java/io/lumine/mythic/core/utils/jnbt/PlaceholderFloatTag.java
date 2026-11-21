package io.lumine.mythic.core.utils.jnbt;

import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;

public final class PlaceholderFloatTag extends Tag {
   private final PlaceholderFloat value;

   public PlaceholderFloatTag(PlaceholderFloat value) {
      this.value = value;
   }

   public PlaceholderFloat getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "TAG_PlaceholderFloat(" + this.value + ")";
   }
}
