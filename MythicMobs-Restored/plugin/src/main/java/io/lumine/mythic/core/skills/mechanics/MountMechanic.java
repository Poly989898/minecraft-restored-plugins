package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "mount",
   aliases = {"vehicle"},
   description = "Summons a vehicle for the caster"
)
public class MountMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "strType",
      aliases = {"mob", "m", "type", "t"},
      defValue = "",
      description = "The type of mob to mount."
   )
   private final PlaceholderString strType;
   @MythicField(
      name = "stack",
      aliases = {"s"},
      defValue = "false",
      description = "If the summoned mob should stack atop existing mounted entities"
   )
   private final boolean stack;
   @MythicField(
      name = "mode",
      aliases = {"m"},
      description = "The mode to use for mount mechanic.\nValid Mode:\n- ADD -> Adds as passenger\n- SET -> Sets the passenger, fails if there is an existing passenger\n- REPLACE -> Replaces all current passengers.\n",
      defValue = "SET"
   )
   private final MountMechanic.Mode mode;

   public MountMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.strType = mlc.getPlaceholderString(new String[]{"mob", "m", "type", "t"}, "");
      this.stack = mlc.getBoolean(new String[]{"stack", "s"}, false);
      this.mode = mlc.getEnum(
         new String[]{"mode"}, MountMechanic.Mode.class, MountMechanic.Mode.SET, "Invalid input for 'mode' attribute. Valid inputs are: ADD, SET, or REPLACE"
      );
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      MythicMob mount = this.getPlugin().getMobManager().getMythicMob(this.strType.get(data)).orElseGet(() -> null);
      if (mount == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "The 'mob' attribute must be a valid MythicMob.");
         return SkillResult.INVALID_CONFIG;
      } else {
         ActiveMob mountMob = mount.spawn(data.getCaster().getEntity().getLocation(), data.getCaster().getLevel(), SpawnReason.SUMMON);
         AbstractEntity mountEntity = mountMob.getEntity();
         if (mountMob != null && mountEntity != null) {
            AbstractEntity mounting = data.getCaster().getEntity();
            if (this.stack) {
               AbstractEntity current = mounting.getVehicle();
               if (current != null) {
                  mounting.leaveVehicle();
                  current.setPassenger(mountEntity);
               }
            }

            this.mode.execute(mountEntity, mounting);
            mountMob.setOwnerUUID(mounting.getUniqueId());
            mountMob.setParent(data.getCaster());
            return SkillResult.SUCCESS;
         } else {
            return SkillResult.ERROR;
         }
      }
   }

   public static enum Mode {
      ADD,
      SET,
      REPLACE;

      public void execute(AbstractEntity vehicle, AbstractEntity passenger) {
         switch (this) {
            case ADD:
               vehicle.addPassenger(passenger);
               break;
            case SET:
               if (vehicle.getPassenger() != null) {
                  return;
               }

               vehicle.setPassenger(passenger);
               break;
            case REPLACE:
               for (AbstractEntity p : vehicle.getPassengers()) {
                  p.leaveVehicle();
               }

               vehicle.setPassenger(passenger);
         }
      }
   }
}
