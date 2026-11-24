package fr.phoenixdevt.profiles.event;

import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.ProfileList;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProfileSelectedEvent extends ProfileEvent {
   private static final HandlerList HANDLER_LIST = new HandlerList();

   public ProfileSelectedEvent(@NotNull ProfileList playerData, @NotNull PlayerProfile profile) {
      super(playerData, profile);
   }

   @NotNull
   public HandlerList getHandlers() {
      return HANDLER_LIST;
   }

   public static HandlerList getHandlerList() {
      return HANDLER_LIST;
   }
}
