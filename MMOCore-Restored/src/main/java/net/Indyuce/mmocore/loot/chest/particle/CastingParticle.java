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
         Object data = defaultData(particle);
         this.display = loc -> loc.getWorld().spawnParticle(particle, loc, 0, data);
      }
   }

   public CastingParticle(Particle particle) {
      Object data = defaultData(particle);
      this.display = loc -> loc.getWorld().spawnParticle(particle, loc, 0, data);
   }

   // Recent versions require data for some particles (e.g. INSTANT_EFFECT takes a Particle.Spell)
   static Object defaultData(Particle particle) {
      Class<?> type = particle.getDataType();
      if (type == Particle.Spell.class) {
         return new Particle.Spell(Color.WHITE, 1.0F);
      } else if (type == Color.class) {
         return Color.WHITE;
      } else if (type == Float.class) {
         return 1.0F;
      } else if (type == Integer.class) {
         return 0;
      }
      return null;
   }

   public void display(Location loc) {
      this.display.accept(loc);
   }
}
