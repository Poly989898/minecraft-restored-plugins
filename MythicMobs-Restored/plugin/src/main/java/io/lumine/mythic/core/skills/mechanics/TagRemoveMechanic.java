package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "tagremove",
   aliases = {"removetag", "removescoreboardtag"},
   description = "Removes a scoreboard tag from the target entity"
)
public class TagRemoveMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString tag;

   public TagRemoveMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.tag = PlaceholderString.of(mlc.getString(new String[]{"tag", "t"}, "default"));
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String tag = this.tag.get(data, target);
      target.removeScoreboardTag(tag);
      return SkillResult.SUCCESS;
   }
}
