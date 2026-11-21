package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.boss.DragonBattle.RespawnPhase;

@MythicMechanic(
   author = "Ashijin",
   name = "enderDragonSetRespawnPhase",
   aliases = {"setEnderDragonRespawnPhase"},
   description = "Sets the game phase on an EnderDragon"
)
public class EnderDragonSetRespawnPhaseMechanic extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "phase",
      aliases = {"p"},
      description = "The EnderDragon respawn phase to set",
      defValue = "NONE"
   )
   private RespawnPhase phase = RespawnPhase.NONE;

   public EnderDragonSetRespawnPhaseMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strPhase = mlc.getString(new String[]{"phase", "p"}, "NONE").toUpperCase();

      try {
         this.phase = RespawnPhase.valueOf(strPhase);
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid phase supplied");
         this.phase = RespawnPhase.NONE;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      location.getWorld().getEnderDragonBattle().setRespawnPhase(this.phase);
      return SkillResult.SUCCESS;
   }
}
