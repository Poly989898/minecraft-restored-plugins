package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
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
   name = "targetPredictedLocation",
   aliases = {"targetPredictedLoc", "TPL", "PredictedTargetLocation"},
   description = "Targets the predicted location the caster is targeting"
)
public class TargetPredictedLocationTargeter extends ILocationSelector {
   private int maxDistance;
   private int ticksPredicted;
   private boolean ignoreTransparent;

   public TargetPredictedLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.getPlugin().getPlayerManager().trackPlayerMovement();
      this.maxDistance = mlc.getInteger(new String[]{"maxdistance", "max", "distance", "d"}, 64);
      this.ticksPredicted = mlc.getInteger(new String[]{"ticksPredicted", "ticks", "t"}, 20);
      this.ignoreTransparent = mlc.getBoolean(new String[]{"ignoreTransparent", "it"}, true);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster caster = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractEntity targetedEntity = null;
      if (caster instanceof ActiveMob am) {
         if (am.hasThreatTable()) {
            if (am.getThreatTable().getTopThreatHolder() != null) {
               targetedEntity = am.getThreatTable().getTopThreatHolder();
            }
         } else if (am.getEntity().isCreature()) {
            if (am.getEntity().getTarget() != null) {
               targetedEntity = am.getEntity().getTarget();
            }
         } else {
            targetedEntity = am.getLastAggroCause();
         }
      } else if (caster.getEntity().isPlayer()) {
         targetedEntity = this.getTargetedEntity(caster.getEntity().asPlayer());
      }

      if (targetedEntity != null) {
         AbstractLocation location = this.predictLocation(targetedEntity);
         targets.add(this.mutate(data, location));
      }

      return targets;
   }

   private AbstractEntity getTargetedEntity(AbstractPlayer aPlayer) {
      Player player = (Player)aPlayer.getBukkitEntity();
      RayTraceResult result = this.rayTrace(
         player.getEyeLocation(),
         player.getEyeLocation().getDirection(),
         this.maxDistance,
         0.25,
         material -> this.ignoreTransparent ? this.getPlugin().getConfiguration().getTransparentBlocks().contains(material) : material == Material.BARRIER
      );
      return result.getHitEntity() != null ? BukkitAdapter.adapt(result.getHitEntity()) : null;
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

   private AbstractLocation predictLocation(AbstractEntity entity) {
      AbstractLocation currentLocation = entity.getLocation();
      AbstractVector velocity;
      if (entity.isPlayer()) {
         PlayerManager.PlayerMovementData playerMovementData = this.getPlugin().getPlayerManager().getPlayerPositions().get(entity.getUniqueId());
         Location playerFrom = playerMovementData.getFrom();
         Location playerTo = playerMovementData.getTo();
         double velocityX = playerTo.getX() - playerFrom.getX();
         double velocityY = playerTo.getY() - playerFrom.getY();
         double velocityZ = playerTo.getZ() - playerFrom.getZ();
         velocity = new AbstractVector(velocityX, velocityY, velocityZ);
      } else {
         velocity = entity.getVelocity();
      }

      double predictedX = currentLocation.getX() + velocity.getX() * this.ticksPredicted;
      double predictedY = currentLocation.getY() + velocity.getY() * this.ticksPredicted;
      double predictedZ = currentLocation.getZ() + velocity.getZ() * this.ticksPredicted;
      AbstractLocation predictedLocation = currentLocation.clone();
      predictedLocation.setX(predictedX);
      predictedLocation.setY(predictedY);
      predictedLocation.setZ(predictedZ);
      if (this.ignoreTransparent) {
         for (int t = 0; t <= this.ticksPredicted; t++) {
            double intermediateX = currentLocation.getX() + velocity.getX() * t;
            double intermediateY = currentLocation.getY() + velocity.getY() * t;
            double intermediateZ = currentLocation.getZ() + velocity.getZ() * t;
            AbstractLocation intermediateLocation = currentLocation.clone();
            intermediateLocation.setX(intermediateX);
            intermediateLocation.setY(intermediateY);
            intermediateLocation.setZ(intermediateZ);
            if (ServerVersion.isFolia()) {
               Location bukkitLoc = BukkitAdapter.adapt(intermediateLocation);
               if (!Bukkit.isPrimaryThread() || !FoliaHelper.isOwnedByCurrentRegion(bukkitLoc)) {
                  Boolean isOccluding = Schedulers.of(bukkitLoc).supply(() -> intermediateLocation.getBlock().getBlockData().isOccluding()).join();
                  if (isOccluding) {
                     return intermediateLocation;
                  }
                  continue;
               }
            }

            if (intermediateLocation.getBlock().getBlockData().isOccluding()) {
               return intermediateLocation;
            }
         }
      }

      return predictedLocation;
   }
}
