package io.lumine.mythic.core.utils.jnbt;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;

public final class PlaceholderStringTag extends Tag {
   private final PlaceholderString value;

   public PlaceholderStringTag(PlaceholderString value) {
      this.value = value;
   }

   public PlaceholderString getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "TAG_PlaceholderString(" + this.value + ")";
   }
}
