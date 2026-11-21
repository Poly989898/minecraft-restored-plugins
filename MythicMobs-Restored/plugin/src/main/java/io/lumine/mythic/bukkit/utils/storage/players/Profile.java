package io.lumine.mythic.bukkit.utils.storage.players;

import java.util.UUID;
import javax.annotation.Nonnull;

public interface Profile {
   @Nonnull
   UUID getUniqueId();

   @Nonnull
   String getName();

   long getTimestamp();
}
