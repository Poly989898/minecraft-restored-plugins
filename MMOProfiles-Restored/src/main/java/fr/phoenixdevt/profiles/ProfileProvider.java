package fr.phoenixdevt.profiles;

import fr.phoenixdevt.profiles.placeholder.PlaceholderProcessor;
import java.util.Collection;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface ProfileProvider<P extends ProfileList> {
   void registerModule(@NotNull ProfileDataModule profileDataModule);

   void registerPlaceholders(@NotNull PlaceholderProcessor placeholderProcessor);

   @NotNull
   Collection<ProfileDataModule> getModules();

   @NotNull
   Collection<PlaceholderProcessor> getPlaceholders();

   @NotNull
   P getPlayerData(UUID uuid);
}
