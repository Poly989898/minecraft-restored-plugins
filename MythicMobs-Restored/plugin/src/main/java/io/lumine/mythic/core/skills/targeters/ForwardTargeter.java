package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "forward",
   aliases = {},
   description = "Targets a point in front of the caster"
)
public class ForwardTargeter extends ILocationSelector {
   protected PlaceholderDouble forward;
   protected PlaceholderDouble rotate;
   protected boolean useEyeLocation;
   protected boolean lockPitch;
   protected boolean rotated = false;

   public ForwardTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.forward = mlc.getPlaceholderDouble(new String[]{"forward", "f", "amount", "a"}, 5.0);
      this.rotate = mlc.getPlaceholderDouble(new String[]{"rotate", "rot"}, (PlaceholderDouble)null);
      this.useEyeLocation = mlc.getBoolean(new String[]{"useeyelocation", "uel"}, false);
      this.lockPitch = mlc.getBoolean(new String[]{"lockpitch"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      double forward = this.forward.get(data);
      AbstractLocation location;
      if (this.useEyeLocation) {
         location = am.getEntity().getEyeLocation();
      } else {
         location = am.getEntity().getLocation();
      }

      if (this.lockPitch) {
         location.setPitch(0.0F);
      }

      double rotate;
      if (this.rotate != null && (rotate = this.rotate.get(data)) != 0.0) {
         location.add(location.getDirection().rotate(rotate).normalize().multiply(forward));
      } else {
         location.add(location.getDirection().normalize().multiply(forward));
      }

      targets.add(this.mutate(data, location));
      return targets;
   }
}
