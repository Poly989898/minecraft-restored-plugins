package io.lumine.mythic.core.utils.jnbt;

public class NamedTag {
   private final String name;
   private final Tag tag;

   public NamedTag(String name, Tag tag) {
      checkNotNull(name);
      checkNotNull(tag);
      this.name = name;
      this.tag = tag;
   }

   public String getName() {
      return this.name;
   }

   public Tag getTag() {
      return this.tag;
   }

   private static void checkNotNull(Object object) {
      if (object == null) {
         throw new NullPointerException();
      }
   }
}
