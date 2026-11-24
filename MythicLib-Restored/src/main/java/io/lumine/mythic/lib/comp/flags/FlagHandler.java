package io.lumine.mythic.lib.comp.flags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlagHandler {
   private final List<FlagPlugin> flagPlugins = new ArrayList<>();

   public void registerPlugin(@NotNull FlagPlugin plugin) {
      this.flagPlugins.add(Objects.requireNonNull(plugin, "Flag plugin cannot be null"));
   }

   @Nullable
   public <T extends FlagPlugin> T getHandler(Class<T> clazz) {
      for (FlagPlugin plugin : this.flagPlugins) {
         if (plugin.getClass().equals(clazz)) {
            return (T)plugin;
         }
      }

      return null;
   }

   public boolean isPvpAllowed(Location loc) {
      for (FlagPlugin plugin : this.flagPlugins) {
         if (!plugin.isPvpAllowed(loc)) {
            return false;
         }
      }

      return true;
   }

   public boolean isFlagAllowed(Player player, CustomFlag flag) {
      for (FlagPlugin plugin : this.flagPlugins) {
         if (plugin.isFlagAllowed(player, flag) != flag.getDefault()) {
            return !flag.getDefault();
         }
      }

      return flag.getDefault();
   }

   public boolean isFlagAllowed(Location loc, CustomFlag flag) {
      for (FlagPlugin plugin : this.flagPlugins) {
         if (plugin.isFlagAllowed(loc, flag) != flag.getDefault()) {
            return !flag.getDefault();
         }
      }

      return flag.getDefault();
   }
}
