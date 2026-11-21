package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import io.lumine.mythic.bukkit.utils.storage.sql.hikari.HikariConfig;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics.MetricsTracker;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.ClockSource;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.DriverDataSource;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.PropertyElf;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.UtilityElf;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PoolBase {
   private final Logger LOGGER = LoggerFactory.getLogger(PoolBase.class);
   protected final HikariConfig config;
   protected final String poolName;
   protected long connectionTimeout;
   protected long validationTimeout;
   private static final String[] RESET_STATES = new String[]{"readOnly", "autoCommit", "isolation", "catalog", "netTimeout"};
   private static final int UNINITIALIZED = -1;
   private static final int TRUE = 1;
   private static final int FALSE = 0;
   private int networkTimeout;
   private int isNetworkTimeoutSupported;
   private int isQueryTimeoutSupported;
   private int defaultTransactionIsolation;
   private int transactionIsolation;
   private Executor netTimeoutExecutor;
   private DataSource dataSource;
   private final String catalog;
   private final boolean isReadOnly;
   private final boolean isAutoCommit;
   private final boolean isUseJdbc4Validation;
   private final boolean isIsolateInternalQueries;
   private final AtomicReference<Throwable> lastConnectionFailure;
   private volatile boolean isValidChecked;

   PoolBase(HikariConfig config) {
      this.config = config;
      this.networkTimeout = -1;
      this.catalog = config.getCatalog();
      this.isReadOnly = config.isReadOnly();
      this.isAutoCommit = config.isAutoCommit();
      this.transactionIsolation = UtilityElf.getTransactionIsolation(config.getTransactionIsolation());
      this.isQueryTimeoutSupported = -1;
      this.isNetworkTimeoutSupported = -1;
      this.isUseJdbc4Validation = config.getConnectionTestQuery() == null;
      this.isIsolateInternalQueries = config.isIsolateInternalQueries();
      this.poolName = config.getPoolName();
      this.connectionTimeout = config.getConnectionTimeout();
      this.validationTimeout = config.getValidationTimeout();
      this.lastConnectionFailure = new AtomicReference<>();
      this.initializeDataSource();
   }

   @Override
   public String toString() {
      return this.poolName;
   }

   abstract void releaseConnection(PoolEntry poolEntry);

   void quietlyCloseConnection(Connection connection, String closureReason) {
      if (connection != null) {
         try {
            this.LOGGER.debug("{} - Closing connection {}: {}", this.poolName, connection, closureReason);

            try {
               this.setNetworkTimeout(connection, TimeUnit.SECONDS.toMillis(15L));
            } finally {
               connection.close();
            }
         } catch (Throwable e) {
            this.LOGGER.debug("{} - Closing connection {} failed", this.poolName, connection, e);
         }
      }
   }

   boolean isConnectionAlive(Connection connection) {
      try {
         if (this.isUseJdbc4Validation) {
            return connection.isValid((int)TimeUnit.MILLISECONDS.toSeconds(Math.max(1000L, this.validationTimeout)));
         } else {
            this.setNetworkTimeout(connection, this.validationTimeout);

            try (Statement statement = connection.createStatement()) {
               if (this.isNetworkTimeoutSupported != 1) {
                  this.setQueryTimeout(statement, (int)TimeUnit.MILLISECONDS.toSeconds(Math.max(1000L, this.validationTimeout)));
               }

               statement.execute(this.config.getConnectionTestQuery());
            }

            if (this.isIsolateInternalQueries && !this.isReadOnly && !this.isAutoCommit) {
               connection.rollback();
            }

            this.setNetworkTimeout(connection, this.networkTimeout);
            return true;
         }
      } catch (SQLException e) {
         this.lastConnectionFailure.set(e);
         this.LOGGER.warn("{} - Failed to validate connection {} ({})", this.poolName, connection, e.getMessage());
         return false;
      }
   }

   Throwable getLastConnectionFailure() {
      return this.lastConnectionFailure.getAndSet(null);
   }

   public DataSource getUnwrappedDataSource() {
      return this.dataSource;
   }

   PoolEntry newPoolEntry() throws Exception {
      return new PoolEntry(this.newConnection(), this, this.isReadOnly, this.isAutoCommit);
   }

   void resetConnectionState(Connection connection, ProxyConnection proxyConnection, int dirtyBits) throws SQLException {
      int resetBits = 0;
      if ((dirtyBits & 1) != 0 && proxyConnection.getReadOnlyState() != this.isReadOnly) {
         connection.setReadOnly(this.isReadOnly);
         resetBits |= 1;
      }

      if ((dirtyBits & 2) != 0 && proxyConnection.getAutoCommitState() != this.isAutoCommit) {
         connection.setAutoCommit(this.isAutoCommit);
         resetBits |= 2;
      }

      if ((dirtyBits & 4) != 0 && proxyConnection.getTransactionIsolationState() != this.transactionIsolation) {
         connection.setTransactionIsolation(this.transactionIsolation);
         resetBits |= 4;
      }

      if ((dirtyBits & 8) != 0 && this.catalog != null && !this.catalog.equals(proxyConnection.getCatalogState())) {
         connection.setCatalog(this.catalog);
         resetBits |= 8;
      }

      if ((dirtyBits & 16) != 0 && proxyConnection.getNetworkTimeoutState() != this.networkTimeout) {
         this.setNetworkTimeout(connection, this.networkTimeout);
         resetBits |= 16;
      }

      if (resetBits != 0 && this.LOGGER.isDebugEnabled()) {
         this.LOGGER.debug("{} - Reset ({}) on connection {}", this.poolName, this.stringFromResetBits(resetBits), connection);
      }
   }

   void shutdownNetworkTimeoutExecutor() {
      if (this.netTimeoutExecutor instanceof ThreadPoolExecutor) {
         ((ThreadPoolExecutor)this.netTimeoutExecutor).shutdownNow();
      }
   }

   void registerMBeans(HikariPool hikariPool) {
      if (this.config.isRegisterMbeans()) {
         try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName beanConfigName = new ObjectName("io.lumine.mythic.bukkit.utils.storage.sql.hikari:type=PoolConfig (" + this.poolName + ")");
            ObjectName beanPoolName = new ObjectName("io.lumine.mythic.bukkit.utils.storage.sql.hikari:type=Pool (" + this.poolName + ")");
            if (!mBeanServer.isRegistered(beanConfigName)) {
               mBeanServer.registerMBean(this.config, beanConfigName);
               mBeanServer.registerMBean(hikariPool, beanPoolName);
            } else {
               this.LOGGER.error("{} - You cannot use the same pool name for separate pool instances.", this.poolName);
            }
         } catch (Exception e) {
            this.LOGGER.warn("{} - Failed to register management beans.", this.poolName, e);
         }
      }
   }

   void unregisterMBeans() {
      if (this.config.isRegisterMbeans()) {
         try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName beanConfigName = new ObjectName("io.lumine.mythic.bukkit.utils.storage.sql.hikari:type=PoolConfig (" + this.poolName + ")");
            ObjectName beanPoolName = new ObjectName("io.lumine.mythic.bukkit.utils.storage.sql.hikari:type=Pool (" + this.poolName + ")");
            if (mBeanServer.isRegistered(beanConfigName)) {
               mBeanServer.unregisterMBean(beanConfigName);
               mBeanServer.unregisterMBean(beanPoolName);
            }
         } catch (Exception e) {
            this.LOGGER.warn("{} - Failed to unregister management beans.", this.poolName, e);
         }
      }
   }

   private void initializeDataSource() {
      String jdbcUrl = this.config.getJdbcUrl();
      String username = this.config.getUsername();
      String password = this.config.getPassword();
      String dsClassName = this.config.getDataSourceClassName();
      String driverClassName = this.config.getDriverClassName();
      Properties dataSourceProperties = this.config.getDataSourceProperties();
      DataSource dataSource = this.config.getDataSource();
      if (dsClassName != null && dataSource == null) {
         dataSource = UtilityElf.createInstance(dsClassName, DataSource.class);
         PropertyElf.setTargetFromProperties(dataSource, dataSourceProperties);
      } else if (jdbcUrl != null && dataSource == null) {
         dataSource = new DriverDataSource(jdbcUrl, driverClassName, dataSourceProperties, username, password);
      }

      if (dataSource != null) {
         this.setLoginTimeout(dataSource, this.connectionTimeout);
         this.createNetworkTimeoutExecutor(dataSource, dsClassName, jdbcUrl);
      }

      this.dataSource = dataSource;
   }

   Connection newConnection() throws Exception {
      Connection connection = null;

      try {
         String username = this.config.getUsername();
         String password = this.config.getPassword();
         connection = username == null ? this.dataSource.getConnection() : this.dataSource.getConnection(username, password);
         if (connection == null) {
            throw new SQLTransientConnectionException("DataSource returned null unexpectedly");
         } else {
            this.setupConnection(connection);
            this.lastConnectionFailure.set(null);
            return connection;
         }
      } catch (Exception e) {
         this.lastConnectionFailure.set(e);
         this.quietlyCloseConnection(connection, "(Failed to create/set connection)");
         throw e;
      }
   }

   private void setupConnection(Connection connection) throws SQLException {
      if (this.networkTimeout == -1) {
         this.networkTimeout = this.getAndSetNetworkTimeout(connection, this.validationTimeout);
      } else {
         this.setNetworkTimeout(connection, this.validationTimeout);
      }

      this.checkDriverSupport(connection);
      connection.setReadOnly(this.isReadOnly);
      connection.setAutoCommit(this.isAutoCommit);
      if (this.transactionIsolation != this.defaultTransactionIsolation) {
         connection.setTransactionIsolation(this.transactionIsolation);
      }

      if (this.catalog != null) {
         connection.setCatalog(this.catalog);
      }

      this.executeSql(connection, this.config.getConnectionInitSql(), true);
      this.setNetworkTimeout(connection, this.networkTimeout);
   }

   private void checkDriverSupport(Connection connection) throws SQLException {
      if (!this.isValidChecked) {
         if (this.isUseJdbc4Validation) {
            try {
               connection.isValid(1);
            } catch (Throwable e) {
               this.LOGGER.error("{} - Failed to execute isValid() for connection, configure connection test query. ({})", this.poolName, e.getMessage());
               throw e;
            }
         } else {
            try {
               this.executeSql(connection, this.config.getConnectionTestQuery(), false);
            } catch (Throwable ex) {
               this.LOGGER.error("{} - Failed to execute connection test query. ({})", this.poolName, ex.getMessage());
               throw ex;
            }
         }

         this.defaultTransactionIsolation = connection.getTransactionIsolation();
         if (this.transactionIsolation == -1) {
            this.transactionIsolation = this.defaultTransactionIsolation;
         }

         this.isValidChecked = true;
      }
   }

   private void setQueryTimeout(Statement statement, int timeoutSec) {
      if (this.isQueryTimeoutSupported != 0) {
         try {
            statement.setQueryTimeout(timeoutSec);
            this.isQueryTimeoutSupported = 1;
         } catch (Throwable e) {
            if (this.isQueryTimeoutSupported == -1) {
               this.isQueryTimeoutSupported = 0;
               this.LOGGER.warn("{} - Failed to set query timeout for statement. ({})", this.poolName, e.getMessage());
            }
         }
      }
   }

   private int getAndSetNetworkTimeout(Connection connection, long timeoutMs) {
      if (this.isNetworkTimeoutSupported != 0) {
         try {
            int originalTimeout = connection.getNetworkTimeout();
            connection.setNetworkTimeout(this.netTimeoutExecutor, (int)timeoutMs);
            this.isNetworkTimeoutSupported = 1;
            return originalTimeout;
         } catch (Throwable e) {
            if (this.isNetworkTimeoutSupported == -1) {
               this.isNetworkTimeoutSupported = 0;
               this.LOGGER.info("{} - Driver does not support get/set network timeout for connections. ({})", this.poolName, e.getMessage());
               if (this.validationTimeout < TimeUnit.SECONDS.toMillis(1L)) {
                  this.LOGGER
                     .warn("{} - A validationTimeout of less than 1 second cannot be honored on drivers without setNetworkTimeout() support.", this.poolName);
               } else if (this.validationTimeout % TimeUnit.SECONDS.toMillis(1L) != 0L) {
                  this.LOGGER
                     .warn(
                        "{} - A validationTimeout with fractional second granularity cannot be honored on drivers without setNetworkTimeout() support.",
                        this.poolName
                     );
               }
            }
         }
      }

      return 0;
   }

   private void setNetworkTimeout(Connection connection, long timeoutMs) throws SQLException {
      if (this.isNetworkTimeoutSupported == 1) {
         connection.setNetworkTimeout(this.netTimeoutExecutor, (int)timeoutMs);
      }
   }

   private void executeSql(Connection connection, String sql, boolean isCommit) throws SQLException {
      if (sql != null) {
         try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
         }

         if (this.isIsolateInternalQueries && !this.isReadOnly && !this.isAutoCommit) {
            if (isCommit) {
               connection.commit();
            } else {
               connection.rollback();
            }
         }
      }
   }

   private void createNetworkTimeoutExecutor(DataSource dataSource, String dsClassName, String jdbcUrl) {
      if ((dsClassName == null || !dsClassName.contains("Mysql"))
         && (jdbcUrl == null || !jdbcUrl.contains("mysql"))
         && (dataSource == null || !dataSource.getClass().getName().contains("Mysql"))) {
         ThreadFactory threadFactory = this.config.getThreadFactory();
         threadFactory = (ThreadFactory)(threadFactory != null
            ? threadFactory
            : new UtilityElf.DefaultThreadFactory(this.poolName + " network timeout executor", true));
         ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool(threadFactory);
         executor.setKeepAliveTime(15L, TimeUnit.SECONDS);
         executor.allowCoreThreadTimeOut(true);
         this.netTimeoutExecutor = executor;
      } else {
         this.netTimeoutExecutor = new PoolBase.SynchronousExecutor();
      }
   }

   private void setLoginTimeout(DataSource dataSource, long connectionTimeout) {
      if (connectionTimeout != 2147483647L) {
         try {
            dataSource.setLoginTimeout((int)TimeUnit.MILLISECONDS.toSeconds(Math.max(1000L, connectionTimeout)));
         } catch (Throwable e) {
            this.LOGGER.warn("{} - Failed to set login timeout for data source. ({})", this.poolName, e.getMessage());
         }
      }
   }

   private String stringFromResetBits(int bits) {
      StringBuilder sb = new StringBuilder();

      for (int ndx = 0; ndx < RESET_STATES.length; ndx++) {
         if ((bits & 1 << ndx) != 0) {
            sb.append(RESET_STATES[ndx]).append(", ");
         }
      }

      sb.setLength(sb.length() - 2);
      return sb.toString();
   }

   static class MetricsTrackerDelegate implements AutoCloseable {
      final MetricsTracker tracker;

      protected MetricsTrackerDelegate() {
         this.tracker = null;
      }

      MetricsTrackerDelegate(MetricsTracker tracker) {
         this.tracker = tracker;
      }

      @Override
      public void close() {
         this.tracker.close();
      }

      void recordConnectionUsage(PoolEntry poolEntry) {
         this.tracker.recordConnectionUsageMillis(poolEntry.getMillisSinceBorrowed());
      }

      void recordBorrowStats(PoolEntry poolEntry, long startTime) {
         long now = ClockSource.INSTANCE.currentTime();
         poolEntry.lastBorrowed = now;
         this.tracker.recordConnectionAcquiredNanos(ClockSource.INSTANCE.elapsedNanos(startTime, now));
      }

      void recordConnectionTimeout() {
         this.tracker.recordConnectionTimeout();
      }
   }

   static final class NopMetricsTrackerDelegate extends PoolBase.MetricsTrackerDelegate {
      @Override
      void recordConnectionUsage(PoolEntry poolEntry) {
      }

      @Override
      public void close() {
      }

      @Override
      void recordBorrowStats(PoolEntry poolEntry, long startTime) {
      }

      @Override
      void recordConnectionTimeout() {
      }
   }

   private static class SynchronousExecutor implements Executor {
      private SynchronousExecutor() {
      }

      @Override
      public void execute(Runnable command) {
         try {
            command.run();
         } catch (Throwable e) {
            LoggerFactory.getLogger(PoolBase.class).debug("Failed to execute: {}", command, e);
         }
      }
   }
}
