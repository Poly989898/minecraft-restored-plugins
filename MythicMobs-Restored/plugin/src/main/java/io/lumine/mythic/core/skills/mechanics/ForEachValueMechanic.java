package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Lxlp",
   name = "forEachValue",
   version = "5.10",
   description = "Runs a given metaskill for each element of the specified list"
)
public class ForEachValueMechanic extends MetaSkillMechanic implements IMetaSkill, SkillHolder {
   private final PlaceholderString values;

   public ForEachValueMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.values = mlc.getPlaceholderString(new String[]{"values", "val", "v"}, null);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.values == null) {
         return SkillResult.INVALID_CONFIG;
      } else if (this.metaskill.isEmpty()) {
         return SkillResult.INVALID_CONFIG;
      } else {
         String unparsedValue = this.values.get(data);
         int i = 0;
         SkillMetadata newData = data.deepClone();
         if (unparsedValue.contains("=")) {
            String[] keyValuePairs = this.values.get(data).split(";");

            for (String pair : keyValuePairs) {
               String[] split = pair.split("=");
               newData.getParameters().put("key", split[0]);
               newData.getParameters().put("value", split[1]);
               newData.getParameters().put("index", String.valueOf(i++));
               super.cast(newData);
            }
         } else if (unparsedValue.contains(",")) {
            String[] split = unparsedValue.split(",");

            for (String value : split) {
               newData.getParameters().put("value", value);
               newData.getParameters().put("index", String.valueOf(i++));
               super.cast(newData);
            }
         } else if (!unparsedValue.isEmpty()) {
            newData.getParameters().put("value", unparsedValue);
            newData.getParameters().put("index", "0");
            super.cast(newData);
         }

         return SkillResult.SUCCESS;
      }
   }
}
