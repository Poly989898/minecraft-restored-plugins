package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joshinn",
   name = "mythicpack",
   aliases = {"pack", "haspack"},
   description = "Whether or not a specific pack is installed."
)
public class MythicPackInstalledCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "pack",
      aliases = {"p", "id", "packid"},
      description = "The pack to check for."
   )
   private String pack;

   public MythicPackInstalledCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.pack = mlc.getString(new String[]{"pack", "p", "id", "packid"}, "");
   }

   @Override
   public boolean check(SkillCaster caster) {
      return getPlugin().getPackManager().getPack(this.pack).isPresent();
   }
}
