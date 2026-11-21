package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0013H\u0002¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/GriefPreventionCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lme/ryanhamshire/GriefPrevention/GriefPrevention;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lme/ryanhamshire/GriefPrevention/GriefPrevention;)V", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "checkPermission", "permission", "Lme/ryanhamshire/GriefPrevention/ClaimPermission;", "protectionlib"}
)
public final class GriefPreventionCompat extends ProtectionCompatibility<GriefPrevention> {
   public GriefPreventionCompat(@NotNull JavaPlugin mainPlugin, @NotNull GriefPrevention plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.checkPermission(player, target, ClaimPermission.Build);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.checkPermission(player, target, ClaimPermission.Build);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.checkPermission(player, target, ClaimPermission.Access);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.checkPermission(player, target, ClaimPermission.Access);
   }

   private final boolean checkPermission(Player player, Location target, ClaimPermission permission) {
      PlayerData it = ((GriefPrevention)this.getPlugin()).dataStore.getPlayerData(player.getUniqueId());
      int var8 = 0;
      PlayerData var10000 = !it.ignoreClaims ? it : null;
      if (var10000 == null) {
         return true;
      } else {
         PlayerData playerData = var10000;
         Claim var9 = ((GriefPrevention)this.getPlugin()).dataStore.getClaimAt(target, false, playerData.lastClaim);
         if (var9 == null) {
            return true;
         } else {
            Claim claim = var9;
            playerData.lastClaim = claim;
            return claim.checkPermission(player, permission, (Event)null) == null;
         }
      }
   }
}
