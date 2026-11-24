package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpecialWeaponAttackEvent extends PlayerDataEvent implements Cancellable {
   private final Weapon weapon;
   private final LivingEntity target;
   private boolean cancelled;
   private static final HandlerList handlers = new HandlerList();

   public SpecialWeaponAttackEvent(@NotNull PlayerData var1, @NotNull Weapon var2, @NotNull LivingEntity var3) {
      super(var1);
      this.weapon = var2;
      this.target = var3;
   }

   @NotNull
   public Weapon getWeapon() {
      return this.weapon;
   }

   @NotNull
   public LivingEntity getTarget() {
      return this.target;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
