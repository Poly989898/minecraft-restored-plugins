package io.lumine.mythic.core.utils.jnbt;

import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;

public final class PlaceholderIntTag extends Tag {
   private final PlaceholderInt value;

   public PlaceholderIntTag(PlaceholderInt value) {
      this.value = value;
   }

   public PlaceholderInt getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "TAG_PlaceholderInt(" + this.value + ")";
   }
}
