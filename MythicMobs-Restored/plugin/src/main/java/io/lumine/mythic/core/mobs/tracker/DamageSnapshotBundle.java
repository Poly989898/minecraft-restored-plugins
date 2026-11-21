package io.lumine.mythic.core.mobs.tracker;

import java.util.LinkedList;
import java.util.UUID;
import lombok.Getter;

public class DamageSnapshotBundle {
   private UUID damager;
   @Getter
   private LinkedList<DamageSnapshot> damageSnapshots = new LinkedList<>();
   @Getter
   private double totalDamage = 0.0;

   public DamageSnapshotBundle(UUID damager) {
      this.damager = damager;
   }

   public void addSnapshot(DamageSnapshot snapshot) {
      this.totalDamage = this.totalDamage + snapshot.getDamageAmount();
      this.damageSnapshots.add(snapshot);
   }

   @Override
   public String toString() {
      return "DamageSnapshotBundle{damager=" + this.damager + "totalDamage=" + this.totalDamage + "}";
   }
}
