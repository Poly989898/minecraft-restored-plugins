package fr.phoenixdevt.profiles.placeholder;

import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderRequest {
   @NotNull
   ProfileDataModule getModule();

   @NotNull
   ProfileList getPlayer();

   @NotNull
   PlayerProfile getProfile();

   void addPlaceholder(@NotNull String text, @Nullable Object obj);

   void validate();
}
