package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "selfEyeLocation",
   aliases = {"eyeDirection", "casterEyeLocation", "bossEyeLocation", "mobEyeLocation"},
   description = "Targets the location of the caster"
)
public class SelfEyeLocationTargeter extends ILocationSelector {
   public SelfEyeLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      targets.add(this.mutate(data, am.getEntity().getEyeLocation()));
      return targets;
   }
}
