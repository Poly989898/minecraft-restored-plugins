package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setfaction",
   description = "Sets the target mob's faction"
)
public class SetFactionMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "faction",
      aliases = {"f"},
      description = "The faction to set on the target mob"
   )
   private PlaceholderString faction;

   public SetFactionMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.faction = mlc.getPlaceholderString(new String[]{"faction", "f"}, null);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.getPlugin().getMobManager().isActiveMob(target.getUniqueId())) {
         ActiveMob at = this.getPlugin().getMobManager().getActiveMob(target.getUniqueId()).get();
         at.setFaction(this.faction.get(data, target));
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.SUCCESS;
      }
   }
}
