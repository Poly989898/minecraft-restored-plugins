package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Arrays;

@MythicCondition(
   author = "Joshinn",
   name = "packversiongreater",
   aliases = {"mythicpackversiongreater", "packversionisgreater"},
   description = "Whether or not a packs version is updated enough."
)
public class MythicPackVersionGreaterThanCondition extends SkillCondition implements ICasterCondition {
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

   public MythicPackVersionGreaterThanCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.pack = mlc.getString(new String[]{"pack", "p", "id", "packid"}, "");
      this.packVersion = mlc.getString(new String[]{"packVersion", "packV", "version", "v"}, "");
   }

   @Override
   public boolean check(SkillCaster caster) {
      return getPlugin().getPackManager().getPack(this.pack).isPresent()
         && this.isGreaterThanOrEqualTo(getPlugin().getPackManager().getPack(this.pack).get().getVersion(), this.packVersion);
   }

   private boolean isGreaterThanOrEqualTo(String baseVersion, String checkVersion) {
      String[] baseParts = baseVersion.split("\\.");
      String[] checkParts = checkVersion.split("\\.");

      int numParts;
      for (numParts = Math.max(baseParts.length, checkParts.length); baseParts.length < numParts; baseParts[baseParts.length - 1] = "0") {
         baseParts = Arrays.copyOf(baseParts, numParts);
      }

      while (checkParts.length < numParts) {
         checkParts = Arrays.copyOf(checkParts, numParts);
         checkParts[checkParts.length - 1] = "0";
      }

      for (int i = 0; i < numParts; i++) {
         int basePart = Integer.parseInt(baseParts[i]);
         int checkPart = Integer.parseInt(checkParts[i]);
         if (checkPart < basePart) {
            return false;
         }
      }

      return true;
   }
}
