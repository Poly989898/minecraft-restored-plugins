package team.unnamed.creative.model;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

public record ModelTexture(@Nullable Key key, @Nullable String reference, boolean forceTranslucent) {
   public static final boolean DEFAULT_FORCE_TRANSLUCENT = false;

   public Object get() {
      return this.key == null ? this.reference : this.key;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ModelTexture that = (ModelTexture)o;
         if (!Objects.equals(this.key, that.key)) {
            return false;
         } else if (!Objects.equals(this.reference, that.reference)) {
            return false;
         } else {
            return this.forceTranslucent == that.forceTranslucent;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.key != null ? this.key.hashCode() : 0;
      result = 31 * result + (this.reference != null ? this.reference.hashCode() : 0);
      return result;
   }

   public static ModelTexture ofKey(Key key) {
      Objects.requireNonNull(key, "key");
      return new ModelTexture(key, (String)null, false);
   }

   public static ModelTexture ofReference(String reference) {
      Objects.requireNonNull(reference, "reference");
      return new ModelTexture((Key)null, reference, false);
   }

   public static ModelTexture ofKey(Key key, boolean forceTranslucent) {
      Objects.requireNonNull(key, "key");
      return new ModelTexture(key, (String)null, forceTranslucent);
   }

   public static ModelTexture ofReference(String reference, boolean forceTranslucent) {
      Objects.requireNonNull(reference, "reference");
      return new ModelTexture((Key)null, reference, forceTranslucent);
   }
}
