package io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics;

public class MetricsTracker implements AutoCloseable {
   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
   }

   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
   }

   public void recordConnectionTimeout() {
   }

   @Override
   public void close() {
   }
}
