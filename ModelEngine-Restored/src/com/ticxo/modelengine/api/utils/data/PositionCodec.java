package com.ticxo.modelengine.api.utils.data;

public class PositionCodec {
   public static final int RESYNC_INTERVAL = 400;
   private long baseX;
   private long baseY;
   private long baseZ;
   private boolean initialized;
   private int movesSinceAbsolute;
   private short deltaX;
   private short deltaY;
   private short deltaZ;

   private static long encode(double value) {
      return Math.round(value * 4096.0);
   }

   public PositionCodec.Result update(double x, double y, double z) {
      long nx = encode(x);
      long ny = encode(y);
      long nz = encode(z);
      if (!this.initialized) {
         this.initialized = true;
         this.baseX = nx;
         this.baseY = ny;
         this.baseZ = nz;
         this.movesSinceAbsolute = 0;
         return PositionCodec.Result.ABSOLUTE;
      } else {
         long dx = nx - this.baseX;
         long dy = ny - this.baseY;
         long dz = nz - this.baseZ;
         if (dx == 0L && dy == 0L && dz == 0L) {
            return PositionCodec.Result.UNCHANGED;
         } else {
            this.baseX = nx;
            this.baseY = ny;
            this.baseZ = nz;
            if (dx == (short)dx && dy == (short)dy && dz == (short)dz && ++this.movesSinceAbsolute < 400) {
               this.deltaX = (short)dx;
               this.deltaY = (short)dy;
               this.deltaZ = (short)dz;
               return PositionCodec.Result.MOVE;
            } else {
               this.movesSinceAbsolute = 0;
               return PositionCodec.Result.ABSOLUTE;
            }
         }
      }
   }

   public short getDeltaX() {
      return this.deltaX;
   }

   public short getDeltaY() {
      return this.deltaY;
   }

   public short getDeltaZ() {
      return this.deltaZ;
   }

   public enum Result {
      UNCHANGED,
      MOVE,
      ABSOLUTE;
   }
}
