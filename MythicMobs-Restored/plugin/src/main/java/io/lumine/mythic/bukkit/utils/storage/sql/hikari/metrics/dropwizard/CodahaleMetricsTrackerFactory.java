package io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.dropwizard;

import com.codahale.metrics.MetricRegistry;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.MetricsTracker;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.MetricsTrackerFactory;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.PoolStats;

public final class CodahaleMetricsTrackerFactory implements MetricsTrackerFactory {
   private final MetricRegistry registry;

   public CodahaleMetricsTrackerFactory(MetricRegistry registry) {
      this.registry = registry;
   }

   public MetricRegistry getRegistry() {
      return this.registry;
   }

   @Override
   public MetricsTracker create(String poolName, PoolStats poolStats) {
      return new CodaHaleMetricsTracker(poolName, poolStats, this.registry);
   }
}
