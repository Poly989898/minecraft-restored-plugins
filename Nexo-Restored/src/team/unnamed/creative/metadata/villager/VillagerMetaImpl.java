package team.unnamed.creative.metadata.villager;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.metadata.MetadataPart;

record VillagerMetaImpl(VillagerMeta.Hat hat) implements VillagerMeta {
   VillagerMetaImpl(final VillagerMeta.@NotNull Hat hat) {
      this.hat = (VillagerMeta.Hat)Objects.requireNonNull(hat, "hat");
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return VillagerMeta.class;
   }

   public VillagerMeta.@NotNull Hat hat() {
      return this.hat;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{hat=" + String.valueOf(this.hat) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VillagerMetaImpl that = (VillagerMetaImpl)o;
         return this.hat == that.hat;
      } else {
         return false;
      }
   }
}
