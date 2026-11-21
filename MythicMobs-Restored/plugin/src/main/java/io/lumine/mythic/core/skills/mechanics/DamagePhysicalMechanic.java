package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.listeners.triggers.DamageTriggerListeners;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@MythicMechanic(
   author = "Ashijin",
   name = "physicalDamage",
   aliases = {"meleeHit", "hit"},
   description = ""
)
public class DamagePhysicalMechanic extends DamagingMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "multiplier",
      aliases = {"m"},
      description = "The percentage of damage to deal",
      defValue = "1"
   )
   protected PlaceholderDouble multiplier;
   @MythicField(
      name = "forcedDamage",
      aliases = {"fd, forced"},
      description = "Forces the damage to be the amount specified, regardless of the damage of the entity"
   )
   protected PlaceholderDouble forcedDamage;
   @MythicField(
      name = "scaleByAttackCooldown",
      aliases = {"fd, forced"},
      description = "Scales the damage by the weapon's attack cooldown"
   )
   protected PlaceholderBoolean scaleByAttackCooldown;

   public DamagePhysicalMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.triggerSkills = mlc.getBoolean(new String[]{"triggerskills", "ts"}, true);
      this.multiplier = mlc.getPlaceholderDouble(new String[]{"multiplier", "m"}, 1.0);
      this.forcedDamage = mlc.getPlaceholderDouble(new String[]{"forcedDamage", "fd", "forced"}, 0.0);
      this.scaleByAttackCooldown = mlc.getPlaceholderBoolean(new String[]{"scaleByAttackCooldown", "sbac"}, false);
      this.cause = DamageCause.ENTITY_ATTACK;
      this.tags.add("melee");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && !(target.getHealth() <= 0.0)) {
         SkillCaster caster = data.getCaster();
         if (caster instanceof ActiveMob am && am.isUsingDamageSkill()) {
            return SkillResult.INVALID_TARGET;
         } else {
            AbstractEntity entity = caster.getEntity();
            double multiplier = this.multiplier.get(data, target);
            double damage;
            if (this.forcedDamage.get(data, target) != 0.0) {
               damage = this.forcedDamage.get(data, target);
            } else {
               if (entity.isLiving()) {
                  AttributeInstance attribute = ((LivingEntity)entity.getBukkitEntity()).getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE"));
                  if (attribute != null) {
                     damage = attribute.getValue();
                  } else if (caster instanceof ActiveMob am) {
                     damage = am.getDamage();
                  } else {
                     damage = 1.0;
                  }
               } else if (caster instanceof ActiveMob am) {
                  damage = am.getDamage();
               } else {
                  damage = 1.0;
               }

               if (this.powerAffectsDamage) {
                  damage *= data.getPower();
               } else {
                  damage = damage;
               }

               if (this.scaleByAttackCooldown.get(data, target) && caster.getEntity().isPlayer()) {
                  double cdMultiplier;
                  if (data.getVariables().has("hit-attack-cooldown")) {
                     cdMultiplier = data.getVariables().getDouble("hit-attack-cooldown");
                  } else {
                     Player player = (Player)caster.getEntity().asPlayer().getBukkitEntity();
                     Double preAttackCd = DamageTriggerListeners.consumePreAttackCooldown(player.getUniqueId());
                     if (preAttackCd != null) {
                        cdMultiplier = preAttackCd;
                     } else {
                        cdMultiplier = player.getAttackCooldown();
                        MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().resetItemRecharge(player);
                     }

                     data.getVariables().putDouble("hit-attack-cooldown", cdMultiplier);
                  }

                  multiplier *= cdMultiplier;
               }
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ DamageBaseMechanic fired for {0} with {1} power", damage, data.getPower());
            this.doDamage(data, target, damage, multiplier);
            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
