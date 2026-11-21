package team.unnamed.creative.overlay;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class OverlayImpl extends ResourceContainerImpl implements Overlay {
   private final String directory;

   OverlayImpl(@NotNull String directory) {
      this.directory = (String)Objects.requireNonNull(directory, "directory");
   }

   public @NotNull String directory() {
      return this.directory;
   }
}
