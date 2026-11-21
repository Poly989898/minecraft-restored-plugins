package io.lumine.mythic.core.mobs.tracker;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.Getter;

public class DamageRecord {
   private final ActiveMob mob;
   private double totalDamageDone = 0.0;
   @Getter
   private final Map<UUID, DamageSnapshotBundle> damagingPlayers = new HashMap<>();

   public DamageRecord(ActiveMob mob) {
      this.mob = mob;
   }

   public Collection<UUID> getDamagers() {
      return this.damagingPlayers.keySet();
   }

   public void registerSnapshot(DamageSnapshot snapshot) {
      UUID damager = snapshot.getDamagingEntity();
      this.damagingPlayers.computeIfAbsent(damager, k -> new DamageSnapshotBundle(k)).addSnapshot(snapshot);
      this.totalDamageDone = this.totalDamageDone + snapshot.getDamageAmount();
   }

   public UUID getTopDamager() {
      UUID topDamager = null;
      double highestDamage = 0.0;

      for (Entry<UUID, DamageSnapshotBundle> entry : this.damagingPlayers.entrySet()) {
         double totalDamage = entry.getValue().getTotalDamage();
         if (totalDamage > highestDamage) {
            highestDamage = totalDamage;
            topDamager = entry.getKey();
         }
      }

      return topDamager;
   }

   public UUID getDamagerInPlace(int position) {
      List<Entry<UUID, DamageSnapshotBundle>> sortedEntries = new ArrayList<>(this.damagingPlayers.entrySet());
      sortedEntries.sort((entry1, entry2) -> Double.compare(entry2.getValue().getTotalDamage(), entry1.getValue().getTotalDamage()));
      int zeroBasedIndex = position - 1;
      return zeroBasedIndex >= 0 && zeroBasedIndex < sortedEntries.size() ? sortedEntries.get(zeroBasedIndex).getKey() : null;
   }

   public double getDamageInPlace(int position) {
      List<Entry<UUID, DamageSnapshotBundle>> sortedEntries = new ArrayList<>(this.damagingPlayers.entrySet());
      sortedEntries.sort((entry1, entry2) -> Double.compare(entry2.getValue().getTotalDamage(), entry1.getValue().getTotalDamage()));
      int zeroBasedIndex = position - 1;
      return zeroBasedIndex >= 0 && zeroBasedIndex < sortedEntries.size() ? sortedEntries.get(zeroBasedIndex).getValue().getTotalDamage() : 0.0;
   }

   public int getPlace(UUID entity) {
      List<Entry<UUID, DamageSnapshotBundle>> sortedEntries = new ArrayList<>(this.damagingPlayers.entrySet());
      sortedEntries.sort((entry1, entry2) -> Double.compare(entry2.getValue().getTotalDamage(), entry1.getValue().getTotalDamage()));

      for (int i = 0; i < sortedEntries.size(); i++) {
         if (sortedEntries.get(i).getKey().equals(entity)) {
            return i + 1;
         }
      }

      return -1;
   }

   public boolean didEnoughDamage(UUID entity, double minAmount) {
      DamageSnapshotBundle bundle = this.damagingPlayers.get(entity);
      return bundle != null && bundle.getTotalDamage() >= minAmount;
   }

   public double getDamageDonePercent(AbstractPlayer player) {
      DamageSnapshotBundle bundle = this.damagingPlayers.get(player.getUniqueId());
      return bundle == null ? 0.0 : bundle.getTotalDamage() / this.totalDamageDone;
   }

   @Override
   public String toString() {
      return "DamageRecord{linkedEntity=" + this.mob.getUniqueId() + ", damagingPlayers=" + this.damagingPlayers + "}";
   }
}
