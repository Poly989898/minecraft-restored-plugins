package team.unnamed.creative.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.overlay.ResourceContainer;

public interface ResourcePackPart {
   void addTo(@NotNull ResourceContainer var1);

   static @NotNull ResourcePackPart compound(@NotNull Collection<? extends ResourcePackPart> parts) {
      return new ResourcePackPartCompound(new ArrayList(parts));
   }

   static @NotNull ResourcePackPart compound(@NotNull ResourcePackPart... parts) {
      return compound(Arrays.asList(parts));
   }
}
