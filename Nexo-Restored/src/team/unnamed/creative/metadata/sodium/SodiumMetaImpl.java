package team.unnamed.creative.metadata.sodium;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.metadata.MetadataPart;

public class SodiumMetaImpl implements SodiumMeta {
   private final List<String> ignoredShaders;

   SodiumMetaImpl(@NotNull List<String> ignoredShaders) {
      this.ignoredShaders = (List)Objects.requireNonNull(ignoredShaders, "ignoredShaders");
   }

   public @Unmodifiable @NotNull List<String> ignoredShaders() {
      return this.ignoredShaders;
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return SodiumMeta.class;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{ignored_shaders=" + String.valueOf(this.ignoredShaders) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SodiumMetaImpl packMeta = (SodiumMetaImpl)o;
         return this.ignoredShaders.equals(packMeta.ignoredShaders);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.ignoredShaders});
   }
}
