package team.unnamed.creative.metadata.overlays;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.metadata.MetadataPart;

@NonExtendable
public interface OverlaysMeta extends MetadataPart {
   @NotNull List<OverlayEntry> entries();

   static @NotNull OverlaysMeta of(@NotNull List<OverlayEntry> overlays) {
      return new OverlaysMetaImpl(overlays);
   }

   static @NotNull OverlaysMeta of(@NotNull OverlayEntry... overlays) {
      return of(Arrays.asList(overlays));
   }
}
