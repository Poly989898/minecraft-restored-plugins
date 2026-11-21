package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "region",
   aliases = {"inregion"},
   description = "If the target is within the given WorldGuard region"
)
public class RegionCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "region",
      aliases = {"r"},
      description = "The region to check"
   )
   private String regionName;

   public RegionCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.regionName = mlc.getString(new String[]{"region", "r", "name", "n"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractLocation target) {
      return !MythicBukkit.inst().getCompatibility().getWorldGuard().isPresent()
         ? true
         : MythicBukkit.inst().getCompatibility().getWorldGuard().get().isLocationInRegions(target, this.regionName);
   }
}
