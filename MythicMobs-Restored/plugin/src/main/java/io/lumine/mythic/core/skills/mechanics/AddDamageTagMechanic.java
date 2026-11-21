package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "addDamageTag",
   description = "Adds a damage tag to the trigger data"
)
public class AddDamageTagMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "tag",
      aliases = {"t", "name", "n"},
      description = "Name of the tag to add",
      defValue = "null"
   )
   protected final PlaceholderString tag;

   public AddDamageTagMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.tag = mlc.getPlaceholderString(new String[]{"tag", "t", "name", "n"}, null);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String tag = this.tag.get(data, target);
      if (tag == null) {
         return SkillResult.SUCCESS;
      } else {
         if (data.getTriggerMetadata() != null) {
            data.getTriggerMetadata().putBoolean(tag, true);
         }

         return SkillResult.SUCCESS;
      }
   }
}
