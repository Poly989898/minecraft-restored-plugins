package me.TechsCode.UltraPermissions.base.item;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public final class Particles {
   public static final double PII = 6.283185307179586;
   public static final double R270 = Math.toRadians(270.0);
   public static final double R90 = 1.5707963267948966;

   private Particles() {
   }

   public static Optional<XParticle> randomParticle(String... texts) {
      int index = randInt(0, texts.length - 1);
      return XParticle.of(texts[index]);
   }

   public static double random(double min, double max) {
      return ThreadLocalRandom.current().nextDouble(min, max);
   }

   public static int randInt(int min, int max) {
      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }

   public static Color randomColor() {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      int red = threadLocalRandom.nextInt(0, 256);
      int green = threadLocalRandom.nextInt(0, 256);
      int blue = threadLocalRandom.nextInt(0, 256);
      return Color.fromRGB(red, green, blue);
   }

   public static DustOptions randomDust() {
      float value = randInt(5, 10) / 10.0F;
      return new DustOptions(randomColor(), value);
   }

   public static void blackSun(double baseRadius, double radiusStep, double baseRate, double rateStep, ParticleDisplay particleDisplay) {
      double rateOffset = 0.0;
      double radiusOffset = 10.0;

      while (radiusOffset > 0.0) {
         rateOffset += rateStep;
         circle(baseRadius + radiusOffset, baseRate - rateOffset, particleDisplay);
         radiusOffset -= radiusStep;
      }
   }

   public static void circle(double radius, double rate, ParticleDisplay particleDisplay) {
      circle(radius, radius, 1.0, rate, 0.0, particleDisplay);
   }

   public static void circle(double radiusX, double radiusZ, double frequency, double rate, double maxAngle, ParticleDisplay particleDisplay) {
      double angleStep = 3.141592653589793 / Math.abs(rate);
      if (maxAngle == 0.0) {
         maxAngle = 6.283185307179586;
      } else if (maxAngle == -1.0) {
         maxAngle = 6.283185307179586 / Math.abs(frequency);
      }

      for (double angle = 0.0; angle <= maxAngle; angle += angleStep) {
         double x = radiusX * Math.cos(frequency * angle);
         double z = radiusZ * Math.sin(frequency * angle);
         if (particleDisplay.isDirectional()) {
            double theta = Math.atan2(z, x);
            double dirX = Math.cos(frequency * theta);
            double dirZ = Math.sin(frequency * theta);
            particleDisplay.particleDirection(dirX, particleDisplay.getOffset().getY(), dirZ);
         }

         particleDisplay.spawn(x, 0.0, z);
      }
   }

   public static void diamond(double widthStep, double step, double height, ParticleDisplay particleDisplay) {
      double halfWidth = 0.0;
      double y = 0.0;

      while (y < height * 2.0) {
         if (y < height) {
            halfWidth += widthStep;
         } else {
            halfWidth -= widthStep;
         }

         for (double x = -halfWidth; x < halfWidth; x += step) {
            particleDisplay.spawn(x, y, 0.0);
         }

         y += step;
      }
   }

   public static Runnable circularBeam(final double maxRadius, final double rate, final double radiusRate, final double distance, final ParticleDisplay particleDisplay) {
      return new Runnable() {
         final double rateDiv = 3.141592653589793 / rate;
         final double radiusDiv = 3.141592653589793 / radiusRate;
         final Vector dir = particleDisplay.getLocation().getDirection().normalize().multiply(distance);
         double dynamicRadius = 0.0;

         @Override
         public void run() {
            double radius = maxRadius * Math.sin(this.dynamicRadius);

            for (double angle = 0.0; angle < 6.283185307179586; angle += this.rateDiv) {
               double x = radius * Math.sin(angle);
               double z = radius * Math.cos(angle);
               particleDisplay.spawn(x, 0.0, z);
            }

            this.dynamicRadius = this.dynamicRadius + this.radiusDiv;
            if (this.dynamicRadius > 3.141592653589793) {
               this.dynamicRadius = 0.0;
            }

            particleDisplay.getLocation().add(this.dir);
         }
      };
   }

   public static BukkitTask circularBeam(Plugin plugin, double maxRadius, double rate, double radiusRate, double distance, ParticleDisplay particleDisplay) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, circularBeam(maxRadius, rate, radiusRate, distance, particleDisplay), 0L, 1L);
   }

   public static void flower(int petals, double radius, ParticleDisplay particleDisplay, Runnable task) {
      double angle = 0.0;

      while (angle < 6.283185307179586) {
         double x = radius * Math.cos(angle);
         double z = radius * Math.sin(angle);
         particleDisplay.getLocation().add(x, 0.0, z);
         task.run();
         particleDisplay.getLocation().subtract(x, 0.0, z);
         angle += 6.283185307179586 / petals;
      }
   }

   public static void filledCircle(double radius, double rate, double step, ParticleDisplay particleDisplay) {
      double currentRate = 0.0;
      double currentRadius = 0.1;

      while (currentRadius < radius) {
         if (currentRadius > radius) {
            currentRadius = radius;
         }

         currentRate += rate / (radius / step);
         circle(currentRadius, currentRate, particleDisplay);
         currentRadius += step;
      }
   }

   public static Runnable chaoticDoublePendulum(
      final double scale,
      final double gravity,
      final double firstLength,
      final double secondLength,
      final double firstMass,
      final double secondMass,
      final boolean rotating,
      final int iterations,
      final ParticleDisplay particleDisplay
   ) {
      return new Runnable() {
         double theta = 1.5707963267948966;
         double theta2 = 1.5707963267948966;
         double thetaPrime = 0.0;
         double thetaPrime2 = 0.0;

         @Override
         public void run() {
            int remaining = iterations;

            while (remaining-- != 0) {
               if (rotating) {
                  particleDisplay.rotate(0.09519977738150888, 0.07139983303613166, 0.057119866428905326);
               }

               double totalMass = firstMass + secondMass;
               double doubleTotalMass = 2.0 * totalMass;
               double angleDiff = this.theta - this.theta2;
               double denominatorBase = doubleTotalMass - secondMass * Math.cos(2.0 * this.theta - 2.0 * this.theta2);
               double cosDiff = Math.cos(angleDiff);
               double sinDiff = Math.sin(angleDiff);
               double firstCentrifugal = this.thetaPrime * this.thetaPrime * firstLength;
               double secondCentrifugal = this.thetaPrime2 * this.thetaPrime2 * secondLength;
               double termA = -gravity * doubleTotalMass * Math.sin(this.theta);
               double termB = -secondMass * gravity * Math.sin(this.theta - 2.0 * this.theta2);
               double termC = -2.0 * sinDiff * secondMass;
               double termD = secondCentrifugal + firstCentrifugal * cosDiff;
               double denominator = firstLength * denominatorBase;
               double firstAcceleration = (termA + termB + termC * termD) / denominator;
               termA = 2.0 * sinDiff;
               termB = firstCentrifugal * totalMass;
               termC = gravity * totalMass * Math.cos(this.theta);
               termD = secondCentrifugal * secondMass * cosDiff;
               denominator = secondLength * denominatorBase;
               double secondAcceleration = termA * (termB + termC + termD) / denominator;
               this.thetaPrime += firstAcceleration;
               this.thetaPrime2 += secondAcceleration;
               this.theta = this.theta + this.thetaPrime;
               this.theta2 = this.theta2 + this.thetaPrime2;
               double firstX = scale * Math.sin(this.theta);
               double firstY = scale * Math.cos(this.theta);
               double secondX = firstX + scale * Math.sin(this.theta2);
               double secondY = firstY + scale * Math.cos(this.theta2);
               particleDisplay.spawn(secondX, secondY, 0.0);
            }
         }
      };
   }

   public static BukkitTask chaoticDoublePendulum(
      Plugin plugin, double scale, double gravity, double firstLength, double secondLength, double firstMass, double secondMass, boolean rotating, int iterations, ParticleDisplay particleDisplay
   ) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, chaoticDoublePendulum(scale, gravity, firstLength, secondLength, firstMass, secondMass, rotating, iterations, particleDisplay), 0L, 1L);
   }

   public static Runnable magicCircles(final double startRadius, final double rate, final double radiusRate, final double distance, final ParticleDisplay particleDisplay) {
      return new Runnable() {
         final double radiusDiv = 3.141592653589793 / radiusRate;
         final Vector dir = particleDisplay.getLocation().getDirection().normalize().multiply(distance);
         double dynamicRadius = startRadius;

         @Override
         public void run() {
            double angleStep = 3.141592653589793 / (rate * this.dynamicRadius);

            for (double angle = 0.0; angle < 6.283185307179586; angle += angleStep) {
               double x = this.dynamicRadius * Math.sin(angle);
               double z = this.dynamicRadius * Math.cos(angle);
               particleDisplay.spawn(x, 0.0, z);
            }

            this.dynamicRadius = this.dynamicRadius + this.radiusDiv;
            particleDisplay.getLocation().add(this.dir);
         }
      };
   }

   public static BukkitTask magicCircles(Plugin plugin, double startRadius, double rate, double radiusRate, double distance, ParticleDisplay particleDisplay) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, magicCircles(startRadius, rate, radiusRate, distance, particleDisplay), 0L, 1L);
   }

   public static void infinity(double scale, double rate, ParticleDisplay particleDisplay) {
      double step = 3.141592653589793 / rate;

      for (double angle = 0.0; angle < 6.283185307179586; angle += step) {
         double sinAngle = Math.sin(angle);
         double divisor = Math.pow(sinAngle, 2.0) + 1.0;
         double numerator = scale * Math.cos(angle);
         double zOffset = numerator / divisor;
         double yOffset = numerator * sinAngle / divisor;
         circle(1.0, rate, particleDisplay.cloneWithLocation(sinAngle, yOffset, zOffset));
      }
   }

   public static void cone(double height, double radius, double step, double rate, ParticleDisplay particleDisplay) {
      double radiusShrink = radius / (height / step);

      for (double y = 0.0; y < height; y += step) {
         radius -= radiusShrink;
         if (radius < 0.0) {
            radius = 0.0;
         }

         circle(radius, rate - y, particleDisplay.cloneWithLocation(0.0, y, 0.0));
      }
   }

   public static void slash(double size, boolean flag, ParticleDisplay particleDisplay) {
      double startAngle = flag ? 1.5707963267948966 : 0.0;
      double endAngle = flag ? R270 : 3.141592653589793;
      ellipse(startAngle, endAngle, 0.10471975511965977, size, size + 2.0, particleDisplay);
   }

   public static void slash(
      Plugin plugin, final double maxDistance, final boolean flag, final Supplier<Double> sizeSupplier, final Supplier<Double> speedSupplier, final ParticleDisplay particleDisplay
   ) {
      (new BukkitRunnable() {
         double distanceTraveled = 0.0;

         public void run() {
            Particles.slash((Double)sizeSupplier.get(), flag, particleDisplay);
            double speed = (Double)speedSupplier.get();
            this.distanceTraveled += speed;
            if (this.distanceTraveled >= maxDistance) {
               this.cancel();
            } else {
               particleDisplay.advanceInDirection(speed);
            }
         }
      }).runTaskTimerAsynchronously(plugin, 1L, 1L);
   }

   public static void ellipse(double startAngle, double endAngle, double step, double radiusX, double radiusZ, ParticleDisplay particleDisplay) {
      double angle = startAngle;

      while (angle <= endAngle) {
         double x = radiusX * Math.cos(angle);
         double z = radiusZ * Math.sin(angle);
         particleDisplay.spawn(x, 0.0, z);
         angle += step;
      }
   }

   public static BooleanSupplier blackhole(final int points, final double radius, final double rate, final int mode, final int duration, final ParticleDisplay particleDisplay) {
      particleDisplay.extra = 0.1;
      return new BooleanSupplier() {
         final double rateDiv = 3.141592653589793 / rate;
         int timer = duration;
         double theta = 0.0;
         boolean done = false;

         @Override
         public boolean getAsBoolean() {
            if (this.done) {
               return false;
            }

            for (int i = 0; i < points; i++) {
               double pointAngle = 6.283185307179586 * ((double)i / points);
               double x = radius * Math.cos(this.theta + pointAngle);
               double z = radius * Math.sin(this.theta + pointAngle);
               double angle = Math.atan2(z, x);
               double dirX = -Math.cos(angle);
               double dirZ = -Math.sin(angle);
               particleDisplay.particleDirection(dirX, 0.0, dirZ);
               particleDisplay.spawn(x, 0.0, z);
               if (mode > 1) {
                  x = radius * Math.cos(-this.theta + pointAngle);
                  z = radius * Math.sin(-this.theta + pointAngle);
                  if (mode == 2) {
                     angle = Math.atan2(z, x);
                  } else if (mode == 3) {
                     angle = Math.atan2(x, z);
                  } else if (mode == 4) {
                     Math.atan2(Math.log(x), Math.log(z));
                  }

                  dirX = -Math.cos(angle);
                  dirZ = -Math.sin(angle);
                  particleDisplay.particleDirection(dirX, 0.0, dirZ);
                  particleDisplay.spawn(x, 0.0, z);
               }
            }

            this.theta = this.theta + this.rateDiv;
            if (--this.timer <= 0) {
               this.done = true;
               return false;
            } else {
               return true;
            }
         }
      };
   }

   public static BukkitTask blackhole(Plugin plugin, int points, double radius, double rate, int mode, int duration, ParticleDisplay particleDisplay) {
      final BooleanSupplier booleanSupplier = blackhole(points, radius, rate, mode, duration, particleDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   public static void rainbow(double radius, double rate, double heightRatio, double ringsPerColor, double radiusIncrement, ParticleDisplay particleDisplay) {
      int[][] countses = new int[][]{{128, 0, 128}, {75, 0, 130}, {0, 0, 255}, {0, 255, 0}, {255, 255, 0}, {255, 140, 0}, {255, 0, 0}};
      double yRadius = radius * heightRatio;

      for (int i = 0; i < 7; i++) {
         int[] counts = countses[i];
         particleDisplay = ParticleDisplay.of(XParticle.DUST).withLocation(particleDisplay.getLocation()).withColor(new java.awt.Color(counts[0], counts[1], counts[2]), 1.0F);

         for (int j = 0; j < ringsPerColor; j++) {
            double angleStep = 3.141592653589793 / (rate * (i + 2));

            for (double angle = 0.0; angle <= 3.141592653589793; angle += angleStep) {
               double x = radius * Math.cos(angle);
               double y = yRadius * Math.sin(angle);
               particleDisplay.spawn(x, y, 0.0);
            }

            radius += radiusIncrement;
         }
      }
   }

   public static void crescent(double radius, double rate, ParticleDisplay particleDisplay) {
      double angleStep = 3.141592653589793 / rate;
      double endAngle = Math.toRadians(325.0);

      for (double angle = Math.toRadians(45.0); angle <= endAngle; angle += angleStep) {
         double cos = Math.cos(angle);
         double sin = Math.sin(angle);
         particleDisplay.spawn(radius * cos, 0.0, radius * sin);
         double innerRadius = radius / 1.3;
         particleDisplay.spawn(innerRadius * cos + 0.8, 0.0, innerRadius * sin);
      }
   }

   public static void waveFunction(double scale, double amplitude, double cycles, double rate, ParticleDisplay particleDisplay) {
      double height = amplitude / 2.0;
      boolean rising = true;
      double heightStep = random(amplitude / 2.0, amplitude);
      double angleStep = 3.141592653589793 / rate;
      cycles *= 6.283185307179586;

      for (double outerAngle = 0.0; outerAngle <= cycles; outerAngle += angleStep) {
         double x = scale * outerAngle;
         double sinOuter = Math.sin(outerAngle);
         if (sinOuter == 1.0) {
            rising = !rising;
            if (rising) {
               heightStep = random(amplitude / 2.0, amplitude);
            } else {
               heightStep = random(-amplitude, -amplitude / 2.0);
            }
         }

         height += heightStep;

         for (double innerAngle = 0.0; innerAngle <= cycles; innerAngle += angleStep) {
            double cosInner = Math.cos(innerAngle);
            double y = height * sinOuter * cosInner;
            double z = scale * innerAngle;
            particleDisplay.spawn(x, y, z);
         }
      }
   }

   public static Runnable vortex(final int points, double rate, final ParticleDisplay particleDisplay) {
      final double angleStep = 3.141592653589793 / rate;
      return new Runnable() {
         double theta = 0.0;

         @Override
         public void run() {
            this.theta = this.theta + angleStep;

            for (int i = 0; i < points; i++) {
               double pointAngle = 6.283185307179586 * ((double)i / points);
               double x = Math.cos(this.theta + pointAngle);
               double z = Math.sin(this.theta + pointAngle);
               double angle = Math.atan2(z, x);
               double dirX = Math.cos(angle);
               double dirZ = Math.sin(angle);
               particleDisplay.particleDirection(dirX, 0.0, dirZ);
               particleDisplay.spawn(x, 0.0, z);
            }
         }
      };
   }

   public static BukkitTask vortex(Plugin plugin, int points, double rate, ParticleDisplay particleDisplay) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, vortex(points, rate, particleDisplay), 0L, 1L);
   }

   public static void cylinder(double height, double radius, double rate, ParticleDisplay particleDisplay) {
      filledCircle(radius, rate, 3.0, particleDisplay);
      filledCircle(radius, rate, 3.0, particleDisplay.cloneWithLocation(0.0, height, 0.0));

      for (double y = 0.0; y < height; y += 0.1) {
         circle(radius, rate, particleDisplay.cloneWithLocation(0.0, y, 0.0));
      }
   }

   public static Runnable moveRotatingAround(
      final double rotationSpeed, final double xFactor, final double yFactor, final double zFactor, final Runnable task, final ParticleDisplay... particleDisplays
   ) {
      return new Runnable() {
         double rotation = 180.0;

         @Override
         public void run() {
            this.rotation = this.rotation + rotationSpeed;
            double angleX = Math.toRadians(90.0 + this.rotation);
            double angleY = Math.toRadians(60.0 + this.rotation);
            double angleZ = Math.toRadians(30.0 + this.rotation);
            Vector vector = new Vector(xFactor * 3.141592653589793, yFactor * 3.141592653589793, zFactor * 3.141592653589793);
            if (xFactor != 0.0) {
               ParticleDisplay.rotateAround(vector, ParticleDisplay.Axis.X, angleX);
            }

            if (yFactor != 0.0) {
               ParticleDisplay.rotateAround(vector, ParticleDisplay.Axis.Y, angleY);
            }

            if (zFactor != 0.0) {
               ParticleDisplay.rotateAround(vector, ParticleDisplay.Axis.Z, angleZ);
            }

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.getLocation().add(vector);
            }

            task.run();

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.getLocation().subtract(vector);
            }
         }
      };
   }

   public static BukkitTask moveRotatingAround(
      Plugin plugin, long period, double rotationSpeed, double xFactor, double yFactor, double zFactor, Runnable task, ParticleDisplay... particleDisplays
   ) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, moveRotatingAround(rotationSpeed, xFactor, yFactor, zFactor, task, particleDisplays), 0L, period);
   }

   public static Runnable moveAround(
      final double speed, final double maxMultiplier, final double dirX, final double dirY, final double dirZ, final Runnable task, final ParticleDisplay... particleDisplays
   ) {
      return new Runnable() {
         double multiplier = 0.0;
         boolean opposite = false;

         @Override
         public void run() {
            if (this.opposite) {
               this.multiplier = this.multiplier - speed;
            } else {
               this.multiplier = this.multiplier + speed;
            }

            double offsetX = this.multiplier * dirX;
            double offsetY = this.multiplier * dirY;
            double offsetZ = this.multiplier * dirZ;

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.getLocation().add(offsetX, offsetY, offsetZ);
            }

            task.run();

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.getLocation().subtract(offsetX, offsetY, offsetZ);
            }

            if (this.opposite) {
               if (this.multiplier <= 0.0) {
                  this.opposite = false;
               }
            } else if (this.multiplier >= maxMultiplier) {
               this.opposite = true;
            }
         }
      };
   }

   public static BukkitTask moveAround(
      Plugin plugin, long period, double speed, double maxMultiplier, double dirX, double dirY, double dirZ, Runnable task, ParticleDisplay... particleDisplays
   ) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, moveAround(speed, maxMultiplier, dirX, dirY, dirZ, task, particleDisplays), 0L, period);
   }

   public static BukkitTask testDisplay(Plugin plugin, Runnable task) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, 0L, 1L);
   }

   public static Runnable rotateAround(
      final double rotationSpeed, final double xFactor, final double yFactor, final double zFactor, final Runnable task, final ParticleDisplay... particleDisplays
   ) {
      return new Runnable() {
         double rotation = 180.0;

         @Override
         public void run() {
            this.rotation = this.rotation + rotationSpeed;
            double angleX = Math.toRadians((90.0 + this.rotation) * xFactor);
            double angleY = Math.toRadians((60.0 + this.rotation) * yFactor);
            double angleZ = Math.toRadians((30.0 + this.rotation) * zFactor);

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.rotate(angleX, angleY, angleZ);
            }

            task.run();
         }
      };
   }

   public static BukkitTask rotateAround(Plugin plugin, long period, double rotationSpeed, double xFactor, double yFactor, double zFactor, Runnable task, ParticleDisplay... particleDisplays) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, rotateAround(rotationSpeed, xFactor, yFactor, zFactor, task, particleDisplays), 0L, period);
   }

   public static Runnable guard(final double rotationSpeed, final double xFactor, final double yFactor, final double zFactor, final Runnable task, final ParticleDisplay... particleDisplays) {
      return new Runnable() {
         double rotation = 180.0;

         @Override
         public void run() {
            this.rotation = this.rotation + rotationSpeed;
            double angleX = Math.toRadians((90.0 + this.rotation) * xFactor);
            double angleY = Math.toRadians((60.0 + this.rotation) * yFactor);
            double angleZ = Math.toRadians((30.0 + this.rotation) * zFactor);
            Vector vector = new Vector(xFactor * 3.141592653589793, yFactor * 3.141592653589793, zFactor * 3.141592653589793);
            ParticleDisplay.rotateAround(vector, angleX, angleY, angleZ);

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.rotate(angleX, angleY, angleZ);
               particleDisplay.getLocation().add(vector);
            }

            task.run();

            for (ParticleDisplay particleDisplay : particleDisplays) {
               particleDisplay.getLocation().subtract(vector);
            }
         }
      };
   }

   public static BukkitTask guard(Plugin plugin, long period, double rotationSpeed, double xFactor, double yFactor, double zFactor, Runnable task, ParticleDisplay... particleDisplays) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, guard(rotationSpeed, xFactor, yFactor, zFactor, task, particleDisplays), 0L, period);
   }

   public static void sphere(double radius, double rate, ParticleDisplay particleDisplay) {
      double angleStep = 3.141592653589793 / rate;

      for (double polarAngle = 0.0; polarAngle <= 3.141592653589793; polarAngle += angleStep) {
         double y = radius * Math.cos(polarAngle);
         double ringRadius = radius * Math.sin(polarAngle);

         for (double azimuth = 0.0; azimuth <= 6.283185307179586; azimuth += angleStep) {
            double x = Math.cos(azimuth) * ringRadius;
            double z = Math.sin(azimuth) * ringRadius;
            if (particleDisplay.isDirectional()) {
               double horizontalAngle = Math.atan2(z, x);
               double dirX = Math.cos(horizontalAngle);
               double dirY = Math.sin(Math.atan2(ringRadius, y));
               double dirZ = Math.sin(horizontalAngle);
               particleDisplay.particleDirection(dirX, dirY, dirZ);
            }

            particleDisplay.spawn(x, y, z);
         }
      }
   }

   public static void spikeSphere(double radius, double rate, int spikeChance, double minScale, double maxScale, ParticleDisplay particleDisplay) {
      double angleStep = 3.141592653589793 / rate;

      for (double polarAngle = 0.0; polarAngle <= 3.141592653589793; polarAngle += angleStep) {
         double y = radius * Math.cos(polarAngle);
         double ringRadius = radius * Math.sin(polarAngle);

         for (double azimuth = 0.0; azimuth <= 6.283185307179586; azimuth += angleStep) {
            double x = Math.cos(azimuth) * ringRadius;
            double z = Math.sin(azimuth) * ringRadius;
            if (spikeChance == 0 || randInt(0, spikeChance) == 1) {
               Location location = particleDisplay.cloneLocation(x, y, z);
               Vector vector = location.clone().subtract(particleDisplay.getLocation()).toVector().multiply(random(minScale, maxScale));
               Location spikeEnd = location.clone().add(vector);
               line(location, spikeEnd, 0.1, particleDisplay);
            }
         }
      }
   }

   public static void ring(double ringRate, double ringRadius, double tubeRadius, ParticleDisplay particleDisplay) {
      double ringStep = 3.141592653589793 / ringRate;
      double tubeStep = 3.141592653589793 / tubeRadius;

      for (double ringAngle = 0.0; ringAngle <= 6.283185307179586; ringAngle += ringStep) {
         double cosRing = Math.cos(ringAngle);
         double sinRing = Math.sin(ringAngle);

         for (double tubeAngle = 0.0; tubeAngle <= 6.283185307179586; tubeAngle += tubeStep) {
            double distance = ringRadius + tubeRadius * Math.cos(tubeAngle);
            double x = distance * cosRing;
            double y = distance * sinRing;
            double z = tubeRadius * Math.sin(tubeAngle);
            particleDisplay.spawn(x, y, z);
         }
      }
   }

   public static BooleanSupplier spread(
      final int duration,
      final int perTick,
      final Location source,
      final Location center,
      final double spreadX,
      final double spreadY,
      final double spreadZ,
      final ParticleDisplay particleDisplay
   ) {
      return new BooleanSupplier() {
         int count = duration;
         boolean done = false;

         @Override
         public boolean getAsBoolean() {
            if (this.done) {
               return false;
            }

            int count = perTick;

            while (count-- != 0) {
               double offsetX = Particles.random(-spreadX, spreadX);
               double offsetY = Particles.random(-spreadY, spreadY);
               double offsetZ = Particles.random(-spreadZ, spreadZ);
               Location target = center.clone().add(offsetX, offsetY, offsetZ);
               Particles.line(source, target, 0.1, particleDisplay);
            }

            if (this.count-- <= 0) {
               this.done = true;
               return false;
            } else {
               return true;
            }
         }
      };
   }

   public static BukkitTask spread(Plugin plugin, int duration, int perTick, Location source, Location center, double spreadX, double spreadY, double spreadZ, ParticleDisplay particleDisplay) {
      final BooleanSupplier booleanSupplier = spread(duration, perTick, source, center, spreadX, spreadY, spreadZ, particleDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   public static void heart(double angleDivisor, double frequency, double sinWeight, double exponentDivisor, double density, ParticleDisplay particleDisplay) {
      double t = 0.0;

      while (t <= 6.283185307179586) {
         double angle = t / angleDivisor;
         double cos = Math.cos(angle);
         double sin = Math.sin(angle);
         double radius = Math.pow(Math.abs(Math.sin(2.0 * frequency * angle)) + sinWeight * Math.abs(Math.sin(frequency * angle)), 1.0 / exponentDivisor);
         double y = radius * (sin + cos);
         double z = radius * (cos - sin);
         particleDisplay.spawn(0.0, y, z);
         t += 3.141592653589793 / density;
      }
   }

   public static Runnable atomic(final int orbits, final double radius, final double rate, final ParticleDisplay particleDisplay) {
      return new Runnable() {
         final double rateDiv = 3.141592653589793 / rate;
         final double dist = 3.141592653589793 / orbits;
         double theta = 0.0;

         @Override
         public void run() {
            int remaining = orbits;
            this.theta = this.theta + this.rateDiv;
            double x = radius * Math.cos(this.theta);
            double z = radius * Math.sin(this.theta);

            for (double rotation = 0.0; remaining > 0; rotation += this.dist) {
               particleDisplay.rotate(ParticleDisplay.Rotation.of(rotation, ParticleDisplay.Axis.Z));
               particleDisplay.spawn(x, 0.0, z);
               remaining--;
            }
         }
      };
   }

   public static BukkitTask atomic(Plugin plugin, int orbits, double radius, double rate, ParticleDisplay particleDisplay) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, atomic(orbits, radius, rate, particleDisplay), 0L, 1L);
   }

   public static BooleanSupplier helix(
      final int points,
      final double radius,
      final double rate,
      final double spiralFactor,
      final double length,
      final double step,
      final double rotationDivisor,
      final boolean contract,
      final boolean expand,
      final ParticleDisplay particleDisplay
   ) {
      return new BooleanSupplier() {
         final double distanceBetweenEachCirclePoints = 6.283185307179586 / points;
         final double radiusDiv = radius / (length / rate);
         final double radiusDiv2 = contract && expand ? this.radiusDiv * 2.0 : this.radiusDiv;
         double dynamicRadius = expand ? 0.0 : radius;
         boolean center = !expand;
         final double calculatedRotRate = this.distanceBetweenEachCirclePoints / rotationDivisor;
         double rotation = 0.0;
         double currentDistance = 0.0;

         @Override
         public boolean getAsBoolean() {
            if (this.currentDistance >= length) {
               return false;
            }

            if (!this.center) {
               this.dynamicRadius = this.dynamicRadius + this.radiusDiv2;
               if (this.dynamicRadius >= radius) {
                  this.center = true;
               }
            } else if (contract) {
               this.dynamicRadius = this.dynamicRadius - this.radiusDiv2;
            }

            for (double pointIndex = 0.0; pointIndex < points; pointIndex++) {
               double pointAngle = pointIndex * this.distanceBetweenEachCirclePoints * spiralFactor + this.rotation;
               double x = this.dynamicRadius * Math.cos(pointAngle);
               double z = this.dynamicRadius * Math.sin(pointAngle);
               particleDisplay.spawn(x, 0.0, z);
            }

            this.currentDistance = this.currentDistance + step;
            if (this.currentDistance < length) {
               particleDisplay.advanceInDirection(step);
            } else {
               particleDisplay.advanceInDirection(step - (this.currentDistance - length));
            }

            this.rotation = this.rotation + this.calculatedRotRate;
            return true;
         }
      };
   }

   public static BukkitTask helix(
      Plugin plugin,
      int points,
      double radius,
      double rate,
      double spiralFactor,
      double length,
      double step,
      double rotationDivisor,
      boolean contract,
      boolean expand,
      ParticleDisplay particleDisplay
   ) {
      final BooleanSupplier booleanSupplier = helix(points, radius, rate, spiralFactor, length, step, rotationDivisor, contract, expand, particleDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   public static void lightning(
      Location location,
      Vector vector,
      int depth,
      int maxBranches,
      double randomness,
      double branchLength,
      double lengthDecay,
      double segmentDistance,
      double distanceDecay,
      double branchChance,
      double chanceDecay,
      ParticleDisplay particleDisplay
   ) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      if (depth > 0) {
         boolean retry = true;

         while (threadLocalRandom.nextDouble() < branchChance || retry) {
            Vector randomOffset = new Vector(threadLocalRandom.nextDouble(-randomness, randomness), threadLocalRandom.nextDouble(-randomness, randomness), threadLocalRandom.nextDouble(-randomness, randomness))
               .normalize()
               .multiply(threadLocalRandom.nextDouble(-randomness, randomness) * branchLength);
            Vector endVector = location.clone().toVector().add(vector.clone().multiply(segmentDistance)).add(randomOffset);
            Location endLocation = endVector.toLocation(location.getWorld());
            if (endLocation.distance(location) <= segmentDistance) {
               retry = true;
            } else {
               retry = false;
               int steps = (int)(location.distance(endLocation) / 0.1);
               Vector stepVector = endVector.clone().subtract(location.toVector()).normalize().multiply(0.1);

               for (int i = 0; i < steps; i++) {
                  Location point = location.clone().add(stepVector.clone().multiply(i));
                  particleDisplay.spawn(point);
               }

               lightning(endLocation.clone(), vector, depth - 1, maxBranches - 1, randomness, branchLength * lengthDecay, lengthDecay, segmentDistance * distanceDecay, distanceDecay, branchChance * chanceDecay, chanceDecay, particleDisplay);
               if (maxBranches <= 0) {
                  break;
               }
            }
         }
      }
   }

   public static void dna(double radius, double step, double frequency, int height, int rungInterval, ParticleDisplay particleDisplay, ParticleDisplay rungDisplay) {
      int rungCounter = 0;
      double y = 0.0;

      while (y <= height) {
         rungCounter++;
         double x = radius * Math.cos(frequency * y);
         double z = radius * Math.sin(frequency * y);
         Location strandA = particleDisplay.getLocation().clone().add(x, y, z);
         particleDisplay.spawn(x, y, z);
         Location strandB = particleDisplay.getLocation().clone().subtract(x, -y, z);
         particleDisplay.spawn(-x, y, -z);
         if (rungCounter >= rungInterval) {
            rungCounter = 0;
            line(strandA, strandB, step * 2.0, rungDisplay);
         }

         y += step;
      }
   }

   public static BooleanSupplier dnaReplication(
      final double radius, final double step, final int perTick, final double frequency, final int height, final int rungInterval, final ParticleDisplay particleDisplay
   ) {
      final ParticleDisplay blueDisplay = ParticleDisplay.of(XParticle.DUST).withColor(java.awt.Color.BLUE, 1.0F);
      final ParticleDisplay yellowDisplay = ParticleDisplay.of(XParticle.DUST).withColor(java.awt.Color.YELLOW, 1.0F);
      final ParticleDisplay greenDisplay = ParticleDisplay.of(XParticle.DUST).withColor(java.awt.Color.GREEN, 1.0F);
      final ParticleDisplay redDisplay = ParticleDisplay.of(XParticle.DUST).withColor(java.awt.Color.RED, 1.0F);
      return new BooleanSupplier() {
         double y = 0.0;
         int nucleotideDist = 0;
         boolean done = false;

         @Override
         public boolean getAsBoolean() {
            if (this.done) {
               return false;
            }

            int remaining = perTick;

            while (remaining-- != 0) {
               this.y = this.y + step;
               this.nucleotideDist++;
               double x = radius * Math.cos(frequency * this.y);
               double z = radius * Math.sin(frequency * this.y);
               Location strandA = particleDisplay.getLocation().clone().add(x, this.y, z);
               Particles.circle(0.1, 10.0, particleDisplay.cloneWithLocation(x, this.y, z));
               Location strandB = particleDisplay.getLocation().clone().subtract(x, -this.y, z);
               Particles.circle(0.1, 10.0, particleDisplay.cloneWithLocation(-x, this.y, -z));
               Location rungCenter = strandA.toVector().midpoint(strandB.toVector()).toLocation(strandA.getWorld());
               if (this.nucleotideDist >= rungInterval) {
                  this.nucleotideDist = 0;
                  if (Particles.randInt(0, 1) == 1) {
                     Particles.line(strandA, rungCenter, step - 0.1, blueDisplay);
                     Particles.line(strandB, rungCenter, step - 0.1, yellowDisplay);
                  } else {
                     Particles.line(strandA, rungCenter, step - 0.1, redDisplay);
                     Particles.line(strandB, rungCenter, step - 0.1, greenDisplay);
                  }
               }

               if (this.y > height) {
                  this.done = true;
                  return false;
               }
            }

            return true;
         }
      };
   }

   public static BukkitTask dnaReplication(Plugin plugin, double radius, double step, int perTick, double frequency, int height, int rungInterval, ParticleDisplay particleDisplay) {
      final BooleanSupplier booleanSupplier = dnaReplication(radius, step, perTick, frequency, height, rungInterval, particleDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   public static void drawLine(Player player, double length, double step, ParticleDisplay particleDisplay) {
      Location eyeLocation = player.getEyeLocation();
      line(eyeLocation, eyeLocation.clone().add(eyeLocation.getDirection().multiply(length)), step, particleDisplay);
   }

   public static Runnable cloud(ParticleDisplay firstDisplay, ParticleDisplay secondDisplay) {
      return () -> {
         firstDisplay.spawn();
         secondDisplay.spawn();
      };
   }

   public static BukkitTask cloud(Plugin plugin, ParticleDisplay firstDisplay, ParticleDisplay secondDisplay) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, cloud(firstDisplay, secondDisplay), 0L, 1L);
   }

   public static void line(Location start, Location end, double step, ParticleDisplay particleDisplay) {
      step = Math.abs(step);
      double dirX = end.getX() - start.getX();
      double dirY = end.getY() - start.getY();
      double dirZ = end.getZ() - start.getZ();
      double length = Math.sqrt(NumberConversions.square(dirX) + NumberConversions.square(dirY) + NumberConversions.square(dirZ));
      dirX /= length;
      dirY /= length;
      dirZ /= length;
      ParticleDisplay lineDisplay = particleDisplay.copy();
      lineDisplay.withLocation(start);

      for (double distance = 0.0; distance < length; distance += step) {
         if (distance > length) {
            distance = length;
         }

         lineDisplay.spawn(dirX * distance, dirY * distance, dirZ * distance);
      }
   }

   public static void rectangle(Location first, Location second, double step, ParticleDisplay particleDisplay) {
      particleDisplay.withLocation(first);
      double maxX = Math.max(first.getX(), second.getX());
      double minX = Math.min(first.getX(), second.getX());
      double maxY = Math.max(first.getY(), second.getY());
      double minY = Math.min(first.getY(), second.getY());
      double x = minX;

      while (x <= maxX) {
         for (double y = minY; y <= maxY; y += step) {
            particleDisplay.spawn(x - minX, y - minY, 0.0);
         }

         x += step;
      }
   }

   public static void cage(Location first, Location second, double step, double rungInterval, ParticleDisplay particleDisplay) {
      double maxX = Math.max(first.getX(), second.getX());
      double minX = Math.min(first.getX(), second.getX());
      double maxZ = Math.max(first.getZ(), second.getZ());
      double minZ = Math.min(first.getZ(), second.getZ());
      double edgeCounter = 0.0;
      double x = minX;

      while (x <= maxX) {
         for (double z = minZ; z <= maxZ; z += step) {
            Location bottom = particleDisplay.spawn(x - minX, 0.0, z - minZ);
            Location top = particleDisplay.spawn(x - minX, 3.0, z - minZ);
            if (x == minX || x + step > maxX || z == minZ || z + step > maxZ) {
               if (++edgeCounter >= rungInterval) {
                  edgeCounter = 0.0;
                  line(bottom, top, step, particleDisplay);
               }
            }
         }

         x += step;
      }
   }

   public static void filledCube(Location first, Location second, double step, ParticleDisplay particleDisplay) {
      particleDisplay.withLocation(first);
      double maxX = Math.max(first.getX(), second.getX());
      double minX = Math.min(first.getX(), second.getX());
      double maxY = Math.max(first.getY(), second.getY());
      double minY = Math.min(first.getY(), second.getY());
      double maxZ = Math.max(first.getZ(), second.getZ());
      double minZ = Math.min(first.getZ(), second.getZ());
      double x = minX;

      while (x <= maxX) {
         for (double y = minY; y <= maxY; y += step) {
            for (double z = minZ; z <= maxZ; z += step) {
               particleDisplay.spawn(x - minX, y - minY, z - minZ);
            }
         }

         x += step;
      }
   }

   public static void cube(Location first, Location second, double step, ParticleDisplay particleDisplay) {
      particleDisplay.withLocation(first);
      double maxX = Math.max(first.getX(), second.getX());
      double minX = Math.min(first.getX(), second.getX());
      double maxY = Math.max(first.getY(), second.getY());
      double minY = Math.min(first.getY(), second.getY());
      double maxZ = Math.max(first.getZ(), second.getZ());
      double minZ = Math.min(first.getZ(), second.getZ());
      double x = minX;

      while (x <= maxX) {
         for (double y = minY; y <= maxY; y += step) {
            for (double z = minZ; z <= maxZ; z += step) {
               if (y == minY || y + step > maxY || x == minX || x + step > maxX || z == minZ || z + step > maxZ) {
                  particleDisplay.spawn(x - minX, y - minY, z - minZ);
               }
            }
         }

         x += step;
      }
   }

   public static void structuredCube(Location first, Location second, double step, ParticleDisplay particleDisplay) {
      particleDisplay.withLocation(first);
      double maxX = Math.max(first.getX(), second.getX());
      double minX = Math.min(first.getX(), second.getX());
      double maxY = Math.max(first.getY(), second.getY());
      double minY = Math.min(first.getY(), second.getY());
      double maxZ = Math.max(first.getZ(), second.getZ());
      double minZ = Math.min(first.getZ(), second.getZ());
      double x = minX;

      while (x <= maxX) {
         for (double y = minY; y <= maxY; y += step) {
            for (double z = minZ; z <= maxZ; z += step) {
               int edgeCount = 0;
               if (x == minX || x + step > maxX) {
                  edgeCount++;
               }

               if (y == minY || y + step > maxY) {
                  edgeCount++;
               }

               if (z == minZ || z + step > maxZ) {
                  edgeCount++;
               }

               if (edgeCount >= 2) {
                  particleDisplay.spawn(x - minX, y - minY, z - minZ);
               }
            }
         }

         x += step;
      }
   }

   public static void hypercube(Location first, Location second, double step, double layerGap, int layers, ParticleDisplay particleDisplay) {
      ArrayList previousCorners = null;

      for (int i = 0; i < layers + 1; i++) {
         ArrayList currentCorners = new ArrayList(8);
         Location layerMin = first.clone().subtract(i * layerGap, i * layerGap, i * layerGap);
         Location layerMax = second.clone().add(i * layerGap, i * layerGap, i * layerGap);
         particleDisplay.withLocation(layerMin);
         double maxX = Math.max(layerMin.getX(), layerMax.getX());
         double minX = Math.min(layerMin.getX(), layerMax.getX());
         double maxY = Math.max(layerMin.getY(), layerMax.getY());
         double minY = Math.min(layerMin.getY(), layerMax.getY());
         double maxZ = Math.max(layerMin.getZ(), layerMax.getZ());
         double minZ = Math.min(layerMin.getZ(), layerMax.getZ());
         currentCorners.add(new Location(layerMin.getWorld(), maxX, maxY, maxZ));
         currentCorners.add(new Location(layerMin.getWorld(), minX, minY, minZ));
         currentCorners.add(new Location(layerMin.getWorld(), maxX, minY, maxZ));
         currentCorners.add(new Location(layerMin.getWorld(), minX, maxY, minZ));
         currentCorners.add(new Location(layerMin.getWorld(), minX, minY, maxZ));
         currentCorners.add(new Location(layerMin.getWorld(), maxX, minY, minZ));
         currentCorners.add(new Location(layerMin.getWorld(), maxX, maxY, minZ));
         currentCorners.add(new Location(layerMin.getWorld(), minX, maxY, maxZ));
         if (previousCorners != null) {
            for (int j = 0; j < 8; j++) {
               Location currentCorner = (Location)currentCorners.get(j);
               Location previousCorner = (Location)previousCorners.get(j);
               line(previousCorner, currentCorner, step, particleDisplay);
            }
         }

         previousCorners = currentCorners;

         for (double x = minX; x <= maxX; x += step) {
            for (double y = minY; y <= maxY; y += step) {
               for (double z = minZ; z <= maxZ; z += step) {
                  int edgeCount = 0;
                  if (x == minX || x + step > maxX) {
                     edgeCount++;
                  }

                  if (y == minY || y + step > maxY) {
                     edgeCount++;
                  }

                  if (z == minZ || z + step > maxZ) {
                     edgeCount++;
                  }

                  if (edgeCount >= 2) {
                     particleDisplay.spawn(x - minX, y - minY, z - minZ);
                  }
               }
            }
         }
      }
   }

   public static BooleanSupplier tesseract(final double scale, final double lineStep, final double angleStep, final long maxRepeats, final ParticleDisplay particleDisplay) {
      final double[][] vertices = new double[][]{
         {-1.0, -1.0, -1.0, 1.0},
         {1.0, -1.0, -1.0, 1.0},
         {1.0, 1.0, -1.0, 1.0},
         {-1.0, 1.0, -1.0, 1.0},
         {-1.0, -1.0, 1.0, 1.0},
         {1.0, -1.0, 1.0, 1.0},
         {1.0, 1.0, 1.0, 1.0},
         {-1.0, 1.0, 1.0, 1.0},
         {-1.0, -1.0, -1.0, -1.0},
         {1.0, -1.0, -1.0, -1.0},
         {1.0, 1.0, -1.0, -1.0},
         {-1.0, 1.0, -1.0, -1.0},
         {-1.0, -1.0, 1.0, -1.0},
         {1.0, -1.0, 1.0, -1.0},
         {1.0, 1.0, 1.0, -1.0},
         {-1.0, 1.0, 1.0, -1.0}
      };
      final ArrayList<int[]> list = new ArrayList<>();
      byte b = 1;

      for (int i = 0; i <= b; i++) {
         int count = 8 * i;

         for (int j = count; j < count + 4; j++) {
            list.add(new int[]{j, (j + 1) % 4 + count});
            list.add(new int[]{j + 4, (j + 1) % 4 + 4 + count});
            list.add(new int[]{j, j + 4});
         }
      }

      for (int i = 0; i < (b + 1) * 4; i++) {
         list.add(new int[]{i, i + 8});
      }

      return new BooleanSupplier() {
         double angle = 0.0;
         long repeat = 0L;
         boolean done = false;

         @Override
         public boolean getAsBoolean() {
            if (this.done) {
               return false;
            }

            double cos = Math.cos(this.angle);
            double sin = Math.sin(this.angle);
            double[][] rotationXY = new double[][]{{cos, -sin, 0.0, 0.0}, {sin, cos, 0.0, 0.0}, {0.0, 0.0, 1.0, 0.0}, {0.0, 0.0, 0.0, 1.0}};
            double[][] rotationZW = new double[][]{{1.0, 0.0, 0.0, 0.0}, {0.0, 1.0, 0.0, 0.0}, {0.0, 0.0, cos, -sin}, {0.0, 0.0, sin, cos}};
            double[][] projected = new double[vertices.length][4];

            for (int i = 0; i < vertices.length; i++) {
               double[] vertex = vertices[i];
               double[] rotated = Particles.matrix(rotationXY, vertex);
               rotated = Particles.matrix(rotationZW, rotated);
               byte cameraDistance = 2;
               double perspective = 1.0 / (cameraDistance - rotated[3]);
               double[][] projection = new double[][]{{perspective, 0.0, 0.0, 0.0}, {0.0, perspective, 0.0, 0.0}, {0.0, 0.0, perspective, 0.0}};
               double[] point = Particles.matrix(projection, rotated);

               for (int j = 0; j < point.length; j++) {
                  point[j] *= scale;
               }

               projected[i] = point;
               particleDisplay.spawn(point[0], point[1], point[2]);
            }

            for (int[] counts : list) {
               double[] startPoint = projected[counts[0]];
               double[] endPoint = projected[counts[1]];
               Location startLocation = particleDisplay.cloneLocation(startPoint[0], startPoint[1], startPoint[2]);
               Location endLocation = particleDisplay.cloneLocation(endPoint[0], endPoint[1], endPoint[2]);
               Particles.line(startLocation, endLocation, lineStep, particleDisplay);
            }

            if (++this.repeat > maxRepeats) {
               this.done = true;
               return false;
            } else {
               this.angle = this.angle + angleStep;
               return true;
            }
         }
      };
   }

   public static BukkitTask tesseract(Plugin plugin, double scale, double lineStep, double angleStep, long maxRepeats, ParticleDisplay particleDisplay) {
      final BooleanSupplier booleanSupplier = tesseract(scale, lineStep, angleStep, maxRepeats, particleDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   private static double[] matrix(double[][] left, double[] vector) {
      double[][] column = new double[4][1];
      column[0][0] = vector[0];
      column[1][0] = vector[1];
      column[2][0] = vector[2];
      column[3][0] = vector[3];
      int leftCols = left[0].length;
      int leftRows = left.length;
      int rightCols = column[0].length;
      int rightRows = column.length;
      double[][] product = new double[leftRows][rightRows];

      for (int i = 0; i < leftRows; i++) {
         for (int j = 0; j < rightCols; j++) {
            float sum = 0.0F;

            for (int k = 0; k < leftCols; k++) {
               sum += (float)(left[i][k] * column[k][j]);
            }

            product[i][j] = sum;
         }
      }

      double[] result = new double[]{product[0][0], product[1][0], product[2][0], 0.0};
      if (product.length > 3) {
         result[3] = product[3][0];
      }

      return result;
   }

   public static void mandelbrot(double extent, double zoom, double step, double offsetX, double offsetY, int maxIterations, ParticleDisplay particleDisplay) {
      double y = -extent;

      while (y < extent) {
         for (double x = -extent; x < extent; x += step) {
            double zImag = 0.0;
            double zReal = 0.0;
            double cReal = (x - offsetX) / zoom;
            double cImag = (y - offsetY) / zoom;

            int remaining;
            for (remaining = maxIterations; zReal * zReal + zImag * zImag <= 4.0 && remaining > 0; remaining--) {
               double newReal = zReal * zReal - zImag * zImag + cReal;
               zImag = 2.0 * zReal * zImag + cImag;
               zReal = newReal;
            }

            if (remaining == 0) {
               particleDisplay.spawn(x, y, 0.0);
            }
         }

         y += step;
      }
   }

   public static void julia(double extent, double zoom, int maxIterations, double offsetX, double offsetY, ParticleDisplay particleDisplay) {
      double cReal = -0.7;
      double cImag = 0.27015;

      for (double x = -extent; x < extent; x += 0.1) {
         for (double y = -extent; y < extent; y += 0.1) {
            double zReal = 1.5 * (extent - extent / 2.0) / (0.5 * zoom * extent) + offsetX;
            double zImag = (y - extent / 2.0) / (0.5 * zoom * extent) + offsetY;

            int remaining;
            for (remaining = maxIterations; zReal * zReal + zImag * zImag < 4.0 && remaining > 0; remaining--) {
               double newReal = zReal * zReal - zImag * zImag + cReal;
               zImag = 2.0 * zReal * zImag + cImag;
               zReal = newReal;
            }

            java.awt.Color color = new java.awt.Color((remaining << 21) + (remaining << 10) + remaining * 8);
            particleDisplay.withColor(color, 0.8F).spawn(x, y, 0.0);
         }
      }
   }

   public static List<BooleanSupplier> star(
      final int rotations,
      int spikes,
      double rate,
      final double radius,
      final double height,
      final double veinStep,
      final boolean sequential,
      final int perTick,
      final ParticleDisplay particleDisplay
   ) {
      final double ringAngleStep = 6.283185307179586 / rotations;
      final double thetaStep = 3.141592653589793 / rate;
      final ThreadLocalRandom threadLocalRandom = sequential ? null : ThreadLocalRandom.current();
      ArrayList list = new ArrayList();

      for (int i = 0; i < spikes * 2; i++) {
         final double tilt = i * 3.141592653589793 / spikes;
         list.add(new BooleanSupplier() {
            double vein = 0.0;
            double theta = 0.0;
            boolean done = false;

            @Override
            public boolean getAsBoolean() {
               if (this.done) {
                  return false;
               }

               int remaining = perTick;

               while (remaining-- != 0) {
                  this.theta = this.theta + thetaStep;
                  double veinOffset = (sequential ? this.vein : threadLocalRandom.nextDouble(0.0, veinStep)) * radius;
                  if (sequential) {
                     this.vein = this.vein + veinStep;
                  }

                  Vector vector = new Vector(Math.cos(this.theta), 0.0, Math.sin(this.theta));
                  vector.multiply((radius - veinOffset) * height / radius);
                  vector.setY(height + veinOffset);
                  ParticleDisplay.rotateAround(vector, ParticleDisplay.Axis.X, tilt);

                  for (int j = 0; j < rotations; j++) {
                     ParticleDisplay.rotateAround(vector, ParticleDisplay.Axis.Y, ringAngleStep);
                     particleDisplay.spawn(vector);
                  }
               }

               if (this.theta >= 6.283185307179586) {
                  this.done = true;
                  return false;
               } else {
                  return true;
               }
            }
         });
      }

      return list;
   }

   public static List<BukkitTask> star(
      Plugin plugin, int rotations, int spikes, double rate, double radius, double height, double veinStep, boolean sequential, int perTick, ParticleDisplay particleDisplay
   ) {
      ArrayList list = new ArrayList();

      for (final BooleanSupplier booleanSupplier : star(rotations, spikes, rate, radius, height, veinStep, sequential, perTick, particleDisplay)) {
         list.add((new BukkitRunnable() {
            public void run() {
               if (!booleanSupplier.getAsBoolean()) {
                  this.cancel();
               }
            }
         }).runTaskTimerAsynchronously(plugin, 0L, 1L));
      }

      return list;
   }

   public static void eye(double upperHeight, double lowerHeight, double density, double frequency, ParticleDisplay particleDisplay) {
      double angleStep = 3.141592653589793 / density;
      double maxAngle = 3.141592653589793 / frequency;
      double x = 0.0;

      for (double angle = 0.0; angle < maxAngle; angle += angleStep) {
         double upperY = upperHeight * Math.sin(frequency * angle);
         double lowerY = lowerHeight * Math.sin(frequency * -angle);
         particleDisplay.spawn(x, upperY, 0.0);
         particleDisplay.spawn(x, lowerY, 0.0);
         x += 0.1;
      }
   }

   public static void illuminati(double size, double eyeFrequency, ParticleDisplay particleDisplay) {
      polygon(3, 1, size, 1.0 / (size * 30.0), 0.0, particleDisplay);
      eye(size / 4.0, size / 4.0, 30.0, eyeFrequency, particleDisplay.cloneWithLocation(0.3, 0.0, size / 1.8).rotate(1.5707963267948966, 1.5707963267948966, 0.0));
      circle(size / 5.0, size * 5.0, particleDisplay.cloneWithLocation(0.3, 0.0, 0.0));
   }

   public static void polygon(int sides, int skip, double radius, double step, double overshoot, ParticleDisplay particleDisplay) {
      for (int i = 0; i < sides; i++) {
         double startAngle = Math.toRadians(360.0 / sides * i);
         double endAngle = Math.toRadians(360.0 / sides * (i + skip));
         double startX = Math.cos(startAngle) * radius;
         double startZ = Math.sin(startAngle) * radius;
         double endX = Math.cos(endAngle) * radius;
         double endZ = Math.sin(endAngle) * radius;
         double deltaX = endX - startX;
         double deltaZ = endZ - startZ;
         double t = 0.0;

         while (t < 1.0 + overshoot) {
            double x = startX + deltaX * t;
            double z = startZ + deltaZ * t;
            particleDisplay.spawn(x, 0.0, z);
            t += step;
         }
      }
   }

   public static void neopaganPentagram(double radius, double step, double overshoot, ParticleDisplay particleDisplay, ParticleDisplay circleDisplay) {
      polygon(5, 2, radius, step, overshoot, particleDisplay);
      circle(radius + 0.5, step * 1000.0, circleDisplay);
   }

   public static void atom(int orbits, double radius, double rate, ParticleDisplay particleDisplay, ParticleDisplay sphereDisplay) {
      double angleStep = 3.141592653589793 / orbits;

      for (double rotation = 0.0; orbits > 0; rotation += angleStep) {
         particleDisplay.rotate(ParticleDisplay.Rotation.of(rotation, ParticleDisplay.Axis.Z));
         circle(radius, rate, particleDisplay);
         orbits--;
      }

      sphere(radius / 3.0, rate / 2.0, sphereDisplay);
   }

   public static BooleanSupplier meguminExplosion(final double radius, final ParticleDisplay particleDisplay) {
      final BooleanSupplier booleanSupplier = spread(30, 2, particleDisplay.getLocation(), particleDisplay.getLocation().clone().add(0.0, 10.0, 0.0), 5.0, 5.0, 5.0, particleDisplay);
      return new BooleanSupplier() {
         boolean first = true;

         @Override
         public boolean getAsBoolean() {
            if (this.first) {
               this.first = false;
               Particles.polygon(10, 4, radius, 0.02, 0.3, particleDisplay);
               Particles.polygon(10, 3, radius / (radius - 1.0), 0.5, 0.0, particleDisplay);
               Particles.circle(radius, 40.0, particleDisplay);
            }

            return booleanSupplier.getAsBoolean();
         }
      };
   }

   public static BukkitTask meguminExplosion(Plugin plugin, double radius, ParticleDisplay particleDisplay) {
      final BooleanSupplier booleanSupplier = meguminExplosion(radius, particleDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   public static BooleanSupplier explosionWave(final double rate, final ParticleDisplay particleDisplay, final ParticleDisplay secondDisplay) {
      return new BooleanSupplier() {
         static final double addition = 0.3141592653589793;
         final double rateDiv = 3.141592653589793 / rate;
         double times = 0.7853981633974483;
         boolean done = false;

         @Override
         public boolean getAsBoolean() {
            if (this.done) {
               return false;
            }

            this.times += 0.3141592653589793;
            double angle = 0.0;

            while (angle <= 6.283185307179586) {
               double x = this.times * Math.cos(angle);
               double y = 2.0 * Math.exp(-0.1 * this.times) * Math.sin(this.times) + 1.5;
               double z = this.times * Math.sin(angle);
               particleDisplay.spawn(x, y, z);
               angle += 0.04908738521234052;
               x = this.times * Math.cos(angle);
               z = this.times * Math.sin(angle);
               secondDisplay.spawn(x, y, z);
               angle += this.rateDiv;
            }

            if (this.times > 20.0) {
               this.done = true;
               return false;
            } else {
               return true;
            }
         }
      };
   }

   public static BukkitTask explosionWave(Plugin plugin, double rate, ParticleDisplay particleDisplay, ParticleDisplay secondDisplay) {
      final BooleanSupplier booleanSupplier = explosionWave(rate, particleDisplay, secondDisplay);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, 1L);
   }

   private static BufferedImage getImage(Path path) {
      if (!Files.exists(path)) {
         return null;
      }

      try {
         return ImageIO.read(Files.newInputStream(path, StandardOpenOption.READ));
      } catch (IOException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   private static CompletableFuture<BufferedImage> getScaledImage(Path path, int width, int height) {
      return CompletableFuture.supplyAsync(() -> {
         BufferedImage image = getImage(path);
         if (image == null) {
            return null;
         }

         int scaledHeight = height;
         int scaledWidth = width;
         if (image.getWidth() > image.getHeight()) {
            scaledHeight = width * image.getHeight() / image.getWidth();
         } else {
            scaledWidth = height * image.getWidth() / image.getHeight();
         }

         BufferedImage bufferedImage = new BufferedImage(width, height, 2);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         graphics.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
         graphics.dispose();
         return bufferedImage;
      });
   }

   public static CompletableFuture<Map<double[], Color>> renderImage(Path path, int width, int height, double scale) {
      return getScaledImage(path, width, height).thenCompose(bufferedImage -> renderImage(bufferedImage, width, height, scale));
   }

   public static CompletableFuture<Map<double[], Color>> renderImage(BufferedImage bufferedImage, int targetWidth, int targetHeight, double scale) {
      return CompletableFuture.supplyAsync(() -> {
         if (bufferedImage == null) {
            return null;
         }

         int width = bufferedImage.getWidth();
         int height = bufferedImage.getHeight();
         double centerX = width / 2.0;
         double centerY = height / 2.0;
         HashMap map = new HashMap();

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               int rGB = bufferedImage.getRGB(j, i);
               if (rGB >> 24 != 0) {
                  java.awt.Color color = new java.awt.Color(rGB);
                  int red = color.getRed();
                  int green = color.getGreen();
                  int blue = color.getBlue();
                  double[] values = new double[]{(j - centerX) * scale, (i - centerY) * scale};
                  Color dustColor = Color.fromRGB(red, green, blue);
                  map.put(values, dustColor);
               }
            }
         }

         return map;
      });
   }

   public static BooleanSupplier displayRenderedImage(
      final Map<double[], Color> map, final Callable<Location> callable, final int repeats, final int particleCount, final int extra, final float size
   ) {
      return new BooleanSupplier() {
         int times = repeats;
         boolean done = false;

         @Override
         public boolean getAsBoolean() {
            if (this.done) {
               return false;
            }

            try {
               Particles.displayRenderedImage(map, (Location)callable.call(), particleCount, extra, size);
            } catch (Exception exception) {
               exception.printStackTrace();
            }

            if (this.times-- <= 0) {
               this.done = true;
               return false;
            } else {
               return true;
            }
         }
      };
   }

   public static BukkitTask displayRenderedImage(
      Plugin plugin, Map<double[], Color> map, Callable<Location> callable, int repeats, long period, int particleCount, int extra, float size
   ) {
      final BooleanSupplier booleanSupplier = displayRenderedImage(map, callable, repeats, particleCount, extra, size);
      return (new BukkitRunnable() {
         public void run() {
            if (!booleanSupplier.getAsBoolean()) {
               this.cancel();
            }
         }
      }).runTaskTimerAsynchronously(plugin, 0L, period);
   }

   public static void displayRenderedImage(Map<double[], Color> map, Location location, int particleCount, int extra, float size) {
      World world = location.getWorld();
      double yaw = location.getYaw();
      BlockFace blockFace;
      if (yaw >= 135.0 || yaw < -135.0) {
         blockFace = BlockFace.NORTH;
      } else if (yaw >= -135.0 && yaw < -45.0) {
         blockFace = BlockFace.EAST;
      } else if (yaw >= -45.0 && yaw < 45.0) {
         blockFace = BlockFace.SOUTH;
      } else {
         if (!(yaw >= 45.0) || !(yaw < 135.0)) {
            throw new IllegalArgumentException("Unknown rotation yaw: " + yaw);
         }

         blockFace = BlockFace.WEST;
      }

      for (Entry entry : map.entrySet()) {
         DustOptions dustOptions = new DustOptions((Color)entry.getValue(), size);
         double[] key = (double[])entry.getKey();
         double x;
         double y;
         double z;
         switch (blockFace) {
            case NORTH:
               x = location.getX() + key[0];
               y = location.getY() - key[1];
               z = location.getZ();
               break;
            case EAST:
               x = location.getX();
               y = location.getY() - key[1];
               z = location.getZ() + key[0];
               break;
            case SOUTH:
               x = location.getX() - key[0];
               y = location.getY() - key[1];
               z = location.getZ();
               break;
            case WEST:
               x = location.getX();
               y = location.getY() - key[1];
               z = location.getZ() - key[0];
               break;
            default:
               throw new AssertionError("Invalid facing: " + blockFace);
         }

         Location point = new Location(world, x, y, z);
         world.spawnParticle(XParticle.DUST.get(), point, particleCount, 0.0, 0.0, 0.0, extra, dustOptions);
      }
   }

   public static void saveImage(BufferedImage bufferedImage, Path path) {
      try {
         ImageIO.write(bufferedImage, "png", Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }

   public static CompletableFuture<BufferedImage> stringToImage(Font font, java.awt.Color color, String text) {
      return CompletableFuture.supplyAsync(() -> {
         BufferedImage bufferedImage = new BufferedImage(1, 1, 2);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setFont(font);
         FontRenderContext fontRenderContext = graphics.getFontMetrics().getFontRenderContext();
         Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
         graphics.dispose();
         bufferedImage = new BufferedImage((int)Math.ceil(stringBounds.getWidth()), (int)Math.ceil(stringBounds.getHeight()), 2);
         graphics = bufferedImage.createGraphics();
         graphics.setColor(color);
         graphics.setFont(font);
         graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
         graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
         graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
         FontMetrics fontMetrics = graphics.getFontMetrics();
         graphics.drawString(text, 0, fontMetrics.getAscent());
         graphics.dispose();
         return bufferedImage;
      });
   }
}
