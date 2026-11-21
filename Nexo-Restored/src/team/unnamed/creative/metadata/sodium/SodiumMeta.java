package team.unnamed.creative.metadata.sodium;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.metadata.MetadataPart;

public interface SodiumMeta extends MetadataPart {
   @NotNull @Unmodifiable List<String> ignoredShaders();

   static @NotNull SodiumMeta sodium(@NotNull List<String> ignoredShaders) {
      return new SodiumMetaImpl(ignoredShaders);
   }
}
