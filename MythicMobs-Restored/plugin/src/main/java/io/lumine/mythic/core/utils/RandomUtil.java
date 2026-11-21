package io.lumine.mythic.core.utils;

import io.lumine.mythic.api.adapters.AbstractVector;
import java.util.Random;
import org.bukkit.Material;

public final class RandomUtil {
   public static final Random random = new Random(System.nanoTime());

   public static AbstractVector getRandomVector() {
      double x = random.nextDouble() * 2.0 - 1.0;
      double y = random.nextDouble() * 2.0 - 1.0;
      double z = random.nextDouble() * 2.0 - 1.0;
      return new AbstractVector(x, y, z).normalize();
   }

   public static AbstractVector getRandomCircleVector() {
      double rnd = random.nextDouble() * 2.0 * Math.PI;
      double x = Math.cos(rnd);
      double z = Math.sin(rnd);
      return new AbstractVector(x, 0.0, z);
   }

   public static Material getRandomMaterial(Material[] materials) {
      return materials[random.nextInt(materials.length)];
   }

   public static double getRandomAngle() {
      return random.nextDouble() * 2.0 * Math.PI;
   }
}
