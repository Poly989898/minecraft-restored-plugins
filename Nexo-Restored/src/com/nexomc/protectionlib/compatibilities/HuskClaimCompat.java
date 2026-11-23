package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import java.util.Optional;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class HuskClaimCompat extends ProtectionCompatibility<BukkitHuskClaims> {
   private final HuskClaimsAPI huskClaimsCommon;
   private final BukkitHuskClaimsAPI huskClaimsBukkit;

   public HuskClaimCompat(@NotNull JavaPlugin mainPlugin, @NotNull BukkitHuskClaims plugin) {
      super(mainPlugin, plugin);
      this.huskClaimsCommon = HuskClaimsAPI.getInstance();
      this.huskClaimsBukkit = BukkitHuskClaimsAPI.getInstance();
   }

   @Override
   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return isAllowed(player, target, OperationType.BLOCK_PLACE);
   }

   @Override
   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      return isAllowed(player, target, OperationType.BLOCK_BREAK);
   }

   @Override
   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      OperationType type = target.getBlock().getType().isBlock() ? OperationType.BLOCK_INTERACT : OperationType.ENTITY_INTERACT;
      return isAllowed(player, target, type);
   }

   @Override
   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      return isAllowed(player, target, OperationType.BLOCK_INTERACT);
   }

   private boolean isAllowed(Player player, Location target, OperationType type) {
      OnlineUser onlineUser = huskClaimsCommon.getOnlineUser(player.getUniqueId());
      Position position = huskClaimsBukkit.getPosition(target);
      Optional<TrustLevel> trustLevel = huskClaimsBukkit.getTrustLevelAt(position, (Trustable) onlineUser);
      return trustLevel.isEmpty() || trustLevel.get().getFlags().contains(type);
   }
}
