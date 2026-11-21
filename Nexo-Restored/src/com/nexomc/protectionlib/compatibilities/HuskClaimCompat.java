package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.william278.huskclaims.BukkitHuskClaims;
import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.trust.TrustLevel;
import net.william278.huskclaims.trust.Trustable;
import net.william278.huskclaims.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016R\u0018\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u0018\u0010\f\u001a\n \n*\u0004\u0018\u00010\r0\rX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000e¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/HuskClaimCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lnet/william278/huskclaims/BukkitHuskClaims;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lnet/william278/huskclaims/BukkitHuskClaims;)V", "huskClaimsCommon", "Lnet/william278/huskclaims/api/HuskClaimsAPI;", "kotlin.jvm.PlatformType", "Lnet/william278/huskclaims/api/HuskClaimsAPI;", "huskClaimsBukkit", "Lnet/william278/huskclaims/api/BukkitHuskClaimsAPI;", "Lnet/william278/huskclaims/api/BukkitHuskClaimsAPI;", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "protectionlib"}
)
public final class HuskClaimCompat extends ProtectionCompatibility<BukkitHuskClaims> {
   private final HuskClaimsAPI huskClaimsCommon;
   private final BukkitHuskClaimsAPI huskClaimsBukkit;

   public HuskClaimCompat(@NotNull JavaPlugin mainPlugin, @NotNull BukkitHuskClaims plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
      this.huskClaimsCommon = HuskClaimsAPI.getInstance();
      this.huskClaimsBukkit = BukkitHuskClaimsAPI.getInstance();
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      OnlineUser onlineUser = this.huskClaimsCommon.getOnlineUser(player.getUniqueId());
      Position position = this.huskClaimsBukkit.getPosition(target);
      Optional trustLevel = this.huskClaimsBukkit.getTrustLevelAt(position, (Trustable)onlineUser);
      return trustLevel.isEmpty() || ((TrustLevel)trustLevel.get()).getFlags().contains(OperationType.BLOCK_PLACE);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      OnlineUser onlineUser = this.huskClaimsCommon.getOnlineUser(player.getUniqueId());
      Position position = this.huskClaimsBukkit.getPosition(target);
      Optional trustLevel = this.huskClaimsBukkit.getTrustLevelAt(position, (Trustable)onlineUser);
      return trustLevel.isEmpty() || ((TrustLevel)trustLevel.get()).getFlags().contains(OperationType.BLOCK_BREAK);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      OnlineUser onlineUser = this.huskClaimsCommon.getOnlineUser(player.getUniqueId());
      Position position = this.huskClaimsBukkit.getPosition(target);
      OperationType operationType = target.getBlock().getType().isBlock() ? OperationType.BLOCK_INTERACT : OperationType.ENTITY_INTERACT;
      Optional trustLevel = this.huskClaimsBukkit.getTrustLevelAt(position, (Trustable)onlineUser);
      return trustLevel.isEmpty() || ((TrustLevel)trustLevel.get()).getFlags().contains(operationType);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      OnlineUser onlineUser = this.huskClaimsCommon.getOnlineUser(player.getUniqueId());
      Position position = this.huskClaimsBukkit.getPosition(target);
      Optional trustLevel = this.huskClaimsBukkit.getTrustLevelAt(position, (Trustable)onlineUser);
      return trustLevel.isEmpty() || ((TrustLevel)trustLevel.get()).getFlags().contains(OperationType.BLOCK_INTERACT);
   }
}
