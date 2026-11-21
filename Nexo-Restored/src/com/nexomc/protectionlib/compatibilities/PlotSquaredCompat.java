package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import com.plotsquared.core.plot.Plot;
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
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u000e\u0010\u0011\u001a\u0004\u0018\u00010\u0012*\u00020\rH\u0002J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\rH\u0002¨\u0006\u0016"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/PlotSquaredCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lorg/bukkit/plugin/Plugin;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lorg/bukkit/plugin/Plugin;)V", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "plotFromLocation", "Lcom/plotsquared/core/plot/Plot;", "adaptBukkitLocation", "Lcom/plotsquared/core/location/Location;", "location", "protectionlib"}
)
public final class PlotSquaredCompat extends ProtectionCompatibility<Plugin> {
   public PlotSquaredCompat(@NotNull JavaPlugin mainPlugin, @NotNull Plugin plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, plugin);
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      Plot var10000 = this.plotFromLocation(target);
      return !(var10000 != null ? var10000.isDenied(player.getUniqueId()) : false);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      Plot var10000 = this.plotFromLocation(target);
      return !(var10000 != null ? var10000.isDenied(player.getUniqueId()) : false);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      Plot var10000 = this.plotFromLocation(target);
      return !(var10000 != null ? var10000.isDenied(player.getUniqueId()) : false);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      Plot var10000 = this.plotFromLocation(target);
      return !(var10000 != null ? var10000.isDenied(player.getUniqueId()) : false);
   }

   private final Plot plotFromLocation(Location $this$plotFromLocation) {
      com.plotsquared.core.location.Location var10000 = this.adaptBukkitLocation($this$plotFromLocation);
      return var10000 == null ? null : Plot.getPlot(var10000);
   }

   private final com.plotsquared.core.location.Location adaptBukkitLocation(Location location) {
      return !location.isWorldLoaded() ? null : com.plotsquared.core.location.Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }
}
