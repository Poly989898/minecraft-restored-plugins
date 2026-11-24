package fr.phoenixdevt.mmoprofiles.bukkit.compat.permission;

import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PermissionModule {
   @Nullable
   ProfilePermissible getPermissible(@NotNull ProfileListImpl var1);

   @NotNull
   CompletableFuture<ProfilePermissible> loadPermissible(@NotNull ProfileListImpl var1);
}
