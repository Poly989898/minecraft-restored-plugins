package litebans;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import javax.sql.DataSource;

public final class HikariPool extends PoolBase implements HikariPoolMXBean, IBagStateListener {
   public static final int POOL_NORMAL = 0;
   public static final int POOL_SUSPENDED = 1;
   public static final int POOL_SHUTDOWN = 2;
   private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
   private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
   private final PoolLogger LOGGER = PoolLogger.computePoolLogger(HikariPool.class);
   private final long ALIVE_BYPASS_WINDOW_MS = Long.getLong("com.zaxxer.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L));
   private final long HOUSEKEEPING_PERIOD_MS = Long.getLong("com.zaxxer.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L));
   private final PoolEntryCreator poolEntryCreator = new PoolEntryCreator(this, null);
   private final PoolEntryCreator postFillPoolEntryCreator = new PoolEntryCreator(this, "After adding ");
   private final Collection addConnectionQueueReadOnlyView;
   private final ThreadPoolExecutor addConnectionExecutor;
   private final ThreadPoolExecutor closeConnectionExecutor;
   private final ConcurrentBag connectionBag = new ConcurrentBag(this);
   private final LeakTaskFactory leakTaskFactory;
   private final SuspendResumeLock suspendResumeLock;
   private final ScheduledExecutorService houseKeepingExecutorService;
   public volatile int poolState;
   private ScheduledFuture houseKeeperTask;

   public HikariPool(HikariConfig hikariConfig) {
      super(hikariConfig);
      this.suspendResumeLock = hikariConfig.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.SUSPEND_RESUME_LOCK;
      this.houseKeepingExecutorService = this.initializeHouseKeepingExecutorService();
      this.checkFailFast();
      this.handleMBeans(this, true);
      ThreadFactory threadFactory = hikariConfig.getThreadFactory();
      int maximumPoolSize = hikariConfig.getMaximumPoolSize();
      LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(maximumPoolSize);
      this.addConnectionQueueReadOnlyView = Collections.unmodifiableCollection(linkedBlockingQueue);
      this.addConnectionExecutor = TransactionIsolation.computeThreadPoolExecutor(linkedBlockingQueue, this.poolName + " innocent connection adder (Async)", threadFactory, new DiscardOldestPolicy());
      this.closeConnectionExecutor = TransactionIsolation.computeThreadPoolExecutor(maximumPoolSize, this.poolName + " innocent connection closer (Async)", threadFactory, new CallerRunsPolicy());
      this.leakTaskFactory = new LeakTaskFactory(hikariConfig.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
      this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(this, null), 100L, this.HOUSEKEEPING_PERIOD_MS, TimeUnit.MILLISECONDS);
      if (Boolean.getBoolean("com.zaxxer.hikari.blockUntilFilled") && hikariConfig.getInitializationFailTimeout() > 1L) {
         this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
         this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
         long currentTime2 = ClockSource.currentTime();

         while (ClockSource.elapsedMillis(currentTime2) < hikariConfig.getInitializationFailTimeout() && this.getTotalConnections() < hikariConfig.getMinimumIdle()) {
            TransactionIsolation.handleLongValue(TimeUnit.MILLISECONDS.toMillis(100L));
         }

         this.addConnectionExecutor.setCorePoolSize(1);
         this.addConnectionExecutor.setMaximumPoolSize(1);
      }
   }

   public Connection getConnection() throws SQLException {
      return this.getConnection(this.connectionTimeout);
   }

   public Connection getConnection(long value) throws SQLException {
      this.suspendResumeLock.perform();
      long currentTime2 = ClockSource.currentTime();

      try {
         long value2 = value;

         while (true) {
            PoolEntry borrow2 = (PoolEntry)this.connectionBag.borrow(value2, TimeUnit.MILLISECONDS);
            if (borrow2 != null) {
               long currentTime3 = ClockSource.currentTime();
               if (!borrow2.isMarkedEvicted() && (ClockSource.elapsedMillis(borrow2.longValue, currentTime3) <= this.ALIVE_BYPASS_WINDOW_MS || this.isConnectionAlive(borrow2.connection2))) {
                  return borrow2.createProxyConnection(this.leakTaskFactory.computeLeakTask(borrow2), currentTime3);
               }

               this.closeConnection(borrow2, borrow2.isMarkedEvicted() ? "(connection was evicted)" : "(connection is dead)");
               value2 = value - ClockSource.elapsedMillis(currentTime2);
               if (value2 > 0L) {
                  continue;
               }
            }

            throw this.createTimeoutException(currentTime2);
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new SQLException(this.poolName + " - Interrupted during connection acquisition", e);
      } finally {
         this.suspendResumeLock.perform2();
      }
   }

   public synchronized void shutdown() throws InterruptedException {
      try {
         this.poolState = 2;
         if (this.addConnectionExecutor != null) {
            this.logPoolState(new String[]{"Before shutdown "});
            if (this.houseKeeperTask != null) {
               this.houseKeeperTask.cancel(false);
               this.houseKeeperTask = null;
            }

            this.softEvictConnections();
            this.addConnectionExecutor.shutdown();
            this.addConnectionExecutor.awaitTermination(this.getLoginTimeout(), TimeUnit.SECONDS);
            this.perform();
            this.connectionBag.close();
            ThreadPoolExecutor threadPoolExecutor = TransactionIsolation.computeThreadPoolExecutor(this.hikariConfig.getMaximumPoolSize(), this.poolName + " connection assassinator", this.hikariConfig.getThreadFactory(), new CallerRunsPolicy());

            try {
               long currentTime2 = ClockSource.currentTime();

               do {
                  this.abortActiveConnections(threadPoolExecutor);
                  this.softEvictConnections();
               } while (this.getTotalConnections() > 0 && ClockSource.elapsedMillis(currentTime2) < TimeUnit.SECONDS.toMillis(10L));
            } finally {
               threadPoolExecutor.shutdown();
               threadPoolExecutor.awaitTermination(10L, TimeUnit.SECONDS);
            }

            this.shutdownNetworkTimeoutExecutor();
            this.closeConnectionExecutor.shutdown();
            this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS);
            return;
         }
      } finally {
         this.logPoolState(new String[]{"After shutdown "});
         this.handleMBeans(this, false);
      }
   }

   @Override
   public void addBagItem(int value) {
      boolean flag = value - this.addConnectionQueueReadOnlyView.size() >= 0;
      if (flag) {
         this.addConnectionExecutor.submit(this.poolEntryCreator);
      } else {
         this.LOGGER.handleText("%s - Add connection elided, waiting %d, queue %d", new Object[]{this.poolName, value, this.addConnectionQueueReadOnlyView.size()});
      }
   }

   @Override
   public int getActiveConnections() {
      return this.connectionBag.getCount(1);
   }

   @Override
   public int getIdleConnections() {
      return this.connectionBag.getCount(0);
   }

   @Override
   public int getTotalConnections() {
      return this.connectionBag.size();
   }

   @Override
   public int getThreadsAwaitingConnection() {
      return this.connectionBag.getWaitingThreadCount();
   }

   @Override
   public void softEvictConnections() {
      this.connectionBag.values().forEach((obj) -> this.evictConnection((PoolEntry) obj));
   }

   @Override
   public synchronized void suspendPool() {
      if (this.suspendResumeLock == SuspendResumeLock.SUSPEND_RESUME_LOCK) {
         throw new IllegalStateException(this.poolName + " - is not suspendable");
      }

      if (this.poolState != 1) {
         this.suspendResumeLock.perform3();
         this.poolState = 1;
      }
   }

   @Override
   public synchronized void resumePool() {
      if (this.poolState == 1) {
         this.poolState = 0;
         this.fillPool();
         this.suspendResumeLock.perform4();
      }
   }

   void logPoolState(String[] texts) {
      if (this.LOGGER.obtainFlag()) {
         this.LOGGER
            .handleText(
               "{} - {}stats (total={}, active={}, idle={}, waiting={})",
               new Object[]{this.poolName, texts.length > 0 ? texts[0] : "", this.getTotalConnections(), this.getActiveConnections(), this.getIdleConnections(), this.getThreadsAwaitingConnection()}
         );
      }
   }

   @Override
   void recycle(PoolEntry poolEntry) {
      this.connectionBag.requite(poolEntry);
   }

   void closeConnection(PoolEntry poolEntry, String text) {
      if (this.connectionBag.remove(poolEntry)) {
         Connection close2 = poolEntry.close();
         this.closeConnectionExecutor.execute(() -> this.handleConnection(close2, text));
      }
   }

   private final PoolEntry createPoolEntry() {
      try {
         PoolEntry poolEntry = this.newPoolEntry();
         long maxLifetime = this.hikariConfig.getMaxLifetime();
         if (maxLifetime > 0L) {
            long value = maxLifetime > 10000L ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L;
            long value2 = maxLifetime - value;
            poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(new MaxLifetimeTask(this, poolEntry), value2, TimeUnit.MILLISECONDS));
         }

         long value3 = this.hikariConfig.getKeepaliveTime();
         if (value3 > 0L) {
            long nextLong2 = ThreadLocalRandom.current().nextLong(value3 / 10L);
            long value4 = value3 - nextLong2;
            poolEntry.setScheduledFuture3(this.houseKeepingExecutorService.scheduleWithFixedDelay(new ConnectionDeadTask(this, poolEntry), value4, value4, TimeUnit.MILLISECONDS));
         }

         return poolEntry;
      } catch (ConnectionSetupException e) {
         if (this.poolState == 0) {
            this.LOGGER.handleText2("%s - Error thrown while acquiring connection from data source", new Object[]{this.poolName, e.getCause()});
            this.lastConnectionFailure.set(e);
         }
      } catch (Exception e2) {
         if (this.poolState == 0) {
            this.LOGGER.handleText("%s - Cannot acquire connection from data source", new Object[]{this.poolName, e2});
         }
      }

      return null;
   }

   private final synchronized void fillPool() {
      int value = Math.min(this.hikariConfig.getMaximumPoolSize() - this.getTotalConnections(), this.hikariConfig.getMinimumIdle() - this.getIdleConnections()) - this.addConnectionQueueReadOnlyView.size();
      if (value <= 0) {
         this.LOGGER.handleText("%s - Fill pool skipped, pool is at sufficient level.", new Object[]{this.poolName});
      }

      for (int i = 0; i < value; i++) {
         this.addConnectionExecutor.submit(i < value - 1 ? this.poolEntryCreator : this.postFillPoolEntryCreator);
      }
   }

   private final void abortActiveConnections(ExecutorService executorService) {
      for (PoolEntry poolEntry : (Iterable<PoolEntry>) this.connectionBag.values(1)) {
         Connection close2 = poolEntry.close();

         try {
            close2.abort(executorService);
         } catch (Throwable e) {
            this.quietlyCloseConnection(close2, "(connection aborted during shutdown)");
         } finally {
            this.connectionBag.remove(poolEntry);
         }
      }
   }

   private final void checkFailFast() {
      long value = this.hikariConfig.getInitializationFailTimeout();
      if (value >= 0L) {
         long currentTime2 = ClockSource.currentTime();

         do {
            PoolEntry poolEntry = this.createPoolEntry();
            if (poolEntry != null) {
               if (this.hikariConfig.getMinimumIdle() > 0) {
                  this.connectionBag.add(poolEntry);
                  this.LOGGER.handleText("%s - Added connection %s", new Object[]{this.poolName, poolEntry.connection2.toString()});
               } else {
                  this.quietlyCloseConnection(poolEntry.close(), "(initialization check complete and minimumIdle is zero)");
               }

               return;
            }

            if (this.getLastConnectionFailure() instanceof ConnectionSetupException) {
               this.throwPoolInitializationException(this.getLastConnectionFailure().getCause());
            }

            TransactionIsolation.handleLongValue(TimeUnit.SECONDS.toMillis(1L));
         } while (ClockSource.elapsedMillis(currentTime2) < value);

         if (value > 0L) {
            this.throwPoolInitializationException(this.getLastConnectionFailure());
         }
      }
   }

   private final void throwPoolInitializationException(Throwable error) {
      this.perform();
      throw new PoolInitializationException(error);
   }

   private final boolean evictConnectionInternal(PoolEntry poolEntry, String text, boolean flag) {
      poolEntry.markEvicted();
      if (!flag && !this.connectionBag.reserve(poolEntry)) {
         return false;
      }

      this.closeConnection(poolEntry, text);
      return true;
   }

   private final ScheduledExecutorService initializeHouseKeepingExecutorService() {
      if (this.hikariConfig.getScheduledExecutor() == null) {
         ThreadFactory orElseGet2 = Optional.ofNullable(this.hikariConfig.getThreadFactory()).orElseGet(this::createThreadFactory);
         ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, orElseGet2, new DiscardPolicy());
         scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
         scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
         return scheduledThreadPoolExecutor;
      } else {
         return this.hikariConfig.getScheduledExecutor();
      }
   }

   private final void perform() {
      if (this.hikariConfig.getScheduledExecutor() == null) {
         this.houseKeepingExecutorService.shutdownNow();
      }
   }

   private final SQLException createTimeoutException(long value) {
      this.logPoolState(new String[]{"Timeout failure "});
      String text = null;
      Exception lastConnectionFailure = this.getLastConnectionFailure();
      if (lastConnectionFailure instanceof SQLException) {
         text = ((SQLException)lastConnectionFailure).getSQLState();
      }

      SQLTransientConnectionException value2 = new SQLTransientConnectionException(
         this.poolName + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(value) + "ms.", text, lastConnectionFailure
      );
      if (lastConnectionFailure instanceof SQLException) {
         value2.setNextException((SQLException)lastConnectionFailure);
      }

      return value2;
   }

   @Override
   public DataSource getUnwrappedDataSource() {
      return super.getUnwrappedDataSource();
   }

   @Override
   public String toString() {
      return super.toString();
   }

   private final ThreadFactory createThreadFactory() {
      return new PoolThreadFactory(this.poolName + " housekeeper", true);
   }

   private final void handleConnection(Connection connection, String text) {
      this.quietlyCloseConnection(connection, text);
      if (this.poolState == 0) {
         this.fillPool();
      }
   }

   private final void evictConnection(PoolEntry poolEntry) {
      this.evictConnectionInternal(poolEntry, "(connection evicted)", false);
   }

   static PoolEntry access$createPoolEntry(HikariPool hikariPool) {
      return hikariPool.createPoolEntry();
   }

   static ConcurrentBag access$connectionBag(HikariPool hikariPool) {
      return hikariPool.connectionBag;
   }

   static PoolLogger access$LOGGER(HikariPool hikariPool) {
      return hikariPool.LOGGER;
   }

   static long access$HOUSEKEEPING_PERIOD_MS(HikariPool hikariPool) {
      return hikariPool.HOUSEKEEPING_PERIOD_MS;
   }

   static LeakTaskFactory access$leakTaskFactory(HikariPool hikariPool) {
      return hikariPool.leakTaskFactory;
   }

   static void access$fillPool(HikariPool hikariPool) {
      hikariPool.fillPool();
   }

   static boolean access$evictConnectionInternal(HikariPool hikariPool, PoolEntry poolEntry, String text, boolean flag) {
      return hikariPool.evictConnectionInternal(poolEntry, text, flag);
   }
}
