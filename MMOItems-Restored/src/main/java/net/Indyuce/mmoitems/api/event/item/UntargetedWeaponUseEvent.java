package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UntargetedWeaponUseEvent extends PlayerDataEvent implements Cancellable {
   private final Weapon weapon;
   private boolean cancelled;
   private static final HandlerList handlers = new HandlerList();

   public UntargetedWeaponUseEvent(@NotNull PlayerData playerData, @NotNull Weapon weapon) {
      super(playerData);
      this.weapon = weapon;
   }

   @NotNull
   public Weapon getWeapon() {
      return this.weapon;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
