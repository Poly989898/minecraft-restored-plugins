package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;

@MythicMechanic(
   author = "Ashijin",
   name = "enderDragonResetCrystals",
   aliases = {"resetEnderResetCrystals"},
   description = "Generates the EnderDragon crystals"
)
public class EnderDragonResetCrystalsMechanic extends SkillMechanic implements ITargetedLocationSkill {
   public EnderDragonResetCrystalsMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      location.getWorld().getEnderDragonBattle().resetCrystals();
      return SkillResult.SUCCESS;
   }
}
