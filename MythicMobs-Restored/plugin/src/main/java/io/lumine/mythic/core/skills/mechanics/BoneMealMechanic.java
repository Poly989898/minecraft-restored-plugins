package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

@MythicMechanic(
   author = "Phil",
   name = "bonemeal",
   aliases = {},
   description = "Applies bonemeal to the targeted location"
)
public class BoneMealMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "blockFace",
      aliases = {"bf", "face", "f"},
      defValue = "UP",
      description = "The block face to apply bonemeal to."
   )
   BlockFace blockFace;

   public BoneMealMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);

      try {
         this.blockFace = BlockFace.valueOf(mlc.getString(new String[]{"blockface", "bf", "face", "f"}, "UP").toUpperCase());
      } catch (IllegalArgumentException e) {
         this.blockFace = BlockFace.UP;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (BukkitAdapter.adapt(target).getBlock().getType() == Material.AIR) {
         return SkillResult.CONDITION_FAILED;
      } else {
         BukkitAdapter.adapt(target).getBlock().applyBoneMeal(this.blockFace);
         return SkillResult.SUCCESS;
      }
   }
}
