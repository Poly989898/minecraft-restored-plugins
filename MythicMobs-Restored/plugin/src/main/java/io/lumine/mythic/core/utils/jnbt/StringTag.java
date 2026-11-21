package io.lumine.mythic.core.utils.jnbt;

public final class StringTag extends Tag {
   private final String value;

   public StringTag(String value) {
      checkNotNull(value);
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "TAG_String(" + this.value + ")";
   }

   private static void checkNotNull(Object object) {
      if (object == null) {
         throw new NullPointerException();
      }
   }
}
