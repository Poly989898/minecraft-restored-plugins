package io.lumine.mythic.core.skills.cinematic;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;

public final class CameraPoint {
   private final AbstractLocation location;
   private final int holdTicks;
   private final Easing easingToNext;
   private final boolean relative;

   public CameraPoint(AbstractLocation location, int holdTicks, Easing easingToNext, boolean relative) {
      this.location = location;
      this.holdTicks = Math.max(0, holdTicks);
      this.easingToNext = easingToNext;
      this.relative = relative;
   }

   public AbstractLocation getLocation() {
      return this.location;
   }

   public int getHoldTicks() {
      return this.holdTicks;
   }

   public Easing getEasingToNext() {
      return this.easingToNext;
   }

   public boolean isRelative() {
      return this.relative;
   }

   public CameraPoint resolve(AbstractLocation anchor) {
      if (this.relative && anchor != null) {
         AbstractLocation absolute = new AbstractLocation(
            anchor.getWorld(),
            anchor.getX() + this.location.getX(),
            anchor.getY() + this.location.getY(),
            anchor.getZ() + this.location.getZ(),
            this.location.getYaw(),
            this.location.getPitch()
         );
         return new CameraPoint(absolute, this.holdTicks, this.easingToNext, false);
      } else {
         return this;
      }
   }

   public static CameraPoint fromString(String raw) {
      if (raw == null) {
         throw new IllegalArgumentException("CameraPoint string is null");
      } else {
         String[] parts = raw.trim().split(",");
         if (parts.length < 6) {
            throw new IllegalArgumentException("CameraPoint requires at least world,x,y,z,yaw,pitch \u2014 got: " + raw);
         } else {
            String world = parts[0].trim();
            double x = Double.parseDouble(parts[1].trim());
            double y = Double.parseDouble(parts[2].trim());
            double z = Double.parseDouble(parts[3].trim());
            float yaw = Float.parseFloat(parts[4].trim());
            float pitch = Float.parseFloat(parts[5].trim());
            int hold = 0;
            Easing easing = null;
            if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
               hold = Integer.parseInt(parts[6].trim());
            }

            if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
               easing = Easing.fromString(parts[7].trim(), null);
            }

            boolean relative = "~".equals(world);
            AbstractLocation loc;
            if (relative) {
               loc = new AbstractLocation((AbstractWorld)null, x, y, z, yaw, pitch);
            } else {
               AbstractWorld resolved = MythicProvider.get().getBootstrap().getWorld(world);
               if (resolved == null) {
                  throw new IllegalArgumentException("Unknown world '" + world + "' in camera point: " + raw);
               }

               loc = new AbstractLocation(resolved, x, y, z, yaw, pitch);
            }

            return new CameraPoint(loc, hold, easing, relative);
         }
      }
   }
}
