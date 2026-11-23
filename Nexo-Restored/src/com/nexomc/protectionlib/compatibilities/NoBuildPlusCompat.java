package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import p1xel.nobuildplus.Flags;
import p1xel.nobuildplus.NoBuildPlus;

public final class NoBuildPlusCompat extends ProtectionCompatibility<NoBuildPlus> {
   public NoBuildPlusCompat(@NotNull JavaPlugin mainPlugin, @NotNull NoBuildPlus plugin) {
      super(mainPlugin, plugin);
   }

   @Override
   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return !getPlugin().getAPI().canExecute(target.getWorld().getName(), Flags.build);
   }

   @Override
   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      return !getPlugin().getAPI().canExecute(target.getWorld().getName(), Flags.destroy);
   }

   @Override
   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      return !getPlugin().getAPI().canExecute(target.getWorld().getName(), Flags.use);
   }

   @Override
   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      return !getPlugin().getAPI().canExecute(target.getWorld().getName(), Flags.use);
   }
}
