package fr.phoenixdevt.mmoprofiles.bukkit.compat.permission;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ProfilePermissible {
   void removePermissions(@NotNull List<String> texts);

   void givePermissions(@NotNull List<String> texts);

   @NotNull
   List<String> getPermisions();

   void save();
}
