package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.entities.properties.ColoredProperty;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Entity;

@MythicMechanic(
   author = "Ashijin",
   name = "setmobcolor",
   aliases = {"setcolor"},
   description = "Sets the color the target entity. Must be a colorable entity."
)
public class SetMobColorMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected ColoredProperty color;

   public SetMobColorMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String strColor = mlc.getString(new String[]{"color", "c"}, "WHITE").toUpperCase();
      this.color = new ColoredProperty(strColor);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Entity bukkitTarget = target.getBukkitEntity();
      Schedulers.runOnRegion(bukkitTarget, () -> this.color.applyProperties(bukkitTarget));
      return SkillResult.SUCCESS;
   }
}
