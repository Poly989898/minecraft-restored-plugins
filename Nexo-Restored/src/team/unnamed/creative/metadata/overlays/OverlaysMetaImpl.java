package team.unnamed.creative.metadata.overlays;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.MetadataPart;

record OverlaysMetaImpl(List<OverlayEntry> overlays) implements OverlaysMeta {
   OverlaysMetaImpl(final @NotNull List<OverlayEntry> overlays) {
      this.overlays = (List)Objects.requireNonNull(overlays, "overlays");
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return OverlaysMeta.class;
   }

   public @NotNull List<OverlayEntry> entries() {
      return this.overlays;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{overlays=" + String.valueOf(this.overlays) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         OverlaysMetaImpl that = (OverlaysMetaImpl)o;
         return this.overlays.equals(that.overlays);
      } else {
         return false;
      }
   }
}
