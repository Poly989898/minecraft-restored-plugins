package com.nexomc.protectionlib.compatibilities;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import com.nexomc.protectionlib.ProtectionCompatibility;
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
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\"\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0012\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0013\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/ResidenceCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lcom/bekvon/bukkit/residence/Residence;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lcom/bekvon/bukkit/residence/Residence;)V", "canDo", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "flag", "Lcom/bekvon/bukkit/residence/containers/Flags;", "canBuild", "canBreak", "canInteract", "canUse", "protectionlib"}
)
public final class ResidenceCompat extends ProtectionCompatibility<Residence> {
   public ResidenceCompat(@NotNull JavaPlugin mainPlugin, @NotNull Residence plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
   }

   private final boolean canDo(Player player, Location target, Flags flag) {
      boolean var6;
      if (!player.isOp()) {
         label21: {
            ClaimedResidence var10000 = ((Residence)this.getPlugin()).getResidenceManager().getByLoc(target);
            if (var10000 != null) {
               ResidencePermissions var4 = var10000.getPermissions();
               if (var4 != null) {
                  var6 = !var4.playerHas(player, flag, false);
                  break label21;
               }
            }

            var6 = false;
         }

         if (var6) {
            var6 = false;
            return var6;
         }
      }

      var6 = true;
      return var6;
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.canDo(player, target, Flags.build);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.canDo(player, target, Flags.destroy);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.canDo(player, target, Flags.use);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return this.canDo(player, target, Flags.use);
   }
}
