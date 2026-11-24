package fr.phoenixdevt.profiles.event;

import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileList;
import fr.phoenixdevt.profiles.ProfileProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PendingProfileEvent extends ProfileEvent implements Cancellable {
   private final ProfileProvider provider;
   private final List<String> notLoaded = new ArrayList<>();
   @Nullable
   private final Runnable runnable;
   private final boolean offlineCompletion;
   private boolean cancelled;

   public PendingProfileEvent(
      @Nullable Runnable runnable,
      @NotNull ProfileProvider<?> provider,
      @NotNull ProfileList playerData,
      @NotNull PlayerProfile profile,
      boolean offlineCompletion
   ) {
      super(playerData, profile);
      this.runnable = runnable;
      this.provider = provider;
      this.offlineCompletion = offlineCompletion;

      for (ProfileDataModule module : provider.getModules()) {
         this.notLoaded.add(module.getIdentifier());
      }

      this.validationCheck();
   }

   @NotNull
   public ProfileProvider getProvider() {
      return this.provider;
   }

   public void validate(@NotNull ProfileDataModule module) {
      if (this.runnable != null) {
         Validate.notNull(module, "Module cannot be null");
         Validate.isTrue(this.notLoaded.remove(module.getIdentifier()), "Module data already marked as loaded or is not registered");
         this.validationCheck();
      }
   }

   private void validationCheck() {
      if (this.notLoaded.isEmpty() && (this.offlineCompletion || this.player != null && this.player.isOnline()) && this.runnable != null) {
         this.runnable.run();
      }
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
