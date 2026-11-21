package team.unnamed.creative.part;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.overlay.ResourceContainer;

final class ResourcePackPartCompound implements ResourcePackPart {
   private final List<ResourcePackPart> parts;

   ResourcePackPartCompound(List<ResourcePackPart> parts) {
      this.parts = (List)Objects.requireNonNull(parts, "parts");
   }

   public void addTo(@NotNull ResourceContainer resourceContainer) {
      for(ResourcePackPart part : this.parts) {
         part.addTo(resourceContainer);
      }

   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{parts=" + String.valueOf(this.parts) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ResourcePackPartCompound that = (ResourcePackPartCompound)o;
         return this.parts.equals(that.parts);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.parts.hashCode();
   }
}
