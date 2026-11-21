package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

@MythicTargeter(
   author = "Ashijin",
   name = "owner",
   aliases = {},
   description = "Targets the caster's owner"
)
public class OwnerTargeter extends IEntitySelector {
   public OwnerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      AbstractEntity caster = data.getCaster().getEntity();
      if (caster != null && caster.getBukkitEntity() instanceof Item item && item.getOwner() != null) {
         Player player = Bukkit.getPlayer(item.getOwner());
         if (player != null) {
            targets.add(BukkitAdapter.adapt(player));
         }

         return targets;
      } else if (!(data.getCaster() instanceof ActiveMob)) {
         return targets;
      } else {
         ActiveMob am = (ActiveMob)data.getCaster();
         if (am.getOwnerUUID().isPresent()) {
            UUID uuid = am.getOwnerUUID().get();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
               targets.add(BukkitAdapter.adapt(player));
            }
         }

         return targets;
      }
   }
}
