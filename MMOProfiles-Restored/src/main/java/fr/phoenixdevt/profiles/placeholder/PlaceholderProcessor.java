package fr.phoenixdevt.profiles.placeholder;

import fr.phoenixdevt.profiles.ProfileDataModule;
import org.jetbrains.annotations.NotNull;

public interface PlaceholderProcessor {
   @NotNull
   ProfileDataModule getDataModule();

   void processPlaceholderRequest(@NotNull PlaceholderRequest placeholderRequest);
}
