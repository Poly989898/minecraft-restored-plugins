package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "siblings",
   aliases = {"sibling", "brothers", "sisters"},
   description = "Targets any child entities summoned by the caster's parent"
)
public class SiblingsTargeter extends IEntitySelector {
   public SiblingsTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Collection<AbstractEntity> targets = Lists.newArrayList();
      if (data.getCaster() instanceof ActiveMob am && am.getParent().isPresent()) {
         AbstractEntity parent = am.getParent().get();
         if (parent.isPlayer()) {
            PlayerData parentMob = this.getPlugin().getPlayerManager().getProfile(parent.asPlayer());
         } else {
            ActiveMob parentMob = this.getPlugin().getMobManager().getMythicMobInstance(parent);
            if (parentMob != null && !parentMob.getEntity().isDead()) {
               targets.addAll(parentMob.getChildren());
            }
         }
      }

      return targets;
   }
}
