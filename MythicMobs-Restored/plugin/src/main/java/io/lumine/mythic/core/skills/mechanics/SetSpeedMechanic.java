package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "setspeed",
   description = "Sets the speed attribute of the target entity"
)
public class SetSpeedMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "speed",
      aliases = {"s", "amount", "a", "value", "v", "multiplier", "m"},
      description = "Speed multiplier/value applied by this mechanic",
      defValue = "1"
   )
   protected PlaceholderFloat speed;
   @MythicField(
      name = "type",
      aliases = {"t"},
      description = "Whether to modify walking or flying speed. Valid values: WALKING, FLYING",
      defValue = "WALKING"
   )
   protected SetSpeedMechanic.Type type;

   public SetSpeedMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strType = mlc.getString(new String[]{"type", "t"}, "WALKING").toUpperCase();
      this.speed = mlc.getPlaceholderFloat(new String[]{"speed", "s", "amount", "a", "value", "v", "multiplier", "m"}, 1.0F);
      if (strType.contains("FLY")) {
         this.type = SetSpeedMechanic.Type.FLYING;
      } else {
         this.type = SetSpeedMechanic.Type.WALKING;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isLiving()) {
         return SkillResult.INVALID_TARGET;
      } else {
         float speed;
         if (this.getPlugin().getSkillManager().getCaster(target) instanceof ActiveMob activeMob) {
            MythicMob mobType = activeMob.getType();
            double defaultSpeed = mobType.getMovementSpeed(activeMob);
            if (defaultSpeed < 0.0) {
               LivingEntity livingEntity = (LivingEntity)BukkitAdapter.adapt(target);
               if (this.type == SetSpeedMechanic.Type.FLYING) {
                  Optional<Double> maybeDefault = activeMob.getType().getEntityType().getAttributeDefault(BukkitAttribute.getAttribute("FLYING_SPEED"));
                  if (maybeDefault.isPresent()) {
                     defaultSpeed = maybeDefault.get();
                  } else {
                     defaultSpeed = 0.0;
                  }
               } else {
                  Optional<Double> maybeDefault = activeMob.getType().getEntityType().getAttributeDefault(BukkitAttribute.getAttribute("MOVEMENT_SPEED"));
                  if (maybeDefault.isPresent()) {
                     defaultSpeed = maybeDefault.get();
                  } else {
                     defaultSpeed = 0.0;
                  }
               }
            }

            speed = (float)(this.speed.get(data, target) * defaultSpeed);
         } else {
            speed = this.speed.get(data, target) * this.type.defaultSpeed;
         }

         if (target.isPlayer()) {
            Player p = (Player)BukkitAdapter.adapt(target);
            if (this.type == SetSpeedMechanic.Type.FLYING) {
               p.setFlySpeed(speed);
            } else {
               p.setWalkSpeed(speed);
            }
         } else if (target.isLiving()) {
            LivingEntity l = (LivingEntity)BukkitAdapter.adapt(target);
            if (this.type == SetSpeedMechanic.Type.FLYING) {
               AttributeInstance attr = l.getAttribute(BukkitAttribute.getAttribute("FLYING_SPEED"));
               if (attr != null) {
                  attr.setBaseValue(speed);
               }
            } else {
               AttributeInstance attr = l.getAttribute(BukkitAttribute.getAttribute("MOVEMENT_SPEED"));
               if (attr != null) {
                  attr.setBaseValue(speed);
               }
            }
         }

         return SkillResult.SUCCESS;
      }
   }

   protected static enum Type {
      WALKING(0.1F),
      FLYING(0.2F);

      private final float defaultSpeed;

      private Type(float defaultSpeed) {
         this.defaultSpeed = defaultSpeed;
      }
   }
}
