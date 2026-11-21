package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.api.CrashClaimAPI;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0012\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0013\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/CrashClaimCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lnet/crashcraft/crashclaim/CrashClaim;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lnet/crashcraft/crashclaim/CrashClaim;)V", "crashClaim", "Lnet/crashcraft/crashclaim/api/CrashClaimAPI;", "getCrashClaim", "()Lnet/crashcraft/crashclaim/api/CrashClaimAPI;", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "protectionlib"}
)
public final class CrashClaimCompat extends ProtectionCompatibility<CrashClaim> {
   @NotNull
   private final CrashClaimAPI crashClaim;

   public CrashClaimCompat(@NotNull JavaPlugin mainPlugin, @NotNull CrashClaim plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
      this.crashClaim = new CrashClaimAPI(plugin);
   }

   @NotNull
   public final CrashClaimAPI getCrashClaim() {
      return this.crashClaim;
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      boolean var3;
      if (!this.crashClaim.getPermissionHelper().getBypassManager().isBypass(player.getUniqueId())) {
         Claim var10000 = this.crashClaim.getClaim(target);
         if (var10000 != null ? !var10000.hasPermission(player.getUniqueId(), target, PermissionRoute.BUILD) : false) {
            var3 = false;
            return var3;
         }
      }

      var3 = true;
      return var3;
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      boolean var3;
      if (!this.crashClaim.getPermissionHelper().getBypassManager().isBypass(player.getUniqueId())) {
         Claim var10000 = this.crashClaim.getClaim(target);
         if (var10000 != null ? !var10000.hasPermission(player.getUniqueId(), target, PermissionRoute.BUILD) : false) {
            var3 = false;
            return var3;
         }
      }

      var3 = true;
      return var3;
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      boolean var3;
      if (!this.crashClaim.getPermissionHelper().getBypassManager().isBypass(player.getUniqueId())) {
         Claim var10000 = this.crashClaim.getClaim(target);
         if (var10000 != null ? !var10000.hasPermission(player.getUniqueId(), target, PermissionRoute.INTERACTIONS) : false) {
            var3 = false;
            return var3;
         }
      }

      var3 = true;
      return var3;
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      boolean var3;
      if (!this.crashClaim.getPermissionHelper().getBypassManager().isBypass(player.getUniqueId())) {
         Claim var10000 = this.crashClaim.getClaim(target);
         if (var10000 != null ? !var10000.hasPermission(player.getUniqueId(), target, PermissionRoute.INTERACTIONS) : false) {
            var3 = false;
            return var3;
         }
      }

      var3 = true;
      return var3;
   }
}
