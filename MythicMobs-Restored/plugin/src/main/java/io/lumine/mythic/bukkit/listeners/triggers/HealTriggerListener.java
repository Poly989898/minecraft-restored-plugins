package io.lumine.mythic.bukkit.listeners.triggers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.skills.triggers.meta.HealedMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealTriggerListener extends PluginModule<MythicBukkit> {
   public HealTriggerListener(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(EntityRegainHealthEvent.class, EventPriority.HIGHEST).filter(event -> !event.isCancelled()).handler(this::onHealTrigger).bindWith(this);
   }

   @Override
   public void unload() {
   }

   protected EventExecutor eventBus() {
      return this.getPlugin().getSkillManager().getEventBus();
   }

   private void onHealTrigger(EntityRegainHealthEvent event) {
      if (event.getEntity() instanceof LivingEntity) {
         AbstractEntity healed = BukkitAdapter.adapt(event.getEntity());
         SkillCaster caster = this.getPlugin().getSkillManager().getCasterNullable(healed);
         if (caster != null) {
            MythicLogger.debug(
               MythicLogger.DebugLevel.EVENT,
               "EntityRegainHealthEvent fired for {0}, amount={1}, reason={2}",
               healed.getName(),
               event.getAmount(),
               event.getRegainReason()
            );
            HealedMetadata triggerMetadata = new HealedMetadata(event, healed);
            AbstractLocation origin = healed.getLocation();
            SkillMetadata skillData = this.eventBus().buildSkillMetadata(SkillTriggers.HEALED, triggerMetadata, caster, healed, origin, true);
            TriggeredSkill ts = this.eventBus().processTriggerMechanics(skillData, triggerMetadata);
            if (ts.getCancelled()) {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "[Cancel Heal] - Cancelled by HEALED trigger skills");
               event.setCancelled(true);
            }
         }
      }
   }
}
