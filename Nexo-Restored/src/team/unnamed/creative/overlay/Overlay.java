package team.unnamed.creative.overlay;

import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface Overlay extends ResourcePackPart, ResourceContainer {
   @Subst("dir")
   @NotNull String directory();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      if (resourceContainer instanceof ResourcePack) {
         ((ResourcePack)resourceContainer).overlay(this);
      } else {
         throw new IllegalArgumentException("Cannot add an Overlay to a resource container that is not a resource pack.");
      }
   }

   static @NotNull Overlay overlay(@OverlayEntry.Directory @NotNull String directory) {
      return new OverlayImpl(directory);
   }
}
