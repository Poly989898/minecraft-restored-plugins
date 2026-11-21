package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.listeners.triggers.DamageTriggerListeners;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@MythicMechanic(
   author = "Ashijin",
   name = "onattack",
   aliases = {"onhit"},
   description = "Applies an aura to the target that triggers a skill when they damage something",
   deprecated = true
)
public class OnAttackMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onAttack",
      aliases = {"oa"},
      version = "4.6",
      description = "Skill to execute if the target hits something"
   )
   protected Optional<Skill> onAttackSkill = Optional.empty();
   protected String onAttackSkillName;
   @MythicField(
      name = "cancelEvent",
      aliases = {"ce"},
      defValue = "false",
      version = "4.6",
      description = "Whether or not to cancel the event that triggered the aura"
   )
   protected boolean cancelDamage;
   protected boolean modDamage = false;
   @MythicField(
      name = "modDamageType",
      aliases = {"dt"},
      defValue = "0",
      version = "4.6",
      description = ""
   )
   protected PlaceholderString modDamageType;
   @MythicField(
      name = "damageAdd",
      aliases = {"a"},
      defValue = "0",
      version = "4.6",
      description = ""
   )
   protected PlaceholderDouble damageAdd;
   @MythicField(
      name = "damageMultiplier",
      aliases = {"m"},
      defValue = "1",
      version = "4.6",
      description = ""
   )
   protected PlaceholderDouble damageMult;

   public OnAttackMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onAttackSkillName = mlc.getString(new String[]{"onattackskill", "onattack", "oa", "onmelee", "onhitskill", "onhit", "oh"});
      this.modDamageType = mlc.getPlaceholderString(new String[]{"modDamageType", "damagetype"}, null);
      this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
      String damageAdd = mlc.getString(new String[]{"damageadd", "add", "a"}, null);
      String damageMult = mlc.getString(new String[]{"damagemultiplier", "multiplier", "m"}, null);
      if (damageAdd != null || damageMult != null) {
         this.modDamage = true;
      }

      if (damageAdd == null) {
         this.damageAdd = PlaceholderDouble.of("0");
      } else {
         this.damageAdd = PlaceholderDouble.of(damageAdd);
      }

      if (damageMult == null) {
         this.damageMult = PlaceholderDouble.of("1");
      } else {
         this.damageMult = PlaceholderDouble.of(damageMult);
      }

      this.getManager().queueSecondPass(() -> {
         if (this.onAttackSkillName != null) {
            this.onAttackSkill = this.getManager().getSkill(file, this, this.onAttackSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnAttackMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.LOW)
               .filter(event -> !event.isCancelled())
               .filter(event -> event.getCause() == DamageCause.ENTITY_ATTACK)
               .filter(event -> event.getDamager().getUniqueId().equals(this.entity.get().getUniqueId()))
               .handler(
                  event -> {
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

                     SkillMetadata meta = this.skillMetadata.deepClone();
                     AbstractEntity target = BukkitAdapter.adapt(event.getEntity());
                     AbstractEntity attacker = BukkitAdapter.adapt(event.getDamager());
                     meta.setTrigger(target);
                     EntityAttackMetadata attackMeta = new EntityAttackMetadata(event, damageData, attacker, target);
                     if (meta instanceof SkillMetadataImpl impl) {
                        impl.setTriggerMetadata(attackMeta);
                     }

                     attackMeta.applyToSkillMetadata(meta);
                     DamageTriggerListeners.applyPreAttackCooldown(meta, attacker);
                     if (this.executeTargetedAuraSkill(OnAttackMechanic.this.onAttackSkill, meta, target)) {
                        this.consumeCharge();
                        if (OnAttackMechanic.this.cancelDamage) {
                           event.setCancelled(true);
                        } else if (OnAttackMechanic.this.modDamage) {
                           if (damageData == null) {
                              double damage = (event.getDamage() + OnAttackMechanic.this.damageAdd.get(meta, target))
                                 * OnAttackMechanic.this.damageMult.get(meta, target);
                              event.setDamage(damage);
                           } else {
                              String modDamageType = OnAttackMechanic.this.modDamageType == null ? null : OnAttackMechanic.this.modDamageType.get(meta, target);
                              if (modDamageType == null) {
                                 damageData.setMultiplier(damageData.getMultiplier() * OnAttackMechanic.this.damageMult.get(meta, target));
                                 event.setDamage(event.getDamage() + OnAttackMechanic.this.damageAdd.get(meta, target));
                              } else {
                                 damageData.getBonusDamage().compute(modDamageType, (k, v) -> {
                                    if (v == null) {
                                       v = 0.0;
                                    }

                                    v = v + OnAttackMechanic.this.damageAdd.get(meta, target);
                                    return v * OnAttackMechanic.this.damageMult.get(meta, target);
                                 });
                              }
                           }
                        }
                     }
                  }
               )
         );
         this.executeAuraSkill(OnAttackMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
