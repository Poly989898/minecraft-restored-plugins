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
   author = "Ashijin",
   name = "serverVersionAfterOrEqual",
   aliases = {"serverAfterEq", "serverAfter", "versionAfterEq", "versionAfter"},
   description = "Whether or not the server is after or equal to a specific version"
)
public class ServerVersionAfterCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "version",
      aliases = {"v", "sv"},
      description = "The version to check for",
      defValue = "1.19.3"
   )
   private MinecraftVersion version;
   private boolean inclusive = false;

   public ServerVersionAfterCondition(String line, MythicLineConfig mlc) {
      super(line);
      String version = mlc.getString(new String[]{"version", "v", "sv"}, "1.19.3");
      this.version = MinecraftVersion.parse(version);
      this.inclusive = mlc.getBoolean(new String[]{"inclusive", "i"}, true);
   }

   @Override
   public boolean check(SkillCaster caster) {
      return this.inclusive ? ServerVersion.isAfterOrEq(this.version) : ServerVersion.isAfter(this.version);
   }
}
