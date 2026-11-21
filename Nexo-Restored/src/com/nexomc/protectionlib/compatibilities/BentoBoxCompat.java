package com.nexomc.protectionlib.compatibilities;

import com.nexomc.protectionlib.ProtectionCompatibility;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0018\u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0012\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u0013\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/protectionlib/compatibilities/BentoBoxCompat;", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lworld/bentobox/bentobox/BentoBox;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lworld/bentobox/bentobox/BentoBox;)V", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canDo", "flag", "Lworld/bentobox/bentobox/api/flags/Flag;", "canBreak", "canInteract", "canUse", "protectionlib"}
)
public final class BentoBoxCompat extends ProtectionCompatibility<BentoBox> {
   public BentoBoxCompat(@NotNull JavaPlugin mainPlugin, @NotNull BentoBox plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super(mainPlugin, (Plugin)plugin);
   }

   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      Flag var10003 = Flags.PLACE_BLOCKS;
      Intrinsics.checkNotNullExpressionValue(var10003, "PLACE_BLOCKS");
      return this.canDo(player, target, var10003);
   }

   private final boolean canDo(Player player, Location target, Flag flag) {
      return !((BentoBox)this.getPlugin()).getIWM().inWorld(target) || (Boolean)((BentoBox)this.getPlugin()).getIslands().getIslandAt(target).map(BentoBoxCompat::canDo$lambda$1).orElse(!flag.isSetForWorld(target.getWorld()));
   }

   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      Flag var10003 = Flags.BREAK_BLOCKS;
      Intrinsics.checkNotNullExpressionValue(var10003, "BREAK_BLOCKS");
      return this.canDo(player, target, var10003);
   }

   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return !((BentoBox)this.getPlugin()).getIWM().inWorld(target) || ((BentoBox)this.getPlugin()).getIslands().locationIsOnIsland(player, target);
   }

   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      return !((BentoBox)this.getPlugin()).getIWM().inWorld(target) || ((BentoBox)this.getPlugin()).getIslands().locationIsOnIsland(player, target);
   }

   private static final Boolean canDo$lambda$0(Player $player, Flag $flag, Island it) {
      return it.isAllowed(User.getInstance($player), $flag);
   }

   private static final Boolean canDo$lambda$1(Function1 $tmp0, Object p0) {
      return (Boolean)$tmp0.invoke(p0);
   }
}
