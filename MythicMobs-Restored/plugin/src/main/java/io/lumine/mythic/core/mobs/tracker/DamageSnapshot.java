package io.lumine.mythic.core.mobs.tracker;

import java.util.UUID;
import lombok.Getter;

@Getter
public class DamageSnapshot {
   private final UUID damagedEntity;
   private final UUID damagingEntity;
   private final double damageAmount;
   private final long timestamp;

   public static DamageSnapshot create(UUID damaged, UUID damaging, double damage) {
      return new DamageSnapshot(damaged, damaging, damage);
   }

   private DamageSnapshot(UUID damaged, UUID damaging, double damage) {
      this.damagedEntity = damaged;
      this.damagingEntity = damaging;
      this.damageAmount = damage;
      this.timestamp = System.currentTimeMillis();
   }
}
