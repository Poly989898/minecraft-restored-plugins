package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Bukkit;

@MythicCondition(
   author = "Joshinn",
   name = "servernmsversion",
   aliases = {"servernms", "nmsversion"},
   description = "Whether or not the server is a specific version"
)
public class ServerNMSVersionCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "version",
      aliases = {"v", "sv"},
      description = "The version to check for",
      defValue = "v1_19_R1_2"
   )
   private String version;

   public ServerNMSVersionCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.version = mlc.getString(new String[]{"version", "v", "sv"}, "v1_19_R1_2");
   }

   @Override
   public boolean check(SkillCaster caster) {
      String packageName = Bukkit.getServer().getClass().getPackage().getName();
      String version = packageName.substring(packageName.lastIndexOf(46) + 1);
      return version.equalsIgnoreCase(this.version);
   }
}
