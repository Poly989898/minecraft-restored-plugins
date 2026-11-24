package fr.phoenixdevt.mmoprofiles.bukkit.compat.permission;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ProfilePermissible {
   void removePermissions(@NotNull List<String> var1);

   void givePermissions(@NotNull List<String> var1);

   @NotNull
   List<String> getPermisions();

   void save();
}
