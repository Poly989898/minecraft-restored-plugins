package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.api.CrashClaimAPI;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class CrashClaimCompat extends ProtectionCompatibility<CrashClaim> {
   @NotNull
   private final CrashClaimAPI crashClaim;

   public CrashClaimCompat(@NotNull JavaPlugin mainPlugin, @NotNull CrashClaim plugin) {
      super(mainPlugin, plugin);
      this.crashClaim = new CrashClaimAPI(plugin);
   }

   @NotNull
   public final CrashClaimAPI getCrashClaim() {
      return crashClaim;
   }

   @Override
   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return check(player, target, PermissionRoute.BUILD);
   }

   @Override
   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      return check(player, target, PermissionRoute.BUILD);
   }

   @Override
   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      return check(player, target, PermissionRoute.INTERACTIONS);
   }

   @Override
   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      return check(player, target, PermissionRoute.INTERACTIONS);
   }

   private boolean check(Player player, Location target, PermissionRoute route) {
      if (crashClaim.getPermissionHelper().getBypassManager().isBypass(player.getUniqueId())) return true;
      Claim claim = crashClaim.getClaim(target);
      return claim == null || claim.hasPermission(player.getUniqueId(), target, route);
   }
}
