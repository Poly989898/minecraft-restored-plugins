package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.CancellableTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleEventSkillMetadata;
import java.util.Optional;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public final class BukkitTriggerMetadata {
   public static SimpleEventSkillMetadata<EntityShootBowEvent> entityShootBow() {
      return SkillTriggerMetadata.simpleEvent(BukkitTriggerMetadata::setEntityShotBowEventData);
   }

   public static void setEntityShotBowEventData(EntityShootBowEvent event, SkillMetadata data) {
      CancellableTriggerMetadata.captureEvent(data, event);
      data.getVariables().putObject("shot-projectile", event.getEntity());
      data.getVariables().putFloat("bow-tension", event.getForce());
   }

   public static SimpleEventSkillMetadata<EntityBreedEvent> entityBreed() {
      return SkillTriggerMetadata.simpleEvent((event, data) -> {
         data.getVariables().putObject("father", BukkitAdapter.adapt(event.getFather()));
         data.getVariables().putObject("mother", BukkitAdapter.adapt(event.getMother()));
         data.getVariables().putObject("child", BukkitAdapter.adapt(event.getEntity()));
      });
   }

   public static SimpleEventSkillMetadata<ProjectileLaunchEvent> projectileLaunch() {
      return SkillTriggerMetadata.simpleEvent((event, data) -> data.getVariables().putObject("shot-projectile", event.getEntity()));
   }

   public static SimpleEventSkillMetadata<ProjectileHitEvent> projectileHit() {
      return SkillTriggerMetadata.simpleEvent((event, data) -> data.getVariables().putObject("shot-projectile", event.getEntity()));
   }

   public static SimpleEventSkillMetadata<EntityPortalEvent> entityPortal() {
      return SkillTriggerMetadata.simpleEvent((event, data) -> data.getVariables().putString("world", event.getTo().getWorld().getName()));
   }

   @Deprecated
   public static SkillMetadata apply(SkillMetadata meta, EntityDamageEvent event) {
      CancellableTriggerMetadata.captureEvent(meta, event);
      AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
      Optional<Object> maybeData = damaged.getMetadata("skill-damage");
      meta.getVariables().putDouble("damage-amount", event.getFinalDamage());
      meta.getVariables().putDouble("damage-amount-post", event.getFinalDamage());
      if (maybeData.isPresent()) {
         DamageMetadata data = (DamageMetadata)maybeData.get();
         String damageTags = String.join(",", data.getTags());
         meta.getVariables().putObject("damage-metadata", data);
         meta.getVariables().putString("damage-cause", "SKILL");
         meta.getVariables().putString("real-damage-cause", event.getCause().toString());
         meta.getVariables().putString("damage-type", data.getElement() != null ? data.getElement() : (damageTags.isEmpty() ? "SKILL" : damageTags));
         meta.getVariables().putString("damage-tags", damageTags);
      } else {
         meta.getVariables().putString("damage-cause", event.getCause().toString());
      }

      return meta;
   }

   @Deprecated
   public static SkillMetadata apply(SkillMetadata meta, EntityShootBowEvent event) {
      CancellableTriggerMetadata.captureEvent(meta, event);
      meta.getVariables().putObject("shot-projectile", event.getEntity());
      meta.getVariables().putFloat("bow-tension", event.getForce());
      return meta;
   }

   @Deprecated
   public static SkillMetadata apply(SkillMetadata meta, ProjectileLaunchEvent event) {
      CancellableTriggerMetadata.captureEvent(meta, event);
      meta.getVariables().putObject("shot-projectile", event.getEntity());
      return meta;
   }

   @Deprecated
   public static void apply(SkillMetadata meta, BlockBreakEvent event) {
      CancellableTriggerMetadata.captureEvent(meta, event);
   }

   @Deprecated
   public static void apply(SkillMetadata meta, BlockPlaceEvent event) {
      CancellableTriggerMetadata.captureEvent(meta, event);
   }
}
