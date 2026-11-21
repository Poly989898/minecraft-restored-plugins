package io.lumine.mythic.bukkit.utils.particles;

import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {
   private static List<Color> fireworkColors = new ArrayList<>();
   private static List<Type> fireworkTypes = new ArrayList<>();

   public static Firework spawn(Location location) {
      return spawn(location, Numbers.randomInt(5));
   }

   public static Firework spawn(Location location, int power) {
      Firework firework = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
      FireworkMeta fireworkMeta = firework.getFireworkMeta();
      Type type = getRandomType();
      Color c1 = getRandomColor();
      Color c2 = getRandomColor();
      FireworkEffect effect = FireworkEffect.builder()
         .flicker(Numbers.randomBoolean())
         .withColor(c1)
         .withFade(c2)
         .with(type)
         .trail(Numbers.randomBoolean())
         .build();
      fireworkMeta.addEffect(effect);
      fireworkMeta.setPower(power);
      firework.setFireworkMeta(fireworkMeta);
      return firework;
   }

   private static Type getRandomType() {
      return fireworkTypes.get(Numbers.randomInt(fireworkTypes.size()));
   }

   private static Color getRandomColor() {
      return fireworkColors.get(Numbers.randomInt(fireworkColors.size()));
   }

   static {
      fireworkColors.add(Color.AQUA);
      fireworkColors.add(Color.YELLOW);
      fireworkColors.add(Color.BLACK);
      fireworkColors.add(Color.BLUE);
      fireworkColors.add(Color.FUCHSIA);
      fireworkColors.add(Color.GRAY);
      fireworkColors.add(Color.GREEN);
      fireworkColors.add(Color.LIME);
      fireworkColors.add(Color.MAROON);
      fireworkColors.add(Color.NAVY);
      fireworkColors.add(Color.OLIVE);
      fireworkColors.add(Color.ORANGE);
      fireworkColors.add(Color.PURPLE);
      fireworkColors.add(Color.RED);
      fireworkColors.add(Color.SILVER);
      fireworkColors.add(Color.TEAL);
      fireworkColors.add(Color.WHITE);
      fireworkTypes.add(Type.BALL);
      fireworkTypes.add(Type.BALL_LARGE);
      fireworkTypes.add(Type.BURST);
      fireworkTypes.add(Type.CREEPER);
      fireworkTypes.add(Type.STAR);
   }
}
