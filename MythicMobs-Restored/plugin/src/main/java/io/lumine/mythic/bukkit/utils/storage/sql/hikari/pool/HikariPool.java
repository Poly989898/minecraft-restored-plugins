package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.HikariConfig;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.HikariPoolMXBean;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.MetricsTrackerFactory;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.PoolStats;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.dropwizard.CodahaleHealthChecker;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.ClockSource;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.ConcurrentBag;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.SuspendResumeLock;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.UtilityElf;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariPool extends PoolBase implements HikariPoolMXBean, ConcurrentBag.IBagStateListener {
   private final Logger LOGGER = LoggerFactory.getLogger(HikariPool.class);
   private static final ClockSource clockSource = ClockSource.INSTANCE;
   private static final int POOL_NORMAL = 0;
   private static final int POOL_SUSPENDED = 1;
   private static final int POOL_SHUTDOWN = 2;
   private volatile int poolState;
   private final long ALIVE_BYPASS_WINDOW_MS = Long.getLong(
      "io.lumine.mythic.bukkit.utils.storage.sql.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L)
   );
   private final long HOUSEKEEPING_PERIOD_MS = Long.getLong(
      "io.lumine.mythic.bukkit.utils.storage.sql.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L)
   );
   private final HikariPool.PoolEntryCreator POOL_ENTRY_CREATOR = new HikariPool.PoolEntryCreator();
   private final AtomicInteger totalConnections;
   private final ThreadPoolExecutor addConnectionExecutor;
   private final ThreadPoolExecutor closeConnectionExecutor;
   private final ScheduledThreadPoolExecutor houseKeepingExecutorService;
   private final ConcurrentBag<PoolEntry> connectionBag = new ConcurrentBag<>(this);
   private final ProxyLeakTask leakTask;
   private final SuspendResumeLock suspendResumeLock;
   private PoolBase.MetricsTrackerDelegate metricsTracker;

   public HikariPool(HikariConfig config) {
      super(config);
      this.totalConnections = new AtomicInteger();
      this.suspendResumeLock = config.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK;
      if (config.getMetricsTrackerFactory() != null) {
         this.setMetricsTrackerFactory(config.getMetricsTrackerFactory());
      } else {
         this.setMetricRegistry(config.getMetricRegistry());
      }

      this.setHealthCheckRegistry(config.getHealthCheckRegistry());
      this.registerMBeans(this);
      this.checkFailFast();
      ThreadFactory threadFactory = config.getThreadFactory();
      this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor(
         config.getMaximumPoolSize(), this.poolName + " connection adder", threadFactory, new DiscardPolicy()
      );
      this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(
         config.getMaximumPoolSize(), this.poolName + " connection closer", threadFactory, new CallerRunsPolicy()
      );
      if (config.getScheduledExecutorService() == null) {
         threadFactory = (ThreadFactory)(threadFactory != null ? threadFactory : new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper", true));
         this.houseKeepingExecutorService = new ScheduledThreadPoolExecutor(1, threadFactory, new DiscardPolicy());
         this.houseKeepingExecutorService.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
         this.houseKeepingExecutorService.setRemoveOnCancelPolicy(true);
      } else {
         this.houseKeepingExecutorService = config.getScheduledExecutorService();
      }

      this.leakTask = new ProxyLeakTask(config.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
      this.houseKeepingExecutorService.scheduleWithFixedDelay(new HikariPool.HouseKeeper(), 100L, this.HOUSEKEEPING_PERIOD_MS, TimeUnit.MILLISECONDS);
   }

   public final Connection getConnection() throws SQLException {
      return this.getConnection(this.connectionTimeout);
   }

   public final Connection getConnection(long hardTimeout) throws SQLException {
      this.suspendResumeLock.acquire();
      long startTime = clockSource.currentTime();

      try {
         long timeout = hardTimeout;

         do {
            PoolEntry poolEntry = this.connectionBag.borrow(timeout, TimeUnit.MILLISECONDS);
            if (poolEntry == null) {
               break;
            }

            long now = clockSource.currentTime();
            if (!poolEntry.isMarkedEvicted()
               && (clockSource.elapsedMillis(poolEntry.lastAccessed, now) <= this.ALIVE_BYPASS_WINDOW_MS || this.isConnectionAlive(poolEntry.connection))) {
               this.metricsTracker.recordBorrowStats(poolEntry, startTime);
               return poolEntry.createProxyConnection(this.leakTask.schedule(poolEntry), now);
            }

            this.closeConnection(poolEntry, "(connection is evicted or dead)");
            timeout = hardTimeout - clockSource.elapsedMillis(startTime);
         } while (timeout > 0L);
      } catch (InterruptedException e) {
         throw new SQLException(this.poolName + " - Interrupted during connection acquisition", e);
      } finally {
         this.suspendResumeLock.release();
      }

      this.logPoolState("Timeout failure ");
      this.metricsTracker.recordConnectionTimeout();
      String str = null;
      Throwable originalException = this.getLastConnectionFailure();
      if (originalException instanceof SQLException) {
         str = ((SQLException)originalException).getSQLState();
      }

      SQLException connectionException = new SQLTransientConnectionException(
         this.poolName + " - Connection is not available, request timed out after " + clockSource.elapsedMillis(startTime) + "ms.", str, originalException
      );
      if (originalException instanceof SQLException) {
         connectionException.setNextException((SQLException)originalException);
      }

      throw connectionException;
   }

   public final synchronized void shutdown() throws InterruptedException {
      try {
         this.poolState = 2;
         this.LOGGER.info("{} - Close initiated...", this.poolName);
         this.logPoolState("Before closing ");
         this.softEvictConnections();
         if (this.addConnectionExecutor != null) {
            this.addConnectionExecutor.shutdown();
            this.addConnectionExecutor.awaitTermination(5L, TimeUnit.SECONDS);
         }

         if (this.config.getScheduledExecutorService() == null && this.houseKeepingExecutorService != null) {
            this.houseKeepingExecutorService.shutdown();
            this.houseKeepingExecutorService.awaitTermination(5L, TimeUnit.SECONDS);
         }

         this.connectionBag.close();
         ExecutorService assassinExecutor = UtilityElf.createThreadPoolExecutor(
            this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config.getThreadFactory(), new CallerRunsPolicy()
         );

         try {
            long start = clockSource.currentTime();

            do {
               this.abortActiveConnections(assassinExecutor);
               this.softEvictConnections();
            } while (this.getTotalConnections() > 0 && clockSource.elapsedMillis(start) < TimeUnit.SECONDS.toMillis(5L));
         } finally {
            assassinExecutor.shutdown();
            assassinExecutor.awaitTermination(5L, TimeUnit.SECONDS);
         }

         this.shutdownNetworkTimeoutExecutor();
         if (this.closeConnectionExecutor != null) {
            this.closeConnectionExecutor.shutdown();
            this.closeConnectionExecutor.awaitTermination(5L, TimeUnit.SECONDS);
         }
      } finally {
         this.logPoolState("After closing ");
         this.unregisterMBeans();
         this.metricsTracker.close();
         this.LOGGER.info("{} - Closed.", this.poolName);
      }
   }

   public final void evictConnection(Connection connection) {
      ProxyConnection proxyConnection = (ProxyConnection)connection;
      proxyConnection.cancelLeakTask();

      try {
         this.softEvictConnection(proxyConnection.getPoolEntry(), "(connection evicted by user)", !connection.isClosed());
      } catch (SQLException e) {
      }
   }

   public void setMetricRegistry(Object metricRegistry) {
      if (metricRegistry != null) {
         this.setMetricsTrackerFactory(new CodahaleMetricsTrackerFactory((MetricRegistry)metricRegistry));
      } else {
         this.setMetricsTrackerFactory(null);
      }
   }

   public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
      if (metricsTrackerFactory != null) {
         this.metricsTracker = new PoolBase.MetricsTrackerDelegate(metricsTrackerFactory.create(this.config.getPoolName(), this.getPoolStats()));
      } else {
         this.metricsTracker = new PoolBase.NopMetricsTrackerDelegate();
      }
   }

   public void setHealthCheckRegistry(Object healthCheckRegistry) {
      if (healthCheckRegistry != null) {
         CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)healthCheckRegistry);
      }
   }

   @Override
   public Future<Boolean> addBagItem() {
      return this.addConnectionExecutor.submit(this.POOL_ENTRY_CREATOR);
   }

   @Override
   public final int getActiveConnections() {
      return this.connectionBag.getCount(1);
   }

   @Override
   public final int getIdleConnections() {
      return this.connectionBag.getCount(0);
   }

   @Override
   public final int getTotalConnections() {
      return this.connectionBag.size() - this.connectionBag.getCount(-1);
   }

   @Override
   public final int getThreadsAwaitingConnection() {
      return this.connectionBag.getPendingQueue();
   }

   @Override
   public void softEvictConnections() {
      for (PoolEntry poolEntry : this.connectionBag.values()) {
         this.softEvictConnection(poolEntry, "(connection evicted)", false);
      }
   }

   @Override
   public final synchronized void suspendPool() {
      if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK) {
         throw new IllegalStateException(this.poolName + " - is not suspendable");
      } else {
         if (this.poolState != 1) {
            this.suspendResumeLock.suspend();
            this.poolState = 1;
         }
      }
   }

   @Override
   public final synchronized void resumePool() {
      if (this.poolState == 1) {
         this.poolState = 0;
         this.fillPool();
         this.suspendResumeLock.resume();
      }
   }

   final void logPoolState(String... prefix) {
      if (this.LOGGER.isDebugEnabled()) {
         this.LOGGER
            .debug(
               "{} - {}stats (total={}, active={}, idle={}, waiting={})",
               this.poolName,
               prefix.length > 0 ? prefix[0] : "",
               this.getTotalConnections(),
               this.getActiveConnections(),
               this.getIdleConnections(),
               this.getThreadsAwaitingConnection()
            );
      }
   }

   @Override
   final void releaseConnection(PoolEntry poolEntry) {
      this.metricsTracker.recordConnectionUsage(poolEntry);
      this.connectionBag.requite(poolEntry);
   }

   final void closeConnection(PoolEntry poolEntry, final String closureReason) {
      if (this.connectionBag.remove(poolEntry)) {
         int tc = this.totalConnections.decrementAndGet();
         if (tc < 0) {
            this.LOGGER.warn("{} - Unexpected value of totalConnections={}", this.poolName, tc, new Exception());
         }

         final Connection connection = poolEntry.close();
         this.closeConnectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
               HikariPool.this.quietlyCloseConnection(connection, closureReason);
            }
         });
      }
   }

   private PoolEntry createPoolEntry() {
      try {
         final PoolEntry poolEntry = this.newPoolEntry();
         long maxLifetime = this.config.getMaxLifetime();
         if (maxLifetime > 0L) {
            long variance = maxLifetime > 10000L ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L;
            long lifetime = maxLifetime - variance;
            poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(new Runnable() {
               @Override
               public void run() {
                  HikariPool.this.softEvictConnection(poolEntry, "(connection has passed maxLifetime)", false);
               }
            }, lifetime, TimeUnit.MILLISECONDS));
         }

         this.LOGGER.debug("{} - Added connection {}", this.poolName, poolEntry.connection);
         return poolEntry;
      } catch (Exception e) {
         if (this.poolState == 0) {
            this.LOGGER.debug("{} - Cannot acquire connection from data source", this.poolName, e);
         }

         return null;
      }
   }

   private void fillPool() {
      int connectionsToAdd = Math.min(this.config.getMaximumPoolSize() - this.totalConnections.get(), this.config.getMinimumIdle() - this.getIdleConnections())
         - this.addConnectionExecutor.getQueue().size();

      for (int i = 0; i < connectionsToAdd; i++) {
         this.addBagItem();
      }

      if (connectionsToAdd > 0 && this.LOGGER.isDebugEnabled()) {
         this.addConnectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
               HikariPool.this.logPoolState("After adding ");
            }
         });
      }
   }

   private void abortActiveConnections(ExecutorService assassinExecutor) {
      for (PoolEntry poolEntry : this.connectionBag.values(1)) {
         try {
            poolEntry.connection.abort(assassinExecutor);
         } catch (Throwable e) {
            this.quietlyCloseConnection(poolEntry.connection, "(connection aborted during shutdown)");
         } finally {
            poolEntry.close();
            if (this.connectionBag.remove(poolEntry)) {
               this.totalConnections.decrementAndGet();
            }
         }
      }
   }

   private void checkFailFast() {
      if (this.config.isInitializationFailFast()) {
         try {
            this.newConnection().close();
         } catch (Throwable e) {
            try {
               this.shutdown();
            } catch (Throwable ex) {
               e.addSuppressed(ex);
            }

            throw new HikariPool.PoolInitializationException(e);
         }
      }
   }

   private void softEvictConnection(PoolEntry poolEntry, String reason, boolean owner) {
      if (!owner && !this.connectionBag.reserve(poolEntry)) {
         poolEntry.markEvicted();
      } else {
         poolEntry.markEvicted();
         this.closeConnection(poolEntry, reason);
      }
   }

   private PoolStats getPoolStats() {
      return new PoolStats(TimeUnit.SECONDS.toMillis(1L)) {
         @Override
         protected void update() {
            this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
            this.idleConnections = HikariPool.this.getIdleConnections();
            this.totalConnections = HikariPool.this.getTotalConnections();
            this.activeConnections = HikariPool.this.getActiveConnections();
         }
      };
   }

   private class HouseKeeper implements Runnable {
      private volatile long previous = HikariPool.clockSource.plusMillis(HikariPool.clockSource.currentTime(), -HikariPool.this.HOUSEKEEPING_PERIOD_MS);

      private HouseKeeper() {
      }

      @Override
      public void run() {
         try {
            HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
            HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
            HikariPool.this.leakTask.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
            long idleTimeout = HikariPool.this.config.getIdleTimeout();
            long now = HikariPool.clockSource.currentTime();
            if (HikariPool.clockSource.plusMillis(now, 128L) < HikariPool.clockSource.plusMillis(this.previous, HikariPool.this.HOUSEKEEPING_PERIOD_MS)) {
               HikariPool.this.LOGGER
                  .warn(
                     "{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.",
                     HikariPool.clockSource.elapsedDisplayString(this.previous, now),
                     HikariPool.this.poolName
                  );
               this.previous = now;
               HikariPool.this.softEvictConnections();
               HikariPool.this.fillPool();
               return;
            }

            if (now > HikariPool.clockSource.plusMillis(this.previous, 3L * HikariPool.this.HOUSEKEEPING_PERIOD_MS / 2L)) {
               HikariPool.this.LOGGER
                  .warn(
                     "{} - Thread starvation or clock leap detected (housekeeper delta={}).",
                     HikariPool.clockSource.elapsedDisplayString(this.previous, now),
                     HikariPool.this.poolName
                  );
            }

            this.previous = now;
            String afterPrefix = "Pool ";
            if (idleTimeout > 0L) {
               List<PoolEntry> idleList = HikariPool.this.connectionBag.values(0);
               int removable = idleList.size() - HikariPool.this.config.getMinimumIdle();
               if (removable > 0) {
                  HikariPool.this.logPoolState("Before cleanup ");
                  afterPrefix = "After cleanup  ";
                  Collections.sort(idleList, PoolEntry.LASTACCESS_COMPARABLE);

                  for (PoolEntry poolEntry : idleList) {
                     if (HikariPool.clockSource.elapsedMillis(poolEntry.lastAccessed, now) > idleTimeout && HikariPool.this.connectionBag.reserve(poolEntry)) {
                        HikariPool.this.closeConnection(poolEntry, "(connection has passed idleTimeout)");
                        if (--removable == 0) {
                           break;
                        }
                     }
                  }
               }
            }

            HikariPool.this.logPoolState(afterPrefix);
            HikariPool.this.fillPool();
         } catch (Exception e) {
            HikariPool.this.LOGGER.error("Unexpected exception in housekeeping task", (Throwable)e);
         }
      }
   }

   private class PoolEntryCreator implements Callable<Boolean> {
      private PoolEntryCreator() {
      }

      public Boolean call() throws Exception {
         long sleepBackoff = 250L;

         while (HikariPool.this.poolState == 0 && HikariPool.this.totalConnections.get() < HikariPool.this.config.getMaximumPoolSize()) {
            PoolEntry poolEntry = HikariPool.this.createPoolEntry();
            if (poolEntry != null) {
               HikariPool.this.totalConnections.incrementAndGet();
               HikariPool.this.connectionBag.add(poolEntry);
               return Boolean.TRUE;
            }

            UtilityElf.quietlySleep(sleepBackoff);
            sleepBackoff = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(HikariPool.this.connectionTimeout, (long)(sleepBackoff * 1.5)));
         }

         return Boolean.FALSE;
      }
   }

   public static class PoolInitializationException extends RuntimeException {
      private static final long serialVersionUID = 929872118275916520L;

      public PoolInitializationException(Throwable t) {
         super("Failed to initialize pool: " + t.getMessage(), t);
      }
   }
}
