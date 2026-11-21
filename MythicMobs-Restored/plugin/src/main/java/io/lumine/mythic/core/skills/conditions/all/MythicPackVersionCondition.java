package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joshinn",
   name = "packversion",
   aliases = {"mythicpackversion", "packversionis"},
   description = "Whether or not a packs version matches a specific version."
)
public class MythicPackVersionCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "pack",
      aliases = {"p", "id", "packid"},
      description = "The pack to check for."
   )
   private String pack;
   @MythicField(
      name = "packVersion",
      aliases = {"packV", "version", "v"},
      description = "The pack version to check for."
   )
   private String packVersion;

   public MythicPackVersionCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.pack = mlc.getString(new String[]{"pack", "p", "id", "packid"}, "");
      this.packVersion = mlc.getString(new String[]{"packVersion", "packV", "version", "v"}, "");
   }

   @Override
   public boolean check(SkillCaster caster) {
      return getPlugin().getPackManager().getPack(this.pack).isPresent()
         && getPlugin().getPackManager().getPack(this.pack).get().getVersion().equals(this.packVersion);
   }
}
