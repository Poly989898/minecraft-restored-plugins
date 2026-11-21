package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "tagadd",
   aliases = {"addtag", "addscoreboardtag"},
   description = "Adds a scoreboard tag to the target entity"
)
public class TagAddMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString tag;

   public TagAddMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.tag = mlc.getPlaceholderString(new String[]{"tag", "t"}, "default");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String tag = this.tag.get(data, target);
      Runnable tagRunnable = () -> target.addScoreboardTag(tag);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(target.getBukkitEntity())) {
         Schedulers.of(target.getBukkitEntity()).run(tagRunnable);
      } else {
         tagRunnable.run();
      }

      return SkillResult.SUCCESS;
   }
}
