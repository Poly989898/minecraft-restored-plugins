package com.nexomc.protectionlib.compatibilities;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.PermissibleActions;
import com.nexomc.protectionlib.ProtectionCompatibility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class FactionsUuidCompat extends ProtectionCompatibility<FactionsPlugin> {
   public FactionsUuidCompat(@NotNull JavaPlugin mainPlugin, @NotNull FactionsPlugin plugin) {
      super(mainPlugin, plugin);
   }

   @Override
   public boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return !isEnabled(target) || FactionsBlockListener.playerCanBuildDestroyBlock(player, target, PermissibleActions.BUILD, false);
   }

   @Override
   public boolean canBreak(@NotNull Player player, @NotNull Location target) {
      PermissibleAction action = PermissibleActions.DESTROY;
      return !isEnabled(target) || FactionsBlockListener.playerCanBuildDestroyBlock(player, target, action, false);
   }

   @Override
   public boolean canInteract(@NotNull Player player, @NotNull Location target) {
      return !isEnabled(target) || FactionsPlayerListener.canUseBlock(player, target.getBlock().getType(), target, true);
   }

   @Override
   public boolean canUse(@NotNull Player player, @NotNull Location target) {
      return !isEnabled(target) || FactionsPlayerListener.canInteractHere(player, target);
   }

   private boolean isEnabled(Location target) {
      return getPlugin().worldUtil().isEnabled(target.getWorld());
   }
}
