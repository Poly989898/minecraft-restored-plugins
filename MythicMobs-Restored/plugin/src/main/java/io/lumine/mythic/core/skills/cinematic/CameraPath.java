package io.lumine.mythic.core.skills.cinematic;

import io.lumine.mythic.api.adapters.AbstractLocation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class CameraPath {
   private final List<CameraPoint> points;
   private final int totalDuration;
   private final boolean loop;
   private final Easing defaultEasing;
   private final int[] legStartFrames;
   private final int[] legDurations;
   private File sourceFile;

   public CameraPath(List<CameraPoint> points, int totalDuration, boolean loop, Easing defaultEasing) {
      if (points != null && !points.isEmpty()) {
         this.points = List.copyOf(points);
         this.loop = loop;
         this.defaultEasing = defaultEasing == null ? Easing.LINEAR : defaultEasing;
         int numLegs = Math.max(0, this.points.size() - 1);
         this.legDurations = new int[numLegs];
         this.legStartFrames = new int[numLegs];
         int sumSpecified = 0;
         int specifiedCount = 0;

         for (int i = 0; i < numLegs; i++) {
            int h = this.points.get(i).getHoldTicks();
            if (h > 0) {
               sumSpecified += h;
               specifiedCount++;
            }
         }

         if (numLegs == 0) {
            this.totalDuration = Math.max(1, totalDuration);
         } else if (specifiedCount == numLegs) {
            for (int ix = 0; ix < numLegs; ix++) {
               this.legDurations[ix] = Math.max(1, this.points.get(ix).getHoldTicks());
            }

            this.totalDuration = sumSpecified;
         } else {
            int remainingDuration = Math.max(numLegs - specifiedCount, totalDuration - sumSpecified);
            int unspecifiedLegs = numLegs - specifiedCount;
            int perLeg = Math.max(1, remainingDuration / unspecifiedLegs);
            int assignedSum = 0;

            for (int ix = 0; ix < numLegs; ix++) {
               int h = this.points.get(ix).getHoldTicks();
               this.legDurations[ix] = h > 0 ? h : perLeg;
               assignedSum += this.legDurations[ix];
            }

            this.totalDuration = assignedSum;
         }

         int acc = 0;

         for (int ix = 0; ix < numLegs; ix++) {
            this.legStartFrames[ix] = acc;
            acc += this.legDurations[ix];
         }
      } else {
         throw new IllegalArgumentException("CameraPath requires at least one point");
      }
   }

   public int getTotalDuration() {
      return this.totalDuration;
   }

   public boolean isLoop() {
      return this.loop;
   }

   public List<CameraPoint> getPoints() {
      return this.points;
   }

   public Easing getDefaultEasing() {
      return this.defaultEasing;
   }

   public File getSourceFile() {
      return this.sourceFile;
   }

   public void setSourceFile(File f) {
      this.sourceFile = f;
   }

   public boolean hasRelativePoints() {
      for (CameraPoint p : this.points) {
         if (p.isRelative()) {
            return true;
         }
      }

      return false;
   }

   public CameraPath resolveRelative(AbstractLocation anchor) {
      if (anchor != null && this.hasRelativePoints()) {
         List<CameraPoint> resolved = new ArrayList<>(this.points.size());

         for (CameraPoint p : this.points) {
            resolved.add(p.resolve(anchor));
         }

         return new CameraPath(resolved, this.totalDuration, this.loop, this.defaultEasing);
      } else {
         return this;
      }
   }

   public AbstractLocation sample(int frame) {
      if (this.points.size() == 1) {
         return cloneLocation(this.points.get(0).getLocation());
      } else {
         int clamped = Math.max(0, Math.min(this.totalDuration - 1, frame));
         int leg = 0;

         for (int i = this.legStartFrames.length - 1; i >= 0; i--) {
            if (clamped >= this.legStartFrames[i]) {
               leg = i;
               break;
            }
         }

         double localT = (double)(clamped - this.legStartFrames[leg]) / this.legDurations[leg];
         if (localT < 0.0) {
            localT = 0.0;
         }

         if (localT > 1.0) {
            localT = 1.0;
         }

         Easing legEasing = this.points.get(leg).getEasingToNext();
         if (legEasing == Easing.STEP) {
            return cloneLocation(this.points.get(leg).getLocation());
         } else if (legEasing == null && this.defaultEasing == Easing.CATMULL_ROM) {
            return this.sampleCatmullRom(leg, localT);
         } else {
            Easing easing = legEasing != null ? legEasing : this.defaultEasing;
            if (easing == Easing.CATMULL_ROM) {
               easing = Easing.LINEAR;
            }

            double u = easing.apply(localT);
            return lerpPoints(this.points.get(leg).getLocation(), this.points.get(leg + 1).getLocation(), u);
         }
      }
   }

   private AbstractLocation sampleCatmullRom(int leg, double t) {
      int n = this.points.size();
      AbstractLocation p0 = this.points.get(Math.max(0, leg - 1)).getLocation();
      AbstractLocation p1 = this.points.get(leg).getLocation();
      AbstractLocation p2 = this.points.get(Math.min(n - 1, leg + 1)).getLocation();
      AbstractLocation p3 = this.points.get(Math.min(n - 1, leg + 2)).getLocation();
      double x = catmull(p0.getX(), p1.getX(), p2.getX(), p3.getX(), t);
      double y = catmull(p0.getY(), p1.getY(), p2.getY(), p3.getY(), t);
      double z = catmull(p0.getZ(), p1.getZ(), p2.getZ(), p3.getZ(), t);
      float yaw = lerpYaw(p1.getYaw(), p2.getYaw(), (float)t);
      float pitch = (float)catmull(p0.getPitch(), p1.getPitch(), p2.getPitch(), p3.getPitch(), t);
      return new AbstractLocation(p1.getWorld(), x, y, z, yaw, pitch);
   }

   private static double catmull(double p0, double p1, double p2, double p3, double t) {
      double t2 = t * t;
      double t3 = t2 * t;
      return 0.5 * (2.0 * p1 + (-p0 + p2) * t + (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3) * t2 + (-p0 + 3.0 * p1 - 3.0 * p2 + p3) * t3);
   }

   private static AbstractLocation lerpPoints(AbstractLocation a, AbstractLocation b, double t) {
      double x = a.getX() + (b.getX() - a.getX()) * t;
      double y = a.getY() + (b.getY() - a.getY()) * t;
      double z = a.getZ() + (b.getZ() - a.getZ()) * t;
      float yaw = lerpYaw(a.getYaw(), b.getYaw(), (float)t);
      float pitch = (float)(a.getPitch() + (b.getPitch() - a.getPitch()) * t);
      return new AbstractLocation(a.getWorld(), x, y, z, yaw, pitch);
   }

   private static float lerpYaw(float a, float b, float t) {
      float diff = ((b - a) % 360.0F + 540.0F) % 360.0F - 180.0F;
      return a + diff * t;
   }

   private static AbstractLocation cloneLocation(AbstractLocation l) {
      return new AbstractLocation(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
   }
}
