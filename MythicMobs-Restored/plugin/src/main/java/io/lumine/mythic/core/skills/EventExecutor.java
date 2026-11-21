package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillEventBus;
import io.lumine.mythic.api.skills.SkillManager;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.events.MythicTriggerEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatRegistrySnapshot;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EmptyMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleEventSkillMetadata;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.event.Event;

public class EventExecutor implements SkillEventBus {
   private final MythicPlugin plugin;
   private final SkillManager manager;

   public EventExecutor(MythicPlugin plugin, SkillExecutor manager) {
      this.plugin = plugin;
      this.manager = manager;
   }

   @Override
   public TriggeredSkill processTrigger(SkillTrigger trigger, SkillCaster caster, AbstractEntity triggerEntity) {
      return this.processTrigger(trigger, new EmptyMetadata(), caster, triggerEntity, caster.getLocation(), false);
   }

   @Override
   public TriggeredSkill processTrigger(SkillTrigger trigger, SkillCaster caster, AbstractEntity triggerEntity, boolean sync) {
      return this.processTrigger(trigger, new EmptyMetadata(), caster, triggerEntity, caster.getLocation(), sync);
   }

   @Override
   public TriggeredSkill processTrigger(
      SkillTrigger trigger, SkillTriggerMetadata triggerMetadata, SkillCaster caster, AbstractEntity triggerEntity, boolean sync
   ) {
      return this.processTrigger(trigger, triggerMetadata, caster, triggerEntity, caster.getLocation(), sync);
   }

   @Override
   public TriggeredSkill processTrigger(
      SkillTrigger trigger, SkillTriggerMetadata triggerMetadata, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin, boolean sync
   ) {
      return this.processTrigger(trigger, triggerMetadata, caster, triggerEntity, origin, sync, null);
   }

   @Override
   public TriggeredSkill processTrigger(
      SkillTrigger trigger, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin, boolean sync, Consumer<SkillMetadata> transformer
   ) {
      return this.processTrigger(trigger, null, caster, triggerEntity, origin, sync, transformer);
   }

   @Override
   public TriggeredSkill processTrigger(
      SkillTrigger trigger,
      SkillTriggerMetadata triggerMetadata,
      SkillCaster caster,
      AbstractEntity triggerEntity,
      AbstractLocation origin,
      boolean sync,
      Consumer<SkillMetadata> transformer
   ) {
      return this.processTrigger(trigger, triggerMetadata, caster, triggerEntity, origin, sync, transformer, null);
   }

   @Override
   public TriggeredSkill processTrigger(
      SkillTrigger trigger,
      SkillTriggerMetadata triggerMetadata,
      SkillCaster caster,
      AbstractEntity triggerEntity,
      AbstractLocation origin,
      boolean sync,
      Consumer<SkillMetadata> transformer,
      Collection<UUID> audience
   ) {
      SkillMetadataImpl data = this.buildSkillMetadata(trigger, caster, triggerEntity, origin, sync, audience);
      if (transformer != null) {
         transformer.accept(data);
      }

      Optional<? extends StatSnapshot> maybeRegistry = this.plugin.getStatManager().getStatRegistry(caster.getEntity());
      if (maybeRegistry.isPresent()) {
         StatSnapshot statRegistry = maybeRegistry.get();
         this.processStatsPre(data, triggerMetadata, statRegistry);
      }

      return this.processTriggerMechanics(data, triggerMetadata);
   }

   @Override
   public SkillMetadata buildSkillMetadata(SkillTrigger trigger, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin, boolean sync) {
      return this.buildSkillMetadata(trigger, null, caster, triggerEntity, origin, sync);
   }

   @Override
   public SkillMetadata buildSkillMetadata(
      SkillTrigger trigger, SkillTriggerMetadata triggerMetadata, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin, boolean sync
   ) {
      SkillMetadataImpl data = this.buildSkillMetadata(trigger, caster, triggerEntity, origin, sync, null);
      if (triggerMetadata != null) {
         data.setTriggerMetadata(triggerMetadata);
      }

      return data;
   }

   public SkillMetadataImpl buildSkillMetadata(
      SkillTrigger trigger, SkillCaster caster, AbstractEntity triggerEntity, AbstractLocation origin, boolean sync, Collection<UUID> audience
   ) {
      SkillMetadataImpl data = new SkillMetadataImpl(trigger, caster, triggerEntity);
      if (sync) {
         data.setIsAsync(false);
      } else {
         data.setIsAsync(true);
      }

      if (triggerEntity != null) {
         data.setEntityTarget(triggerEntity);
      }

      if (origin != null) {
         data.setOrigin(origin);
      } else {
         data.setOrigin(caster.getLocation());
      }

      if (audience != null) {
         data.setExecutionAudience(audience);
      }

      return data;
   }

   public <E extends Event> SkillMetadata buildSkillMetadata(
      SkillTrigger trigger,
      E event,
      SimpleEventSkillMetadata<E> triggerMetadata,
      SkillCaster caster,
      AbstractEntity triggerEntity,
      AbstractLocation origin,
      boolean sync
   ) {
      SkillMetadataImpl data = new SkillMetadataImpl(trigger, caster, triggerEntity);
      if (sync) {
         data.setIsAsync(false);
      } else {
         data.setIsAsync(true);
      }

      if (triggerEntity != null) {
         data.setEntityTarget(triggerEntity);
      }

      if (origin != null) {
         data.setOrigin(origin);
      } else {
         data.setOrigin(caster.getLocation());
      }

      if (triggerMetadata != null) {
         data.setTriggerMetadata(triggerMetadata);
         triggerMetadata.applyToSkillMetadata(event, data);
      }

      return data;
   }

   @Override
   public TriggeredSkill processTriggerMechanics(SkillMetadata data) {
      return this.processTriggerMechanics(data, null);
   }

   @Override
   public TriggeredSkill processTriggerMechanics(SkillMetadata data, SkillTriggerMetadata triggerMetadata) {
      SkillCaster caster = data.getCaster();
      AbstractEntity triggerEntity = data.getTrigger();
      if (triggerMetadata != null) {
         triggerMetadata.applyToSkillMetadata(data);
      }

      TriggeredSkill triggeredSkill = new TriggeredSkill(data);
      boolean sync = !data.isAsync();
      if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.EVENT)) {
         MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Calling MythicTriggerEvent for {0}", data.getCaster().getEntity().getName());
      }

      MythicTriggerEvent event = new MythicTriggerEvent(triggeredSkill, data.getCause(), triggerMetadata, data, sync);
      if (sync) {
         Events.call(event);
      } else {
         Events.callAsync(event);
      }

      if (!event.isCancelled() && caster instanceof ActiveMob activeMob) {
         if (triggerEntity != null) {
            ((ActiveMob)caster).setLastAggroCause(triggerEntity);
         }

         data.setPower(activeMob.getPower());
         if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.TRIGGER)) {
            MythicLogger.debug(
               MythicLogger.DebugLevel.TRIGGER,
               "Skill Trigger {0} fired for ActiveMob {1} (uuid: {2})",
               data.getCause(),
               activeMob.getDisplayName(),
               activeMob.getUniqueId()
            );
         }

         activeMob.getType().executeSkills(data.getCause(), data);
      }

      return triggeredSkill;
   }

   @Override
   public void processStatsPre(SkillMetadata data, SkillTriggerMetadata triggerMetadata, StatSnapshot statSnapshot) {
      if (statSnapshot instanceof StatRegistry statRegistry) {
         Collection<StatRegistry.StatMap> stats = statRegistry.getPreProcessStats(data.getCause());
         if (!stats.isEmpty() && triggerMetadata != null) {
            triggerMetadata.applyToSkillMetadata(data);
         }

         for (StatRegistry.StatMap stat : stats) {
            Double value = stat.get();
            if (value != 0.0) {
               stat.getType().processTrigger(data, triggerMetadata, statRegistry, value);
            }
         }
      } else if (statSnapshot instanceof StatRegistrySnapshot statRegistry) {
         Collection<StatRegistry.StatMap> stats = statRegistry.getBaseRegistry().getPreProcessStats(data.getCause());
         if (!stats.isEmpty() && triggerMetadata != null) {
            triggerMetadata.applyToSkillMetadata(data);
         }

         for (StatRegistry.StatMap statx : stats) {
            double value = statRegistry.get(statx.getType());
            if (value != 0.0) {
               statx.getType().processTrigger(data, triggerMetadata, statRegistry, value);
            }
         }
      }
   }

   @Override
   public void processStatsPost(SkillMetadata data, SkillTriggerMetadata triggerMetadata, StatSnapshot statSnapshot) {
      if (statSnapshot instanceof StatRegistry statRegistry) {
         for (StatRegistry.StatMap stat : statRegistry.getPostProcessStats(data.getCause())) {
            Double value = stat.get();
            if (value != 0.0) {
               stat.getType().processTrigger(data, triggerMetadata, statRegistry, value);
            }
         }
      } else if (statSnapshot instanceof StatRegistrySnapshot statRegistry) {
         for (StatRegistry.StatMap statx : statRegistry.getBaseRegistry().getPostProcessStats(data.getCause())) {
            double value = statRegistry.get(statx.getType());
            if (value != 0.0) {
               statx.getType().processTrigger(data, triggerMetadata, statRegistry, value);
            }
         }
      }
   }
}
