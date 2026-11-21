package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Phil",
   name = "bowTension",
   aliases = {"bowshoottension"},
   description = "Tests the bow tension when shooting from a bow"
)
public class BowTensionCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "force",
      aliases = {"f", "value", "v", "val"},
      description = "The amount of force to check",
      defValue = ">0"
   )
   private final PlaceholderString force;

   public BowTensionCondition(String line, MythicLineConfig config) {
      super(line);
      this.force = config.getPlaceholderString(new String[]{"force", "f", "value", "v", "val"}, ">0");
   }

   @Override
   public boolean check(SkillMetadata data) {
      if (!data.getVariables().has("bow-tension")) {
         return false;
      } else {
         double actualForce = data.getVariables().getFloat("bow-tension");
         return new RangedDouble(this.force.get(data, data.getCaster().getEntity())).equals(actualForce);
      }
   }
}
