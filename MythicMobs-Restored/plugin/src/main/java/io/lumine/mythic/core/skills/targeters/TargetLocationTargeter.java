package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.function.Predicate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@MythicTargeter(
   author = "Ashijin",
   name = "targetLocation",
   aliases = {"targetLoc", "TL"},
   description = "Targets the location the caster is targeting"
)
public class TargetLocationTargeter extends ILocationSelector {
   private PlaceholderDouble maxDistance;
   private boolean ignoreTransparent;

   public TargetLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.maxDistance = mlc.getPlaceholderDouble(new String[]{"maxdistance", "max", "distance", "d"}, 64.0);
      this.ignoreTransparent = mlc.getBoolean(new String[]{"ignoreTransparent", "it"}, true);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster caster = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractLocation l = null;
      if (caster instanceof ActiveMob am) {
         if (am.hasThreatTable()) {
            AbstractEntity topThreatHolder = am.getThreatTable().getTopThreatHolder();
            if (topThreatHolder != null) {
               l = topThreatHolder.getLocation();
            }
         } else if (am.getEntity().isCreature()) {
            AbstractEntity target = am.getEntity().getTarget();
            if (target != null) {
               l = target.getLocation();
            }
         } else {
            l = am.getLastAggroCause().getLocation();
         }
      } else if (caster.getEntity().isPlayer()) {
         l = this.getTargetedLocation(data, caster.getEntity().asPlayer());
      }

      if (l != null) {
         targets.add(this.mutate(data, l));
      }

      return targets;
   }

   private AbstractLocation getTargetedLocation(SkillMetadata data, AbstractPlayer aPlayer) {
      Player player = (Player)aPlayer.getBukkitEntity();
      RayTraceResult result = this.rayTrace(
         player.getEyeLocation(),
         player.getEyeLocation().getDirection(),
         this.maxDistance.get(data),
         0.25,
         material -> this.ignoreTransparent ? this.getPlugin().getConfiguration().getTransparentBlocks().contains(material) : material == Material.BARRIER
      );
      return result.getHitPosition() != null ? BukkitAdapter.adapt(result.getHitPosition().toLocation(player.getWorld())) : null;
   }

   private RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, double raySize, Predicate<Material> blockFilter) {
      if (!(direction.lengthSquared() < 1.0E-5) && !(maxDistance <= 1.0E-5)) {
         RayTraceResult blockRayTrace = null;
         BlockIterator bIterator = new BlockIterator(start.getWorld(), start.toVector(), direction, 0.0, (int)Math.ceil(maxDistance));
         Block block = null;

         while (bIterator.hasNext()) {
            block = bIterator.next();
            if (!block.isEmpty() && !blockFilter.test(block.getType())) {
               RayTraceResult res = block.rayTrace(start, direction, maxDistance, FluidCollisionMode.ALWAYS);
               if (res != null) {
                  blockRayTrace = res;
                  break;
               }
            }
         }

         if (blockRayTrace != null) {
            return blockRayTrace;
         } else {
            Vector endVector = start.toVector().add(direction.normalize().multiply(maxDistance));
            return new RayTraceResult(endVector);
         }
      } else {
         return null;
      }
   }
}
