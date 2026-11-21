package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "playerLocationByName",
   aliases = {"specificplayerlocation"},
   description = "Targets a specific player's location by name"
)
public class PlayerLocationByNameTargeter extends ILocationSelector {
   private final PlaceholderString name;

   public PlayerLocationByNameTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.name = mlc.getPlaceholderString(new String[]{"name", "n"}, "CarsonJF");
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractPlayer player = this.getPlugin().getBootstrap().getPlayer(this.name.get(data));
      if (player != null) {
         targets.add(player.getLocation());
      }

      return targets;
   }
}
