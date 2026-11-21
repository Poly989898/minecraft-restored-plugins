package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.william278.husktowns.BukkitHuskTowns;
import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.api.HuskTownsAPI;
import net.william278.husktowns.claim.Position;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import net.william278.husktowns.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J \u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u001bH\u0002R\u0018\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u0018\u0010\f\u001a\n \n*\u0004\u0018\u00010\r0\rX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000e¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/HuskTownsCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lnet/william278/husktowns/BukkitHuskTowns;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lnet/william278/husktowns/BukkitHuskTowns;)V", "commonAPI", "Lnet/william278/husktowns/api/HuskTownsAPI;", "kotlin.jvm.PlatformType", "Lnet/william278/husktowns/api/HuskTownsAPI;", "bukkitAPI", "Lnet/william278/husktowns/api/BukkitHuskTownsAPI;", "Lnet/william278/husktowns/api/BukkitHuskTownsAPI;", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "isOperationAllowed", "location", "type", "Lnet/william278/husktowns/libraries/cloplib/operation/OperationType;", "protectionlib"}
)
public final class HuskTownsCompat extends ProtectionCompatibility<BukkitHuskTowns> {
   private final HuskTownsAPI commonAPI;
   private final BukkitHuskTownsAPI bukkitAPI;

   public HuskTownsCompat(@NotNull JavaPlugin mainPlugin, @NotNull BukkitHuskTowns plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
      this.commonAPI = HuskTownsAPI.getInstance();
      this.bukkitAPI = BukkitHuskTownsAPI.getInstance();
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.isOperationAllowed(player, target, OperationType.BLOCK_PLACE);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.isOperationAllowed(player, target, OperationType.BLOCK_BREAK);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      OperationType type = target.getBlock().getType().isBlock() ? OperationType.BLOCK_INTERACT : OperationType.ENTITY_INTERACT;
      return this.isOperationAllowed(player, target, type);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.isOperationAllowed(player, target, OperationType.BLOCK_INTERACT);
   }

   private final boolean isOperationAllowed(Player player, Location location, OperationType type) {
      OnlineUser user = this.bukkitAPI.getOnlineUser(player);
      Position position = this.bukkitAPI.getPosition(location);
      return this.commonAPI.isOperationAllowed(user, type, position);
   }
}
