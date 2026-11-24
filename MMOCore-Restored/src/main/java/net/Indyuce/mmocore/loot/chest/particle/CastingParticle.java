package net.Indyuce.mmocore.loot.chest.particle;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class CastingParticle {
   private final Consumer<Location> display;

   public CastingParticle(@NotNull ConfigurationSection config) {
      Validate.notNull(config, "Casting particle config cannot be null");
      Particle particle = Particle.valueOf(UtilityMethods.enumName(Objects.requireNonNull(config.getString("particle"), "Could not find particle name")));
      if (particle.getDataType() == DustOptions.class) {
         float size = Math.max((float)config.getDouble("size", 1.0), 0.0F);
         Color color = Color.fromRGB(config.getInt("color.red"), config.getInt("color.green"), config.getInt("color.blue"));
         this.display = loc -> loc.getWorld().spawnParticle(particle, loc, 1, new DustOptions(color, size));
      } else if (particle.getDataType() == BlockData.class) {
         Material material = Material.valueOf(UtilityMethods.enumName(Objects.requireNonNull(config.getString("material"), "Particle requires a block")));
         this.display = loc -> loc.getWorld().spawnParticle(particle, loc, 1, material.createBlockData());
      } else {
         this.display = loc -> loc.getWorld().spawnParticle(particle, loc, 0);
      }
   }

   public CastingParticle(Particle particle) {
      this.display = loc -> loc.getWorld().spawnParticle(particle, loc, 0);
   }

   public void display(Location loc) {
      this.display.accept(loc);
   }
}
