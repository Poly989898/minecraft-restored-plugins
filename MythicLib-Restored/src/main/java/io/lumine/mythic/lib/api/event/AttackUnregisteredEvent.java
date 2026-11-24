package io.lumine.mythic.lib.api.event;

import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class AttackUnregisteredEvent extends EntityEvent {
   private static final HandlerList handlers = new HandlerList();
   private final EntityDamageEvent event;
   private final AttackMetadata attack;

   public AttackUnregisteredEvent(EntityDamageEvent event, AttackMetadata attack) {
      super(event.getEntity());
      this.event = event;
      this.attack = attack;
   }

   @NotNull
   public AttackMetadata getAttack() {
      return this.attack;
   }

   @NotNull
   public DamageMetadata getDamage() {
      return this.attack.getDamage();
   }

   @NotNull
   public LivingEntity getEntity() {
      return this.attack.getTarget();
   }

   public EntityDamageEvent toBukkit() {
      return this.event;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
