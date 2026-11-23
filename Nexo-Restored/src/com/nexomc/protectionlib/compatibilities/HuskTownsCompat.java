package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import net.william278.husktowns.BukkitHuskTowns;
import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.api.HuskTownsAPI;
import net.william278.husktowns.claim.Position;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import net.william278.husktowns.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class HuskTownsCompat extends ProtectionCompatibility<BukkitHuskTowns> {
   private final HuskTownsAPI commonAPI;
   private final BukkitHuskTownsAPI bukkitAPI;

   public HuskTownsCompat(@NotNull JavaPlugin mainPlugin, @NotNull BukkitHuskTowns plugin) {
      super(mainPlugin, plugin);
      this.commonAPI = HuskTownsAPI.getInstance();
      this.bukkitAPI = BukkitHuskTownsAPI.getInstance();
   }

   @Override
   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return isOperationAllowed(player, target, OperationType.BLOCK_PLACE);
   }

   @Override
   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      return isOperationAllowed(player, target, OperationType.BLOCK_BREAK);
   }

   @Override
   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      OperationType type = target.getBlock().getType().isBlock() ? OperationType.BLOCK_INTERACT : OperationType.ENTITY_INTERACT;
      return isOperationAllowed(player, target, type);
   }

   @Override
   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      return isOperationAllowed(player, target, OperationType.BLOCK_INTERACT);
   }

   private boolean isOperationAllowed(Player player, Location location, OperationType type) {
      OnlineUser user = bukkitAPI.getOnlineUser(player);
      Position position = bukkitAPI.getPosition(location);
      return commonAPI.isOperationAllowed(user, type, position);
   }
}
