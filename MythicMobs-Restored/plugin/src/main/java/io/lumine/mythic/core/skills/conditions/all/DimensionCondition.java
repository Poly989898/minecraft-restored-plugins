package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.HashSet;
import java.util.Set;

@MythicCondition(
   author = "Ashijin",
   name = "dimension",
   aliases = {"environment"},
   description = "Tests if the target is within a certain dimension"
)
public class DimensionCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "dimension",
      aliases = {"d", "environment", "env"},
      description = "A list of dimensions to check"
   )
   private final Set<String> dimension = new HashSet<>();

   public DimensionCondition(String line, MythicLineConfig mlc) {
      super(line);
      String b = mlc.getString(new String[]{"dimension", "d", "environment", "env"}, "THE_END", this.conditionVar);

      for (String s : b.split(",")) {
         this.dimension.add(s.toUpperCase());
      }
   }

   @Override
   public boolean check(AbstractLocation target) {
      String env = BukkitAdapter.adapt(target).getWorld().getEnvironment().toString().toUpperCase();
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DimensionCondition checking {0} contains {1}", this.dimension.toString(), env);
      return this.dimension.contains(env);
   }
}
