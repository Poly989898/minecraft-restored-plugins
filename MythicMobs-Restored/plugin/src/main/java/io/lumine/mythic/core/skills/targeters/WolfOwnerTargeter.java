package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

@MythicTargeter(
   author = "Ashijin",
   name = "WolfOwner",
   aliases = {},
   description = "Targets the vanilla owner of the caster, if the caster is a wolf"
)
public class WolfOwnerTargeter extends IEntitySelector {
   public WolfOwnerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractEntity> targets = new HashSet<>();
      if (am.getEntity().getBukkitEntity() instanceof Wolf) {
         Wolf w = (Wolf)am.getEntity().getBukkitEntity();
         if (w.getOwner() != null) {
            targets.add(BukkitAdapter.adapt((Entity)w.getOwner()));
         }
      }

      return targets;
   }
}
