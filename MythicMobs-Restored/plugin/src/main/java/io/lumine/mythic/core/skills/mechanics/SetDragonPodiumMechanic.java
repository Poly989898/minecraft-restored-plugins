package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.EnderDragon;

@MythicMechanic(
   author = "Seyarada",
   name = "SetDragonPodium",
   aliases = {"setPodium", "podium"},
   description = "Sets the dragon podium"
)
public class SetDragonPodiumMechanic extends SkillMechanic implements ITargetedLocationSkill {
   public SetDragonPodiumMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (!ServerVersion.isPaper() || !ServerVersion.isAfterOrEq(MinecraftVersions.v1_18_2)) {
         return SkillResult.INVALID_VERSION;
      } else if (!data.getCaster().getEntity().isLiving()) {
         return SkillResult.CONDITION_FAILED;
      } else if (data.getCaster().getEntity().getBukkitEntity() instanceof EnderDragon dragon) {
         dragon.setPodium(BukkitAdapter.adapt(target));
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.CONDITION_FAILED;
      }
   }
}
