package io.lumine.mythic.bukkit.listeners;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Optional;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class ThreatTableListeners implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void MobTargetEventNoOwner(EntityTargetLivingEntityEvent e) {
      if (e.getTarget() != null) {
         if (MythicBukkit.inst().getMobManager().isActiveMob(e.getEntity().getUniqueId())) {
            if (e.getEntity() instanceof LivingEntity) {
               ActiveMob amE = MythicBukkit.inst().getMobManager().getMythicMobInstance((LivingEntity)e.getEntity());
               if (amE.getOwnerUUID() != null && amE.getOwnerUUID().isPresent() && amE.getOwnerUUID().get().equals(e.getTarget().getUniqueId())) {
                  e.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void MobTargetEventNoFaction(EntityTargetLivingEntityEvent event) {
      if (event.getTarget() != null) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(event.getEntity());
         if (am != null) {
            if (am.getFaction() != null) {
               AbstractEntity target = BukkitAdapter.adapt(event.getTarget());
               target.getFaction().ifPresent(faction -> {
                  if (faction.equals(am.getFaction())) {
                     event.setCancelled(true);
                  }
               });
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void MobTargetEvent(EntityTargetLivingEntityEvent event) {
      if (event.getTarget() != null) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(event.getEntity());
         if (am != null && !am.isDead() && am.getThreatTable() != null) {
            AbstractEntity target = BukkitAdapter.adapt(event.getTarget());
            if (am.getFaction() != null) {
               Optional<String> maybeFaction = target.getFaction();
               if (maybeFaction.isPresent() && maybeFaction.get().equals(am.getFaction())) {
                  return;
               }
            }

            MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Target Change Event called, reviewing Threat Table");
            if (!am.getThreatTable().targetEvent(target)) {
               event.setCancelled(true);
               am.setTarget(am.getThreatTable().getTopThreatHolder());
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void MobTargetEvent(EntityTargetEvent e) {
      if (e.getEntity() instanceof LivingEntity) {
         if (e.getTarget() != null) {
            ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance((LivingEntity)e.getEntity());
            if (am != null && am.getType() != null) {
               if (!am.isDead() && am.getType().usesThreatTable() && !am.getThreatTable().inCombat()) {
                  AbstractEntity target = BukkitAdapter.adapt(e.getTarget());
                  if (am.getFaction() != null) {
                     Optional<String> maybeFaction = target.getFaction();
                     if (maybeFaction.isPresent() && maybeFaction.get().equals(am.getFaction())) {
                        return;
                     }
                  }

                  MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Target Change Event called, reviewing Threat Table");
                  if (!am.getThreatTable().targetEvent(BukkitAdapter.adapt(e.getTarget()))) {
                     e.setCancelled(true);
                     am.setTarget(am.getThreatTable().getTopThreatHolder());
                  }
               }
            }
         }
      }
   }
}
