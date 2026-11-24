package fr.phoenixdevt.profiles.event;

import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.ProfileList;
import fr.phoenixdevt.profiles.ProfileProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfileRemoveEvent extends PendingProfileEvent {
   private static final HandlerList HANDLER_LIST = new HandlerList();

   public ProfileRemoveEvent(@NotNull ProfileProvider<?> provider, @NotNull ProfileList playerData, @NotNull PlayerProfile profile) {
      this(null, provider, playerData, profile);
   }

   public ProfileRemoveEvent(@Nullable Runnable runnable, @NotNull ProfileProvider<?> provider, @NotNull ProfileList playerData, @NotNull PlayerProfile profile) {
      super(runnable, provider, playerData, profile, false);
   }

   public HandlerList getHandlers() {
      return HANDLER_LIST;
   }

   public static HandlerList getHandlerList() {
      return HANDLER_LIST;
   }
}
