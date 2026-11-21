package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Material;

@MythicTargeter(
   author = "Ashijin",
   name = "blocksnearorigin",
   aliases = {"BNO"},
   description = "Targets all blocks in the radius around the origin"
)
public class BlocksNearOriginTargeter extends ILocationSelector {
   protected PlaceholderDouble radius;
   protected PlaceholderDouble radiusY;
   protected float noise;
   protected double randomness;
   protected boolean sphere = false;
   private BlocksNearOriginTargeter.MaskType maskType;

   public BlocksNearOriginTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      String radius = mlc.getString(new String[]{"radius", "r"}, "2");
      this.radius = PlaceholderDouble.of(radius);
      this.radiusY = mlc.getPlaceholderDouble(new String[]{"radiusy", "ry", "yradius", "yr"}, radius);
      this.randomness = mlc.getDouble(new String[]{"noise", "n"}, 0.0);
      String shape = mlc.getString(new String[]{"shape", "s"}, "SPHERE").toUpperCase();
      if (shape.equals("SPHERE")) {
         this.sphere = true;
      } else {
         this.sphere = false;
      }

      boolean noAir = mlc.getBoolean(new String[]{"noair", "na"}, true);
      boolean onlyAir = mlc.getBoolean(new String[]{"onlyair", "oa"}, false);
      if (noAir && !onlyAir) {
         this.maskType = BlocksNearOriginTargeter.MaskType.IGNORE_AIR;
      } else if (onlyAir) {
         this.maskType = BlocksNearOriginTargeter.MaskType.ONLY_AIR;
      } else {
         this.maskType = BlocksNearOriginTargeter.MaskType.ALL;
      }
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractLocation> targets = new HashSet<>();
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@BlocksNearOrigin targeter");

      for (AbstractLocation location : this.getBlocksInRadius(data, data.getOrigin())) {
         targets.add(location);
      }

      return targets;
   }

   private List<AbstractLocation> getBlocksInRadius(SkillMetadata data, AbstractLocation origin) {
      List<AbstractLocation> blocks = new ArrayList<>();
      double radius = this.radius.get(data);
      double radiusSq = (int)Math.pow(radius, 2.0);
      double radiusY = this.radiusY.get(data);
      double radiusSqY = (int)Math.pow(radiusY, 2.0);

      for (double x = -radius; x <= radius; x++) {
         for (double y = -radiusY; y <= radiusY; y++) {
            for (double z = -radius; z <= radius; z++) {
               if (!(this.randomness > 0.0) || !(this.randomness < Numbers.randomDouble())) {
                  if (this.sphere) {
                     double dx = x * x / radiusSq;
                     double dy = y * y / radiusSqY;
                     double dz = z * z / radiusSq;
                     double distanceSq = dx + dy + dz;
                     if (distanceSq > 1.0) {
                        continue;
                     }
                  }

                  AbstractLocation newloc = new AbstractLocation(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                  newloc = this.mutate(data, newloc);
                  if (this.maskType == BlocksNearOriginTargeter.MaskType.ALL) {
                     blocks.add(newloc);
                  } else if (this.maskType == BlocksNearOriginTargeter.MaskType.IGNORE_AIR && BukkitAdapter.adapt(newloc).getBlock().getType() != Material.AIR) {
                     blocks.add(newloc);
                  } else if (this.maskType == BlocksNearOriginTargeter.MaskType.ONLY_AIR && BukkitAdapter.adapt(newloc).getBlock().getType() == Material.AIR) {
                     blocks.add(newloc);
                  }
               }
            }
         }
      }

      return blocks;
   }

   private static enum MaskType {
      ALL,
      IGNORE_AIR,
      ONLY_AIR;
   }
}
