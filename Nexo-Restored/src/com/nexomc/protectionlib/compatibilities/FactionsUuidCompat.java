package com.nexomc.protectionlib.compatibilities;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.PermissibleActions;
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
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/FactionsUuidCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lcom/massivecraft/factions/FactionsPlugin;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lcom/massivecraft/factions/FactionsPlugin;)V", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "protectionlib"}
)
public final class FactionsUuidCompat extends ProtectionCompatibility<FactionsPlugin> {
   public FactionsUuidCompat(@NotNull JavaPlugin mainPlugin, @NotNull FactionsPlugin plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return !((FactionsPlugin)this.getPlugin()).worldUtil().isEnabled(target.getWorld()) || FactionsBlockListener.playerCanBuildDestroyBlock(player, target, (PermissibleAction)PermissibleActions.BUILD, false);
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return !((FactionsPlugin)this.getPlugin()).worldUtil().isEnabled(target.getWorld()) || FactionsBlockListener.playerCanBuildDestroyBlock(player, target, (PermissibleAction)PermissibleActions.DESTROY, false);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return !((FactionsPlugin)this.getPlugin()).worldUtil().isEnabled(target.getWorld()) || FactionsPlayerListener.canUseBlock(player, target.getBlock().getType(), target, true);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return !((FactionsPlugin)this.getPlugin()).worldUtil().isEnabled(target.getWorld()) || FactionsPlayerListener.canInteractHere(player, target);
   }
}
