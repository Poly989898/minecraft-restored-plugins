package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "summonPassenger",
   aliases = {"passenger", "summonRider", "rider"},
   description = "Summons a mob riding the caster"
)
public class SummonPassengerMechanic extends SkillMechanic implements INoTargetSkill {
   protected MythicMob mount;
   protected String strType;
   private boolean stack;
   @MythicField(
      name = "mode",
      aliases = {"m"},
      description = "The mode to use for mount mechanic.\nValid Mode:\n- ADD -> Adds as passenger\n- SET -> Sets the passenger, fails if there is an existing passenger\n- REPLACE -> Replaces all current passengers.\n",
      defValue = "SET"
   )
   private final MountMechanic.Mode mode;

   public SummonPassengerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.strType = mlc.getString(new String[]{"passenger", "p", "rider", "r", "type", "t"});
      this.stack = mlc.getBoolean(new String[]{"stack", "s"}, false);
      this.mount = MythicBukkit.inst().getMobManager().getMythicMob(this.strType).orElseGet(() -> null);
      this.mode = mlc.getEnum(
         new String[]{"mode"}, MountMechanic.Mode.class, MountMechanic.Mode.SET, "Invalid input for 'mode' attribute. Valid inputs are: ADD, SET, or REPLACE"
      );
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.mount == null) {
         this.mount = MythicBukkit.inst().getMobManager().getMythicMob(this.strType).orElseGet(() -> null);
         if (this.mount == null) {
            MythicLogger.errorMechanicConfig(this, this.config, "The 'mob' attribute must be a valid MythicMob.");
            return SkillResult.INVALID_CONFIG;
         }
      }

      AbstractEntity mountEntity = this.mount.spawn(data.getCaster().getEntity().getLocation(), data.getCaster().getLevel(), SpawnReason.SUMMON).getEntity();
      if (mountEntity == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         AbstractEntity mounting = data.getCaster().getEntity();
         if (this.stack && mounting.getPassenger() != null) {
            AbstractEntity current = mounting.getPassenger();
            mounting.setPassenger(current);
            current.setPassenger(mountEntity);
         } else {
            this.mode.execute(mounting, mountEntity);
         }

         MythicBukkit.inst().getMobManager().getActiveMob(mountEntity.getUniqueId()).get().setOwnerUUID(data.getCaster().getEntity().getUniqueId());
         return SkillResult.SUCCESS;
      }
   }
}
