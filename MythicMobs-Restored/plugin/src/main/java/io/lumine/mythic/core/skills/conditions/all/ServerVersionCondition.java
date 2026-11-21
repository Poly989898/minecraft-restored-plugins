package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersion;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joshinn",
   name = "serverversion",
   aliases = {"server", "version"},
   description = "Whether or not the server is a specific version"
)
public class ServerVersionCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "version",
      aliases = {"v", "sv"},
      description = "The version to check for",
      defValue = "1.19.3"
   )
   private MinecraftVersion version;

   public ServerVersionCondition(String line, MythicLineConfig mlc) {
      super(line);
      String version = mlc.getString(new String[]{"version", "v", "sv"}, "1.19.3");
      this.version = MinecraftVersion.parse(version);
   }

   @Override
   public boolean check(SkillCaster caster) {
      return ServerVersion.eq(this.version);
   }
}
