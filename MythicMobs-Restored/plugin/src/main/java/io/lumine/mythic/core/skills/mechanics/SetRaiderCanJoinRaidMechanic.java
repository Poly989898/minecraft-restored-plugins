package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Raider;

@MythicMechanic(
   author = "Ashijin",
   name = "setRaiderCanJoinRaid",
   aliases = {"setCanJoinRaid"},
   version = "5.2",
   description = "Sets if the target raider can join a raid or not"
)
public class SetRaiderCanJoinRaidMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "can",
      aliases = {"b", "bool", "c"},
      description = "Whether the target raider can join raids",
      defValue = "true"
   )
   protected boolean bool;

   public SetRaiderCanJoinRaidMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.bool = mlc.getBoolean(new String[]{"bool", "b", "can", "c"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving() && target.getBukkitEntity() instanceof Raider raider) {
         Schedulers.runOnRegion(target.getBukkitEntity(), () -> raider.setCanJoinRaid(this.bool));
      }

      return SkillResult.SUCCESS;
   }
}
