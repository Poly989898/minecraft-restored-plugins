package io.lumine.mythic.lib.player.cooldown;

import org.apache.commons.lang.Validate;

public class CooldownInfo {
   private final long initialCooldown;
   private final long castTime = System.currentTimeMillis();
   private long nextUse;

   public CooldownInfo(double initialCooldown) {
      this.initialCooldown = (long)(initialCooldown * 1000.0);
      this.nextUse = this.castTime + this.initialCooldown;
   }

   public long getCastTime() {
      return this.castTime;
   }

   public long getInitialCooldown() {
      return this.initialCooldown;
   }

   public long getNextUse() {
      return this.nextUse;
   }

   public long getRemaining() {
      return Math.max(0L, this.nextUse - System.currentTimeMillis());
   }

   public boolean hasEnded() {
      return System.currentTimeMillis() > this.nextUse;
   }

   public void reduceRemainingCooldown(double p) {
      Validate.isTrue(p >= 0.0 && p <= 1.0, "p must be between 0 and 1");
      double left = this.getRemaining();
      this.nextUse -= (long)(left * p);
   }

   public void reduceInitialCooldown(double p) {
      Validate.isTrue(p >= 0.0 && p <= 1.0, "p must be between 0 and 1");
      this.nextUse = (long)(this.nextUse - this.initialCooldown * p);
   }

   public void reduceFlat(double t) {
      this.nextUse = (long)(this.nextUse - 1000.0 * t);
   }
}
