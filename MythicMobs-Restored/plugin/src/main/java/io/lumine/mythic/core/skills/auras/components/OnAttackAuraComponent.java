package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.listeners.triggers.DamageTriggerListeners;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.List;
import java.util.Optional;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class OnAttackAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onAttackSkill;
   private final boolean cancelDamage;
   private final boolean modDamage;
   private final PlaceholderString modDamageType;
   private final PlaceholderDouble damageAdd;
   private final PlaceholderDouble damageMult;
   private final List<SkillCondition> triggerConditions;

   public OnAttackAuraComponent(
      Aura.AuraTracker tracker,
      Optional<Skill> onAttackSkill,
      boolean cancelDamage,
      boolean modDamage,
      PlaceholderString modDamageType,
      PlaceholderDouble damageAdd,
      PlaceholderDouble damageMult,
      List<SkillCondition> triggerConditions
   ) {
      this.tracker = tracker;
      this.onAttackSkill = onAttackSkill;
      this.cancelDamage = cancelDamage;
      this.modDamage = modDamage;
      this.modDamageType = modDamageType;
      this.damageAdd = damageAdd;
      this.damageMult = damageMult;
      this.triggerConditions = triggerConditions;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.LOW)
                  .filter(event -> !event.isCancelled())
                  .filter(event -> event.getCause() == DamageCause.ENTITY_ATTACK)
                  .filter(event -> event.getDamager().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .handler(event -> {
                     AbstractEntity damaged = BukkitAdapter.adapt(event.getEntity());
                     Optional<Object> maybeData = damaged.getMetadata("skill-damage");
                     DamageMetadata damageData;
                     if (maybeData.isPresent()) {
                        damageData = (DamageMetadata)maybeData.get();
                        if (damageData.getDamageCause() != DamageCause.ENTITY_ATTACK || !damageData.getBoolean("trigger_skills")) {
                           return;
                        }
                     } else {
                        damageData = null;
                     }

                     AbstractEntity attacker = BukkitAdapter.adapt(event.getDamager());
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     EntityAttackMetadata attackMeta = new EntityAttackMetadata(event, damageData, attacker, damaged);
                     if (meta instanceof SkillMetadataImpl impl) {
                        impl.setTriggerMetadata(attackMeta);
                     }

                     attackMeta.applyToSkillMetadata(meta);
                     DamageTriggerListeners.applyPreAttackCooldown(meta, attacker);
                     meta.setTrigger(damaged);
                     if (this.triggerConditions != null) {
                        for (SkillCondition condition : this.triggerConditions) {
                           if (!condition.evaluateCaster(meta)) {
                              MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Condition {0} failed", condition);
                              return;
                           }
                        }
                     }

                     if (this.tracker.executeTargetedAuraSkill(this.onAttackSkill, meta, damaged)) {
                        this.tracker.consumeCharge();
                        if (this.cancelDamage) {
                           event.setCancelled(true);
                        } else if (this.modDamage) {
                           if (damageData == null) {
                              double damage = (event.getDamage() + this.damageAdd.get(meta, damaged)) * this.damageMult.get(meta, damaged);
                              event.setDamage(damage);
                           } else {
                              String modType = this.modDamageType == null ? null : this.modDamageType.get(meta, damaged);
                              if (modType == null) {
                                 damageData.setMultiplier(damageData.getMultiplier() + this.damageMult.get(meta, damaged) - 1.0);
                                 event.setDamage(event.getDamage() + this.damageAdd.get(meta, damaged));
                              } else {
                                 damageData.getBonusDamage().compute(modType, (k, v) -> {
                                    if (v == null) {
                                       v = 0.0;
                                    }

                                    v = v + this.damageAdd.get(meta, damaged);
                                    return v * this.damageMult.get(meta, damaged);
                                 });
                              }
                           }
                        }
                     }
                  })
            );
      }
   }

   @MythicAuraComponent(
      name = "onattack",
      aliases = {"onhit"},
      description = "Triggers a skill when the aura holder attacks"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onAttackSkillName;
      private Optional<Skill> onAttackSkill = Optional.empty();
      private final boolean cancelDamage;
      private final boolean modDamage;
      private final PlaceholderString modDamageType;
      private final PlaceholderDouble damageAdd;
      private final PlaceholderDouble damageMult;
      private final List<SkillCondition> triggerConditions;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onAttackSkillName = mlc.getString(new String[]{"onattackskill", "onattack", "oa", "onmelee", "onhitskill", "onhit", "oh"});
         this.modDamageType = mlc.getPlaceholderString(new String[]{"modDamageType", "damagetype"}, null);
         this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
         String damageAddRaw = mlc.getString(new String[]{"damageadd", "add", "a"}, null);
         String damageMultRaw = mlc.getString(new String[]{"damagemultiplier", "multiplier", "m"}, null);
         this.modDamage = damageAddRaw != null || damageMultRaw != null;
         this.damageAdd = damageAddRaw == null ? PlaceholderDouble.of("0") : PlaceholderDouble.of(damageAddRaw);
         this.damageMult = damageMultRaw == null ? PlaceholderDouble.of("1") : PlaceholderDouble.of(damageMultRaw);
         this.triggerConditions = mechanic.getConditions();
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onAttackSkillName != null) {
               this.onAttackSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onAttackSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnAttackAuraComponent(
            tracker, this.onAttackSkill, this.cancelDamage, this.modDamage, this.modDamageType, this.damageAdd, this.damageMult, this.triggerConditions
         );
      }
   }
}
