package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.logging.MythicDebug;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@MythicCondition(
   author = "Ashijin",
   name = "mythicMobType",
   aliases = {"mmType"},
   description = "Checks the MythicMob type of the target mob"
)
public class MythicMobTypeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "type",
      aliases = {"types", "t"},
      description = "A list of MythicMob types"
   )
   private Set<String> types = new HashSet<>();

   public MythicMobTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      boolean exact = mlc.getBoolean(new String[]{"exactmatch", "em"}, true);
      String types = mlc.getString(new String[]{"types", "type", "t"}, this.conditionVar);
      String[] split = types.split(",");
      getPlugin().getClock().queueSecondPass(() -> {
         if (exact) {
            for (String lookup : split) {
               for (String type : getPlugin().getMobManager().getMobNames()) {
                  if (type.equals(lookup)) {
                     this.types.add(lookup);
                  }
               }
            }
         } else {
            for (String lookup : split) {
               for (String typex : getPlugin().getMobManager().getMobNames()) {
                  if (typex.contains(lookup)) {
                     this.types.add(lookup);
                  }
               }
            }
         }
      });
   }

   @Override
   public boolean check(AbstractEntity entity) {
      MythicDebug.debug(this, debugger -> {
         Optional<String> maybeTypex = getPlugin().getMobManager().getMythicType(entity);
         if (maybeTypex.isPresent()) {
            debugger.detail("MythicMobType", maybeTypex.get());
         } else {
            debugger.detail("MythicMobType", "null");
         }

         debugger.log("Filtering against:");

         for (String type : this.types) {
            debugger.log("- {0}", type);
         }

         debugger.log("Returning {0}", maybeTypex.filter(string -> this.types.contains(string)).isPresent());
      });
      Optional<String> maybeType = getPlugin().getMobManager().getMythicType(entity);
      return maybeType.filter(string -> this.types.contains(string)).isPresent();
   }
}
