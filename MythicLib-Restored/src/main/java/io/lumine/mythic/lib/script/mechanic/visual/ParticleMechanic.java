package io.lumine.mythic.lib.script.mechanic.visual;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.LocationMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;

@MechanicMetadata
public class ParticleMechanic extends LocationMechanic {
   private final Particle particle;
   private final int amount;
   private final double speed;
   private final double xoffset;
   private final double yoffset;
   private final double zoffset;
   private final DustOptions dustOptions;
   private final BlockData blockData;
   private static final DustOptions DEFAULT_DUST_OPTIONS = new DustOptions(Color.RED, 1.0F);
   private static final BlockData DEFAULT_BLOCK_DATA = Material.DIRT.createBlockData();

   public ParticleMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("particle");
      this.particle = Particle.valueOf(UtilityMethods.enumName(config.getString("particle")));
      this.amount = config.getInt("amount", 1);
      this.speed = config.getDouble("speed", 0.0);
      this.xoffset = config.getDouble("x", 0.0);
      this.yoffset = config.getDouble("y", 0.0);
      this.zoffset = config.getDouble("z", 0.0);
      this.dustOptions = config.contains("color")
         ? new DustOptions(this.loadColor(config.getObject("color")), (float)config.getDouble("size", 1.0))
         : DEFAULT_DUST_OPTIONS;
      this.blockData = config.contains("block") ? Material.valueOf(UtilityMethods.enumName(config.getString("block"))).createBlockData() : DEFAULT_BLOCK_DATA;
   }

   @Override
   public void cast(SkillMetadata meta, Location loc) {
      if (this.particle.getDataType() == DustOptions.class) {
         meta.getCaster()
            .getPlayer()
            .getWorld()
            .spawnParticle(this.particle, loc, this.amount, this.xoffset, this.yoffset, this.zoffset, this.speed, this.dustOptions);
      } else if (this.particle.getDataType() == BlockData.class) {
         meta.getCaster()
            .getPlayer()
            .getWorld()
            .spawnParticle(this.particle, loc, this.amount, this.xoffset, this.yoffset, this.zoffset, this.speed, this.blockData);
      } else {
         meta.getCaster().getPlayer().getWorld().spawnParticle(this.particle, loc, this.amount, this.xoffset, this.yoffset, this.zoffset, this.speed);
      }
   }

   private Color loadColor(ConfigObject obj) {
      Validate.isTrue(obj.contains("red") && obj.contains("green") && obj.contains("blue"), "Color must have red, green and blue");
      return Color.fromRGB(obj.getInt("red"), obj.getInt("green"), obj.getInt("blue"));
   }
}
