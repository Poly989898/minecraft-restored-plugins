package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.heal.HealMetadata;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.jetbrains.annotations.NotNull;

public class MythicHealMechanicEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   boolean cancelled = false;
   @Getter
   private final SkillMetadata metadata;
   @Getter
   private final HealMetadata healMeta;

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   @NotNull
   public Entity getTarget() {
      return this.healMeta.getTarget().getBukkitEntity();
   }

   public double getHealAmount() {
      return this.healMeta.getAmount();
   }

   public void setHealAmount(double heal) {
      this.healMeta.setAmount(heal);
   }

   public double getFinalAmount() {
      return this.healMeta.getAmount() * this.healMeta.getMultiplier();
   }

   public MythicHealMechanicEvent(@NotNull SkillMetadata metadata, @NotNull HealMetadata healMeta) {
      super(healMeta.getHealer().getEntity().getBukkitEntity());
      this.metadata = metadata;
      this.healMeta = healMeta;
   }

   public static MythicHealMechanicEvent call(@NotNull SkillMetadata metadata, @NotNull HealMetadata healMeta) {
      MythicHealMechanicEvent mythicEvent = new MythicHealMechanicEvent(metadata, healMeta);
      Entity targetEntity = healMeta.getTarget().getBukkitEntity();
      if (ServerVersion.isFolia() && FoliaHelper.isOwnedByCurrentRegion(targetEntity)) {
         Events.call(mythicEvent);
      } else {
         Events.callSyncAndJoin(targetEntity, mythicEvent);
      }

      if (mythicEvent.isCancelled()) {
         return mythicEvent;
      } else {
         EntityRegainHealthEvent bukkitEvent = new EntityRegainHealthEvent(targetEntity, mythicEvent.getFinalAmount(), RegainReason.CUSTOM);
         if (ServerVersion.isFolia() && FoliaHelper.isOwnedByCurrentRegion(targetEntity)) {
            Events.call(bukkitEvent);
         } else {
            Events.callSyncAndJoin(targetEntity, bukkitEvent);
         }

         if (bukkitEvent.isCancelled()) {
            mythicEvent.setCancelled(true);
            return mythicEvent;
         } else {
            mythicEvent.setHealAmount(bukkitEvent.getAmount());
            healMeta.setMultiplier(1.0);
            return mythicEvent;
         }
      }
   }
}
