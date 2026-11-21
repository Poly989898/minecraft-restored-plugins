package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@MythicTargeter(
   author = "Ashijin",
   name = "target",
   aliases = {"T"},
   description = "Targets the caster's target"
)
public class TargetTargeter extends IEntitySelector {
   public TargetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      ArrayList<AbstractEntity> targets = new ArrayList<>(1);
      SkillCaster caster = data.getCaster();
      if (caster instanceof ActiveMob am) {
         if (am.hasThreatTable()) {
            targets.add(am.getThreatTable().getTopThreatHolder());
         } else {
            targets.add(am.getEntity().getTarget());
         }
      } else if (caster.getEntity().isPlayer()) {
         LivingEntity le = MythicUtil.getTargetedEntity((Player)BukkitAdapter.adapt(caster.getEntity()));
         if (le != null) {
            targets.add(BukkitAdapter.adapt(le));
         }
      }

      return targets;
   }
}
