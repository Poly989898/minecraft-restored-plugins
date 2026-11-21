package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@MythicCondition(
   author = "Ashijin",
   name = "world",
   aliases = {},
   description = "Checks the name of the target world."
)
public class WorldCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "world",
      aliases = {"w"},
      description = "The worlds to check",
      defValue = "tutorial_world"
   )
   private String world;
   private Set<String> data = new HashSet<>();

   public WorldCondition(String line, MythicLineConfig mlc) {
      super(line);

      for (String s : this.conditionVar.split(",")) {
         this.data.add(s);
      }

      this.world = mlc.getString(new String[]{"world", "w"}, "tutorial_world", this.conditionVar);
      this.data.addAll(Arrays.asList(this.world.split(",")));
   }

   @Override
   public boolean check(AbstractLocation l) {
      return this.data.contains(l.getWorld().getName());
   }
}
