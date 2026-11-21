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
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;

@MythicMechanic(
   author = "Ashijin",
   name = "enderDragonSpawnPortal",
   aliases = {"spawnEnderDragonPortal"},
   description = "Sets the game phase on an EnderDragon"
)
public class EnderDragonSpawnPortalMechanic extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "withPortals",
      aliases = {"wp", "p"},
      description = "Whether to generate the portal of the EnderDragon battle",
      defValue = "false"
   )
   private final boolean withPortals;

   public EnderDragonSpawnPortalMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.withPortals = mlc.getBoolean(new String[]{"withPortals", "wp", "p"}, false);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      location.getWorld().getEnderDragonBattle().generateEndPortal(this.withPortals);
      return SkillResult.SUCCESS;
   }
}
