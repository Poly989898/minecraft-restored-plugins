package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0013H\u0002¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/WorldGuardCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lcom/sk89q/worldguard/bukkit/WorldGuardPlugin;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lcom/sk89q/worldguard/bukkit/WorldGuardPlugin;)V", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "hasBypass", "localPlayer", "Lcom/sk89q/worldguard/LocalPlayer;", "protectionlib"}
)
public final class WorldGuardCompat extends ProtectionCompatibility<WorldGuardPlugin> {
   public WorldGuardCompat(@NotNull JavaPlugin mainPlugin, @NotNull WorldGuardPlugin plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      LocalPlayer localPlayer = ((WorldGuardPlugin)this.getPlugin()).wrapPlayer(player);
      RegionQuery var10000 = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
      com.sk89q.worldedit.util.Location var10001 = BukkitAdapter.adapt(target);
      StateFlag[] var4 = new StateFlag[]{Flags.BLOCK_PLACE};
      boolean var5;
      if (!var10000.testBuild(var10001, localPlayer, var4)) {
         Intrinsics.checkNotNull(localPlayer);
         if (!this.hasBypass(player, localPlayer)) {
            var5 = false;
            return var5;
         }
      }

      var5 = true;
      return var5;
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      LocalPlayer localPlayer = ((WorldGuardPlugin)this.getPlugin()).wrapPlayer(player);
      RegionQuery var10000 = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
      com.sk89q.worldedit.util.Location var10001 = BukkitAdapter.adapt(target);
      StateFlag[] var4 = new StateFlag[]{Flags.BLOCK_BREAK};
      boolean var5;
      if (!var10000.testBuild(var10001, localPlayer, var4)) {
         Intrinsics.checkNotNull(localPlayer);
         if (!this.hasBypass(player, localPlayer)) {
            var5 = false;
            return var5;
         }
      }

      var5 = true;
      return var5;
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      LocalPlayer localPlayer = ((WorldGuardPlugin)this.getPlugin()).wrapPlayer(player);
      RegionQuery var10000 = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
      com.sk89q.worldedit.util.Location var10001 = BukkitAdapter.adapt(target);
      StateFlag[] var4 = new StateFlag[]{Flags.INTERACT};
      boolean var5;
      if (!var10000.testBuild(var10001, localPlayer, var4)) {
         Intrinsics.checkNotNull(localPlayer);
         if (!this.hasBypass(player, localPlayer)) {
            var5 = false;
            return var5;
         }
      }

      var5 = true;
      return var5;
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      LocalPlayer localPlayer = ((WorldGuardPlugin)this.getPlugin()).wrapPlayer(player);
      RegionQuery var10000 = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
      com.sk89q.worldedit.util.Location var10001 = BukkitAdapter.adapt(target);
      StateFlag[] var4 = new StateFlag[]{Flags.USE};
      boolean var5;
      if (!var10000.testBuild(var10001, localPlayer, var4)) {
         Intrinsics.checkNotNull(localPlayer);
         if (!this.hasBypass(player, localPlayer)) {
            var5 = false;
            return var5;
         }
      }

      var5 = true;
      return var5;
   }

   private final boolean hasBypass(Player player, LocalPlayer localPlayer) {
      return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(player.getWorld()));
   }
}
