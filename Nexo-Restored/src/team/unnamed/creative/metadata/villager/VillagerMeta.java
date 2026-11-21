package team.unnamed.creative.metadata.villager;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.metadata.MetadataPart;

public interface VillagerMeta extends MetadataPart {
   @Contract("_ -> new")
   static @NotNull VillagerMeta villager(@NotNull Hat hat) {
      return new VillagerMetaImpl(hat);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_ -> new")
   static @NotNull VillagerMeta of(@NotNull Hat hat) {
      return new VillagerMetaImpl(hat);
   }

   @NotNull Hat hat();

   public static enum Hat {
      NONE,
      PARTIAL,
      FULL;
   }
}
