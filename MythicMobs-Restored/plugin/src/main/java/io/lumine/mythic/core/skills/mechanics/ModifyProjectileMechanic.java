package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "modifyprojectile",
   description = "Modifies an attribute of the projectile that executed the mechanic"
)
public class ModifyProjectileMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "trait",
      aliases = {"t"},
      defValue = "VELOCITY",
      description = "The trait of the projectile to modify."
   )
   private ModifyProjectileMechanic.ProjectileTrait trait;
   @MythicField(
      name = "action",
      aliases = {"a"},
      defValue = "MULTIPLY",
      description = "The action to perform for modifying the projectile trait."
   )
   private ModifyProjectileMechanic.ModifyAction action;
   @MythicField(
      name = "value",
      aliases = {"v"},
      defValue = "0",
      description = "The value to use for the modification."
   )
   private final PlaceholderDouble value;

   public ModifyProjectileMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String t = mlc.getString(new String[]{"trait", "t"}, "VELOCITY");

      try {
         this.trait = ModifyProjectileMechanic.ProjectileTrait.valueOf(t.toUpperCase());
      } catch (Exception e) {
         e.printStackTrace();
      }

      String a = mlc.getString(new String[]{"action", "a"}, "MULTIPLY");

      try {
         this.action = ModifyProjectileMechanic.ModifyAction.valueOf(a.toUpperCase());
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      this.value = mlc.getPlaceholderDouble(new String[]{"value", "v"}, 0.0);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCallingEvent() instanceof ProjectileMechanic.ProjectileMechanicTracker projectileTracker) {
         this.apply(projectileTracker, this.action, this.trait, (float)this.value.get(data));
      } else if (data.getCallingEvent() instanceof MissileMechanic.MissileTracker missileTracker) {
         this.apply(missileTracker, this.action, this.trait, this.value.get(data));
      } else if (data.getCallingEvent() instanceof OrbitalMechanic.OrbitalTracker orbitalTracker) {
         this.apply(orbitalTracker, this.action, this.trait, this.value.get(data));
      }

      return SkillResult.SUCCESS;
   }

   void apply(
      ProjectileMechanic.ProjectileMechanicTracker projectile,
      ModifyProjectileMechanic.ModifyAction action,
      ModifyProjectileMechanic.ProjectileTrait trait,
      float value
   ) {
      switch (action) {
         case MULTIPLY:
            switch (trait) {
               case GRAVITY:
                  projectile.multiplyGravity(value);
                  return;
               case POWER:
                  projectile.multiplyPower(value);
                  return;
               case VELOCITY:
                  projectile.multiplyVelocity(value);
                  return;
               default:
                  return;
            }
         case ADD:
            switch (trait) {
               case GRAVITY:
                  projectile.addGravity(value);
                  return;
               case POWER:
                  projectile.addPower(value);
                  return;
               case VELOCITY:
                  projectile.addVelocity(value);
                  return;
               default:
                  return;
            }
         case SET:
            switch (trait) {
               case GRAVITY:
                  projectile.setGravity(value);
                  break;
               case POWER:
                  projectile.setPower(value);
                  break;
               case VELOCITY:
                  projectile.setVelocity(value);
            }
      }
   }

   void apply(
      MissileMechanic.MissileTracker missile, ModifyProjectileMechanic.ModifyAction action, ModifyProjectileMechanic.ProjectileTrait trait, double value
   ) {
      switch (action) {
         case MULTIPLY:
            switch (trait) {
               case INERTIA:
                  missile.multiplyInertia((float)value);
                  return;
               case GRAVITY:
               default:
                  return;
               case POWER:
                  missile.multiplyPower((float)value);
                  return;
               case VELOCITY:
                  missile.multiplyVelocity(value);
                  return;
            }
         case ADD:
            switch (trait) {
               case INERTIA:
                  missile.addInertia((float)value);
                  return;
               case GRAVITY:
               default:
                  return;
               case POWER:
                  missile.addPower((float)value);
                  return;
               case VELOCITY:
                  missile.addVelocity(value);
                  return;
            }
         case SET:
            switch (trait) {
               case INERTIA:
                  missile.setInertia((float)value);
               case GRAVITY:
               default:
                  break;
               case POWER:
                  missile.setPower((float)value);
                  break;
               case VELOCITY:
                  missile.setVelocity(value);
            }
      }
   }

   void apply(
      OrbitalMechanic.OrbitalTracker orbital, ModifyProjectileMechanic.ModifyAction action, ModifyProjectileMechanic.ProjectileTrait trait, double value
   ) {
      switch (action) {
         case MULTIPLY:
            switch (trait) {
               case RADIUS:
                  orbital.multiplyRadius((float)value);
                  return;
               case YOFFSET:
                  orbital.multiplyYOffset((float)value);
                  return;
               default:
                  return;
            }
         case ADD:
            switch (trait) {
               case RADIUS:
                  orbital.addRadius((float)value);
                  return;
               case YOFFSET:
                  orbital.addYOffset((float)value);
                  return;
               default:
                  return;
            }
         case SET:
            switch (trait) {
               case RADIUS:
                  orbital.setRadius((float)value);
                  break;
               case YOFFSET:
                  orbital.setYOffset((float)value);
            }
      }
   }

   private static enum ModifyAction {
      MULTIPLY,
      ADD,
      SET;
   }

   private static enum ProjectileTrait {
      INERTIA,
      GRAVITY,
      POWER,
      VELOCITY,
      RADIUS,
      YOFFSET;
   }
}
