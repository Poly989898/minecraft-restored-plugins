package io.lumine.mythic.bukkit.adapters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractFirework;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.volatilecode.VolatileEntityType;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.awt.Color;
import java.util.Collection;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class BukkitFirework extends AbstractFirework {
   private String type;
   private int flightDuration;
   private Collection<Color> colors = Lists.newArrayList();
   private Collection<Color> fadeColors = Lists.newArrayList();
   private boolean flicker;
   private boolean trail;
   private FireworkEffect effect;

   public BukkitFirework(String type, int duration, Collection<Color> colors, Collection<Color> fadeColors, boolean flicker, boolean trail) {
      this.type = type;
      this.flightDuration = duration;
      this.colors = colors;
      this.fadeColors = fadeColors;
      this.flicker = flicker;
      this.trail = trail;
      Builder builder = FireworkEffect.builder().with(Type.valueOf(type.toUpperCase())).flicker(flicker).trail(trail);

      for (Color c : colors) {
         builder.withColor(org.bukkit.Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
      }

      for (Color c : fadeColors) {
         builder.withFade(org.bukkit.Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
      }

      this.effect = builder.build();
   }

   @Override
   public void launch(SkillMetadata data, AbstractLocation location) {
      Location loc = BukkitAdapter.adapt(location);
      Firework firework = (Firework)loc.getWorld().spawnEntity(loc, VolatileEntityType.FIREWORK_ROCKET);
      FireworkMeta meta = firework.getFireworkMeta();
      meta.setPower(this.flightDuration);
      meta.addEffect(this.effect);
      firework.setFireworkMeta(meta);
      if (this.flightDuration == 0) {
         firework.detonate();
      }
   }
}
