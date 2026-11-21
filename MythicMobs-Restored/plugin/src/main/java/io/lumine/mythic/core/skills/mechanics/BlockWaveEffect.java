package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@MythicMechanic(
   author = "Ashijin",
   name = "blockWave",
   aliases = {"effect:blockWave", "e:blockWave"},
   description = "Creates a wave of blocks at the target location"
)
public class BlockWaveEffect extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "velocity",
      aliases = {"v"},
      description = "The speed of the effect",
      defValue = "0.2"
   )
   protected PlaceholderDouble velocity;
   @MythicField(
      name = "horizontalVelocity",
      aliases = {"velocityh", "vh"},
      description = "The speed of the effect in the horizontal direction",
      defValue = "0"
   )
   protected PlaceholderDouble velocityH;
   @MythicField(
      name = "velocityX",
      aliases = {"vx"},
      description = "The speed of the effect in the x direction",
      defValue = "0"
   )
   protected PlaceholderDouble velocityX;
   @MythicField(
      name = "velocityY",
      aliases = {"vy"},
      description = "The speed of the effect in the y direction",
      defValue = "0"
   )
   protected PlaceholderDouble velocityY;
   @MythicField(
      name = "velocityZ",
      aliases = {"vz"},
      description = "The speed of the effect in the z direction",
      defValue = "0"
   )
   protected PlaceholderDouble velocityZ;
   @MythicField(
      name = "specificVelocities",
      aliases = {"vs"},
      description = "Whether to set the components of the velocity separately",
      defValue = "false"
   )
   protected boolean specificVelocities;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius of the block wave effect",
      defValue = "2"
   )
   protected PlaceholderInt radius;
   @MythicField(
      name = "radiusY",
      aliases = {"ry"},
      description = "The y-radius of the block wave effect",
      defValue = "radius"
   )
   protected PlaceholderInt radiusY;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "The duration of the block wave effect",
      defValue = "15"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "noise",
      aliases = {"n"},
      description = "The noise/randomness of the block wave effect",
      defValue = "0"
   )
   protected PlaceholderDouble randomness;
   @MythicField(
      name = "shape",
      aliases = {"s"},
      description = "The shape of the block wave effect - SPHERE / CUBE",
      defValue = "SPHERE"
   )
   protected boolean sphere;
   @MythicField(
      name = "M",
      aliases = {"vx"},
      description = "The speed of the effect in the x direction",
      defValue = "0"
   )
   protected Material material;
   @MythicField(
      name = "ignoreAir",
      aliases = {"ia"},
      description = "Whether to ignore air blocks during the wave effect.",
      defValue = "true"
   )
   protected boolean ignoreAir;
   @MythicField(
      name = "hideSourceBlock",
      aliases = {"hidesourceblock", "hidesource", "hsb", "hs"},
      description = "Whether to hide the source block during the wave effect.",
      defValue = "true"
   )
   protected boolean hideSourceBlock;
   private int radiusSq;

   public BlockWaveEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.velocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v"}, 0.2);
      this.velocityH = mlc.getPlaceholderDouble(new String[]{"horizontalvelocity", "velocityh", "vh"}, 0.0);
      this.velocityX = mlc.getPlaceholderDouble(new String[]{"velocityx", "vx"}, 0.0);
      this.velocityY = mlc.getPlaceholderDouble(new String[]{"velocityy", "vy"}, 0.0);
      this.velocityZ = mlc.getPlaceholderDouble(new String[]{"velocityz", "vz"}, 0.0);
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 2);
      this.radiusY = mlc.getPlaceholderInteger(new String[]{"radiusy", "ry"}, this.radius);
      this.randomness = mlc.getPlaceholderDouble(new String[]{"noise", "n"}, 0.0);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "15");
      this.ignoreAir = mlc.getBoolean(new String[]{"ignoreair", "ia"}, true);
      this.hideSourceBlock = mlc.getBoolean(new String[]{"hidesourceblock", "hidesource", "hsb", "hs"}, true);
      this.specificVelocities = mlc.getBoolean(new String[]{"specificvelocities", "sv"}, false);
      String shape = mlc.getString(new String[]{"shape", "s"}, "SPHERE").toUpperCase();
      if (shape.equals("SPHERE")) {
         this.sphere = true;
      } else {
         this.sphere = false;
      }

      String strMaterial = mlc.getString(new String[]{"material", "m"}, null);
      if (strMaterial == null) {
         this.material = null;
      } else {
         this.material = Material.valueOf(strMaterial.toUpperCase());
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      double bv = this.velocity.get(data);
      double bvh = this.velocityH.get(data);
      int radius = this.radius.get(data);
      if (radius < 0) {
         radius = 0;
      }

      this.radiusSq = radius * radius;
      if (radius == 0) {
         AbstractVector velocity;
         if (this.specificVelocities) {
            velocity = new AbstractVector(this.velocityX.get(data), this.velocityY.get(data), this.velocityZ.get(data));
         } else {
            double v = -(bv / 2.0) + bv * Numbers.randomDouble();
            double vh = -(bvh / 2.0) + bvh * Numbers.randomDouble();
            velocity = new AbstractVector(vh, v, vh);
         }

         this.throwBlock(target, velocity, this.duration.get(data).getTicks());
      } else if (this.specificVelocities) {
         AbstractVector velocity = new AbstractVector(this.velocityX.get(data), this.velocityY.get(data), this.velocityZ.get(data));

         for (AbstractLocation l : this.getBlocksInRadius(data, target, true)) {
            this.throwBlock(l, velocity, this.duration.get(data).getTicks());
         }
      } else {
         for (AbstractLocation l : this.getBlocksInRadius(data, target, true)) {
            double v = -(bv / 2.0) + bv * Numbers.randomDouble();
            double vh = -(bvh / 2.0) + bvh * Numbers.randomDouble();
            this.throwBlock(l, new AbstractVector(vh, v, vh), this.duration.get(data).getTicks());
         }
      }

      return SkillResult.SUCCESS;
   }

   private void throwBlock(AbstractLocation location, AbstractVector velocity, int duration) {
      if (this.material != null) {
         this.getPlugin().getVolatileCodeHandler().getBlockHandler().throwBlock(location, this.material, velocity, duration, this.hideSourceBlock);
      } else {
         Block block = BukkitAdapter.adapt(location).getBlock();
         if (block.getType() == Material.LIGHT) {
            return;
         }

         this.getPlugin().getVolatileCodeHandler().getBlockHandler().throwBlock(location, block.getState(), velocity, duration, this.hideSourceBlock);
      }
   }

   private List<AbstractLocation> getBlocksInRadius(SkillMetadata data, AbstractLocation location, boolean randomize) {
      int radius = this.radius.get(data);
      int radiusY = this.radiusY.get(data);
      double randomness = this.randomness.get(data);
      List<AbstractLocation> blocks = new ArrayList<>();
      Location loc = BukkitAdapter.adapt(location);

      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            for (int y = radiusY; y >= -radiusY; y--) {
               Location newloc = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
               if ((!this.sphere || !(loc.distanceSquared(newloc) > this.radiusSq))
                  && (!randomize || !(randomness > 0.0) || !(randomness < Numbers.randomDouble()))
                  && (!this.ignoreAir || newloc.getBlock().getType() != Material.AIR)) {
                  blocks.add(BukkitAdapter.adapt(newloc));
                  break;
               }
            }
         }
      }

      return blocks;
   }
}
