package fr.phoenixdevt.mmoprofiles.bukkit.profile.placeholder;

import fr.phoenixdevt.profiles.PlayerProfile;
import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileList;
import fr.phoenixdevt.profiles.placeholder.PlaceholderRequest;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderRequestImpl extends CompletableFuture<Void> implements PlaceholderRequest {
   private final ProfileList player;
   private final PlayerProfile profile;
   private final ProfileDataModule module;
   private boolean validated;
   private static final String UNPARSED_VALUE = "UNPARSED_PLACEHOLDER";

   public PlaceholderRequestImpl(@NotNull ProfileDataModule module, @NotNull ProfileList player, @NotNull PlayerProfile profile) {
      this.module = module;
      this.player = player;
      this.profile = profile;
   }

   @Override
   public ProfileDataModule getModule() {
      return this.module;
   }

   @Override
   public ProfileList getPlayer() {
      return this.player;
   }

   @Override
   public PlayerProfile getProfile() {
      return this.profile;
   }

   @Override
   public void addPlaceholder(@NotNull String key, @Nullable Object value) {
      Validate.notNull(key, "Placeholder cannot be null");
      Validate.notNull(key, "Value cannot be null");
      Validate.isTrue(!value.equals("UNPARSED_PLACEHOLDER"), "Cannot use this string as value");
      this.profile.getPlaceholderCache().registerPlaceholder(this.module.getIdentifier() + "_" + key, String.valueOf(value));
   }

   @Override
   public void validate() {
      Validate.isTrue(!this.validated, "Placeholder request already validated");
      this.validated = true;
      Bukkit.getScheduler().runTask(this.module.getOwningPlugin(), () -> this.complete(null));
   }
}
