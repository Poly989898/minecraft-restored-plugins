package me.TechsCode.UltraPermissions.base.expire;

public class ExpireTime {
   private final ExpireTimeType type;
   private final long time;

   private ExpireTime(ExpireTimeType expireTimeType, long value) {
      this.type = expireTimeType;
      this.time = value;
   }

   public static ExpireTime of(ExpireTimeType expireTimeType, long value) {
      return new ExpireTime(expireTimeType, value);
   }

   public static ExpireTime release() {
      return new ExpireTime(ExpireTimeType.RELEASE, -1L);
   }

   public ExpireTimeType getType() {
      return this.type;
   }

   public long getTime() {
      return this.time;
   }

   public boolean isRelease() {
      return this.getType() == ExpireTimeType.RELEASE;
   }
}
