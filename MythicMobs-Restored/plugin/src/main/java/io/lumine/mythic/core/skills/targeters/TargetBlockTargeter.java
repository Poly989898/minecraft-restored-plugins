package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@MythicTargeter(
   author = "Ashijin",
   name = "targetBlock",
   aliases = {},
   description = "Targets the block the caster is targeting"
)
public class TargetBlockTargeter extends ILocationSelector {
   private int maxDistance;
   private boolean ignoreTransparent;

   public TargetBlockTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.maxDistance = mlc.getInteger(new String[]{"maxdistance", "max", "distance", "d"}, 64);
      this.ignoreTransparent = mlc.getBoolean(new String[]{"ignoreTransparent", "it"}, true);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster caster = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractLocation l = null;
      if (caster instanceof ActiveMob am) {
         if (am.hasThreatTable()) {
            if (am.getThreatTable().getTopThreatHolder() != null) {
               l = am.getThreatTable().getTopThreatHolder().getLocation();
            }
         } else if (am.getEntity().isCreature()) {
            if (am.getEntity().getTarget() != null) {
               l = am.getEntity().getTarget().getLocation();
            }
         } else {
            l = am.getLastAggroCause().getLocation();
         }
      } else if (caster.getEntity().isPlayer()) {
         if (this.ignoreTransparent) {
            l = BukkitAdapter.adapt(
               ((Player)BukkitAdapter.adapt(data.getCaster().getEntity()))
                  .getTargetBlock(this.getPlugin().getConfiguration().getTransparentBlocks(), this.maxDistance)
                  .getLocation()
            );
         } else {
            HashSet<Material> transparent = new HashSet<>();
            transparent.add(Material.AIR);
            l = BukkitAdapter.adapt(((Player)BukkitAdapter.adapt(data.getCaster().getEntity())).getTargetBlock(transparent, this.maxDistance).getLocation());
         }
      }

      if (l != null) {
         targets.add(this.mutate(data, l));
      }

      return targets;
   }
}
