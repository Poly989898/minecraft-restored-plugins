package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0018\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0018\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0018\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J \u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u0016\u0010\b\u001a\u00070\t¢\u0006\u0002\b\n¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u001a"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/LandsCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lorg/bukkit/plugin/Plugin;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lorg/bukkit/plugin/Plugin;)V", "landsIntegration", "Lme/angeschossen/lands/api/LandsIntegration;", "Lorg/jetbrains/annotations/NotNull;", "getLandsIntegration", "()Lme/angeschossen/lands/api/LandsIntegration;", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "hasFlag", "location", "flag", "Lme/angeschossen/lands/api/flags/type/RoleFlag;", "protectionlib"}
)
public final class LandsCompat extends ProtectionCompatibility<Plugin> {
   @NotNull
   private final LandsIntegration landsIntegration;

   public LandsCompat(@NotNull JavaPlugin mainPlugin, @NotNull Plugin plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, plugin);
      LandsIntegration var10001 = LandsIntegration.of((Plugin)mainPlugin);
      Intrinsics.checkNotNullExpressionValue(var10001, "of(...)");
      this.landsIntegration = var10001;
   }

   @NotNull
   public final LandsIntegration getLandsIntegration() {
      return this.landsIntegration;
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      RoleFlag var10003 = Flags.BLOCK_PLACE;
      Intrinsics.checkNotNullExpressionValue(var10003, "BLOCK_PLACE");
      return this.hasFlag(target, player, var10003);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      RoleFlag var10003 = Flags.BLOCK_BREAK;
      Intrinsics.checkNotNullExpressionValue(var10003, "BLOCK_BREAK");
      return this.hasFlag(target, player, var10003);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      RoleFlag var10003 = Flags.INTERACT_GENERAL;
      Intrinsics.checkNotNullExpressionValue(var10003, "INTERACT_GENERAL");
      return this.hasFlag(target, player, var10003);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      RoleFlag var10003 = Flags.INTERACT_GENERAL;
      Intrinsics.checkNotNullExpressionValue(var10003, "INTERACT_GENERAL");
      return this.hasFlag(target, player, var10003);
   }

   private final boolean hasFlag(Location location, Player player, RoleFlag flag) {
      LandWorld var10000 = this.landsIntegration.getWorld(location.getWorld());
      if (var10000 == null) {
         return true;
      } else {
         LandWorld landWorld = var10000;
         return landWorld.hasRoleFlag(this.landsIntegration.getLandPlayer(player.getUniqueId()), location, flag, (Material)null, true);
      }
   }
}
