package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

public final class BentoBoxCompat extends ProtectionCompatibility<BentoBox> {
   public BentoBoxCompat(@NotNull JavaPlugin mainPlugin, @NotNull BentoBox plugin) {
      super(mainPlugin, plugin);
   }

   @Override
   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return canDo(player, target, Flags.PLACE_BLOCKS);
   }

   @Override
   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      return canDo(player, target, Flags.BREAK_BLOCKS);
   }

   @Override
   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      return !getPlugin().getIWM().inWorld(target) || getPlugin().getIslands().locationIsOnIsland(player, target);
   }

   @Override
   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      return !getPlugin().getIWM().inWorld(target) || getPlugin().getIslands().locationIsOnIsland(player, target);
   }

   private boolean canDo(Player player, Location target, Flag flag) {
      if (!getPlugin().getIWM().inWorld(target)) return true;
      Optional<Island> island = getPlugin().getIslands().getIslandAt(target);
      return island
         .map(value -> value.isAllowed(User.getInstance(player), flag))
         .orElse(!flag.isSetForWorld(target.getWorld()));
   }
}
