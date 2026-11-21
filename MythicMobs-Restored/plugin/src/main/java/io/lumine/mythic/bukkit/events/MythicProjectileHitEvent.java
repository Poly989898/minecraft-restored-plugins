package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Setter
public class MythicProjectileHitEvent extends Event {
   private final Projectile.ProjectileTracker projectileTracker;
   private final AbstractEntity hitEntity;
   @Getter
   private boolean cancelled = false;
   private static final HandlerList handlers = new HandlerList();

   public MythicProjectileHitEvent(Projectile.ProjectileTracker projectileTracker, AbstractEntity hitEntity) {
      this.projectileTracker = projectileTracker;
      this.hitEntity = hitEntity;
   }

   public Projectile.ProjectileTracker getProjectile() {
      return this.projectileTracker;
   }

   public AbstractEntity getEntity() {
      return this.hitEntity;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
