package team.unnamed.creative.font;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.util.MoreCollections;

public class SpaceFontProvider implements FontProvider {
   private final Map<String, Integer> advances;

   protected SpaceFontProvider(Map<String, Integer> advances) {
      Objects.requireNonNull(advances, "advances");
      this.advances = MoreCollections.<String, Integer>immutableMapOf(advances);
      this.validate();
   }

   private void validate() {
      for(Map.Entry<String, Integer> entry : this.advances.entrySet()) {
         String character = (String)entry.getKey();
         Integer offset = (Integer)entry.getValue();
         Objects.requireNonNull(character, "A key from the 'advances' map is null");
         Objects.requireNonNull(offset, "The advanced pixels value for '" + character + "' character is null");
      }

   }

   public Map<String, Integer> advances() {
      return this.advances;
   }

   public SpaceFontProvider advances(Map<String, Integer> advances) {
      return this.toBuilder().advances(advances).build();
   }

   public Builder toBuilder() {
      return FontProvider.space().advances(this.advances);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{advances=" + String.valueOf(this.advances) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SpaceFontProvider that = (SpaceFontProvider)o;
         return this.advances.equals(that.advances);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.advances});
   }

   public static class Builder {
      private Map<String, Integer> advances;

      protected Builder() {
      }

      public Builder advances(Map<String, Integer> entries) {
         Objects.requireNonNull(entries, "entries");
         this.advances = entries;
         return this;
      }

      public Builder advance(String character, int value) {
         if (this.advances == null) {
            this.advances = new LinkedHashMap();
         }

         this.advances.put(character, value);
         return this;
      }

      public Builder advance(int codePoint, int value) {
         return this.advance((new StringBuilder()).appendCodePoint(codePoint).toString(), value);
      }

      public SpaceFontProvider build() {
         return new SpaceFontProvider(this.advances);
      }
   }
}
