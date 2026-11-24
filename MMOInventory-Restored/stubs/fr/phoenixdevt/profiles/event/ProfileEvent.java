package fr.phoenixdevt.profiles.event;

import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.ProfileList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class ProfileEvent extends PlayerEvent {
   private final PlayerProfile profile;
   private final ProfileList playerData;

   public ProfileEvent(@NotNull ProfileList playerData, @NotNull PlayerProfile profile) {
      super(playerData.getPlayer());
      this.playerData = playerData;
      this.profile = profile;
   }

   @NotNull
   public ProfileList getPlayerData() {
      return this.playerData;
   }

   @NotNull
   public PlayerProfile getProfile() {
      return this.profile;
   }
}
