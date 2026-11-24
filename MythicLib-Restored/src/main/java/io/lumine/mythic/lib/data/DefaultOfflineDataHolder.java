package io.lumine.mythic.lib.data;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class DefaultOfflineDataHolder implements OfflineDataHolder {
   private final UUID uuid;

   public DefaultOfflineDataHolder(UUID uuid) {
      this.uuid = uuid;
   }

   @NotNull
   @Override
   public UUID getUniqueId() {
      return this.uuid;
   }
}
