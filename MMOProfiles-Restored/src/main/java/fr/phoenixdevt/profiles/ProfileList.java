package fr.phoenixdevt.profiles;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ProfileList<P extends PlayerProfile> {
   UUID getUniqueId();

   @NotNull
   Player getPlayer();

   @NotNull
   List<P> getProfiles();

   @Nullable
   P getCurrent();

   @Nullable
   P getProfile(UUID uuid);
}
