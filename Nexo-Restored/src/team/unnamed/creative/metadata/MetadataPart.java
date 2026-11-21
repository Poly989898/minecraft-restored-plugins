package team.unnamed.creative.metadata;

import org.jetbrains.annotations.NotNull;

public interface MetadataPart {
   default @NotNull Class<? extends MetadataPart> type() {
      return this.getClass();
   }
}
