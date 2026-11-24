package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.version.VParticle;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class RayTrace {
   private final LivingEntity hitEntity;
   private final double distanceTraveled;
   private final Location initialLocation;
   private final Vector initialDirection;
   private static final FluidCollisionMode FLUID_COLLISION_MODE = FluidCollisionMode.NEVER;
   private static final boolean IGNORE_PASSABLE_BLOCKS = true;
   private static final double RAY_SIZE = 0.2;

   public RayTrace(Player player, double range, Predicate<Entity> filter) {
      this(player.getEyeLocation(), player.getEyeLocation().getDirection(), range, filter);
   }

   public RayTrace(Player player, Vector direction, double range, Predicate<Entity> filter) {
      this(player.getEyeLocation(), direction, range, filter);
   }

   public RayTrace(Player player, EquipmentSlot hand, double range, Predicate<Entity> filter) {
      Validate.isTrue(hand.isHand(), "Not a hand equipment slot");
      double a = Math.toRadians(player.getEyeLocation().getYaw() + 90.0F + 45 * (hand == EquipmentSlot.MAIN_HAND ? 1 : -1));
      this.initialLocation = player.getLocation().add(Math.cos(a) * 0.5, 1.5, Math.sin(a) * 0.5);
      this.initialDirection = player.getEyeLocation().getDirection();
      RayTraceResult result = this.initialLocation
         .getWorld()
         .rayTrace(this.initialLocation, this.initialDirection, range, FLUID_COLLISION_MODE, true, 0.2, filter);
      this.hitEntity = result != null && result.getHitEntity() != null ? (LivingEntity)result.getHitEntity() : null;
      this.distanceTraveled = result == null ? range : result.getHitPosition().distance(this.initialLocation.toVector());
   }

   public RayTrace(Location loc, Vector direction, double range, Predicate<Entity> filter) {
      RayTraceResult result = loc.getWorld().rayTrace(loc, direction, range, FLUID_COLLISION_MODE, true, 0.2, filter);
      this.hitEntity = result != null && result.getHitEntity() != null ? (LivingEntity)result.getHitEntity() : null;
      this.distanceTraveled = result == null ? range : result.getHitPosition().distance(loc.toVector());
      this.initialDirection = direction;
      this.initialLocation = loc;
   }

   public boolean hasHit() {
      return this.hitEntity != null;
   }

   @Nullable
   public LivingEntity getHit() {
      return this.hitEntity;
   }

   public double getDistanceTraveled() {
      return this.distanceTraveled;
   }

   public void draw(double step, Color color) {
      this.draw(step, loc -> loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), loc, 0, new DustOptions(color, 1.0F)));
   }

   public void draw(double step, Consumer<Location> tick) {
      double d = 0.0;

      while (d < this.distanceTraveled) {
         tick.accept(this.initialLocation.clone().add(this.initialDirection.clone().multiply(d)));
         d += step;
      }
   }
}
