package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "playerByName",
   aliases = {"specificplayer"},
   description = "Targets a specific player by name"
)
public class PlayerByNameTargeter extends IEntitySelector {
   private final PlaceholderString name;

   public PlayerByNameTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.name = mlc.getPlaceholderString(new String[]{"name", "n"}, "CarsonJF");
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = Sets.newHashSet();
      AbstractPlayer player = this.getPlugin().getBootstrap().getPlayer(this.name.get(data));
      if (player != null) {
         targets.add(player);
      }

      return targets;
   }
}
