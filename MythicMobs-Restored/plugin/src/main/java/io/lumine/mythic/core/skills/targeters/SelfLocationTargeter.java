package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MythicTargeter(
   author = "Ashijin",
   name = "selfLocation",
   aliases = {"casterLocation", "bossLocation", "mobLocation"},
   description = "Targets the location of the caster"
)
public class SelfLocationTargeter extends ILocationSelector {
   public SelfLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      List<AbstractLocation> targets = new ArrayList<>();
      targets.add(this.mutate(data, am.getEntity().getLocation()));
      return targets;
   }
}
