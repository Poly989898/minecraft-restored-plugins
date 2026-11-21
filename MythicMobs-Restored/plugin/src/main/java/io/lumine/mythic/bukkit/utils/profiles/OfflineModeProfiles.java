package io.lumine.mythic.bukkit.utils.profiles;

import java.util.UUID;

public final class OfflineModeProfiles {
   public static UUID getUniqueId(String username) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
   }

   private OfflineModeProfiles() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
