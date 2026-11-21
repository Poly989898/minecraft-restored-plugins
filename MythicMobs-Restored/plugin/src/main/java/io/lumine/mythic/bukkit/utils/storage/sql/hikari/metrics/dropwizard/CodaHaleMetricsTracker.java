package io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.dropwizard;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.MetricsTracker;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.PoolStats;
import java.util.concurrent.TimeUnit;

public final class CodaHaleMetricsTracker extends MetricsTracker {
   private final String poolName;
   private final Timer connectionObtainTimer;
   private final Histogram connectionUsage;
   private final Meter connectionTimeoutMeter;
   private final MetricRegistry registry;

   public CodaHaleMetricsTracker(String poolName, final PoolStats poolStats, MetricRegistry registry) {
      this.poolName = poolName;
      this.registry = registry;
      this.connectionObtainTimer = registry.timer(MetricRegistry.name(poolName, new String[]{"pool", "Wait"}));
      this.connectionUsage = registry.histogram(MetricRegistry.name(poolName, new String[]{"pool", "Usage"}));
      this.connectionTimeoutMeter = registry.meter(MetricRegistry.name(poolName, new String[]{"pool", "ConnectionTimeoutRate"}));
      registry.register(MetricRegistry.name(poolName, new String[]{"pool", "TotalConnections"}), new Gauge<Integer>() {
         public Integer getValue() {
            return poolStats.getTotalConnections();
         }
      });
      registry.register(MetricRegistry.name(poolName, new String[]{"pool", "IdleConnections"}), new Gauge<Integer>() {
         public Integer getValue() {
            return poolStats.getIdleConnections();
         }
      });
      registry.register(MetricRegistry.name(poolName, new String[]{"pool", "ActiveConnections"}), new Gauge<Integer>() {
         public Integer getValue() {
            return poolStats.getActiveConnections();
         }
      });
      registry.register(MetricRegistry.name(poolName, new String[]{"pool", "PendingConnections"}), new Gauge<Integer>() {
         public Integer getValue() {
            return poolStats.getPendingThreads();
         }
      });
   }

   @Override
   public void close() {
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "Wait"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "Usage"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "TotalConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "IdleConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ActiveConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "PendingConnections"}));
   }

   @Override
   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
      this.connectionObtainTimer.update(elapsedAcquiredNanos, TimeUnit.NANOSECONDS);
   }

   @Override
   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
      this.connectionUsage.update(elapsedBorrowedMillis);
   }

   @Override
   public void recordConnectionTimeout() {
      this.connectionTimeoutMeter.mark();
   }

   public Timer getConnectionAcquisitionTimer() {
      return this.connectionObtainTimer;
   }

   public Histogram getConnectionDurationHistogram() {
      return this.connectionUsage;
   }
}
