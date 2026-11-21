package litebans;

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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

abstract class PoolBase {
   private final PoolLogger logger = PoolLogger.computePoolLogger(PoolBase.class);
   public final HikariConfig hikariConfig;
   protected final String poolName;
   volatile String catalog;
   final AtomicReference lastConnectionFailure;
   long connectionTimeout;
   long validationTimeout;
   SQLExceptionOverride exceptionOverride;
   private static final String[] RESET_STATES = new String[]{"readOnly", "autoCommit", "isolation", "catalog", "netTimeout", "schema"};
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
   private final String schema;
   private final boolean isReadOnly;
   private final boolean isAutoCommit;
   private final boolean isUseJdbc4Validation;
   private final boolean isIsolateInternalQueries;
   private volatile boolean isShutdown;

   PoolBase(HikariConfig hikariConfig) {
      this.hikariConfig = hikariConfig;
      this.networkTimeout = -1;
      this.catalog = hikariConfig.getCatalog();
      this.schema = hikariConfig.getSchema();
      this.isReadOnly = hikariConfig.isReadOnly();
      this.isAutoCommit = hikariConfig.isAutoCommit();
      this.exceptionOverride = (SQLExceptionOverride)TransactionIsolation.computeObj(hikariConfig.getExceptionOverrideClassName(), SQLExceptionOverride.class, new Object[0]);
      this.transactionIsolation = TransactionIsolation.computeIntValue(hikariConfig.getTransactionIsolation());
      this.isQueryTimeoutSupported = -1;
      this.isNetworkTimeoutSupported = -1;
      this.isUseJdbc4Validation = hikariConfig.getConnectionTestQuery() == null;
      this.isIsolateInternalQueries = hikariConfig.isIsolateInternalQueries();
      this.poolName = hikariConfig.getPoolName();
      this.connectionTimeout = hikariConfig.getConnectionTimeout();
      this.validationTimeout = hikariConfig.getValidationTimeout();
      this.lastConnectionFailure = new AtomicReference();
      this.initializeDataSource();
   }

   @Override
   public String toString() {
      return this.poolName;
   }

   abstract void recycle(PoolEntry poolEntry);

   void quietlyCloseConnection(Connection connection, String text) {
      if (connection != null) {
         try {
            this.logger.handleText("%s - Closing connection %s: %s", new Object[]{this.poolName, connection.toString(), text});

            try {
               this.setNetworkTimeout(connection, TimeUnit.SECONDS.toMillis(15L));
            } catch (Throwable e) {
               if (!(e instanceof SQLException)) {
                  throw SneakyThrow.<RuntimeException>rethrow(e);
               }
            } finally {
               connection.close();
            }
         } catch (Exception e2) {
            this.logger.handleText("%s - Closing connection %s failed", new Object[]{this.poolName, connection.toString(), e2});
         }
      }
   }

   boolean isConnectionAlive(Connection connection) {
      try {
         try {
            this.setNetworkTimeout(connection, this.validationTimeout);
            int value = (int)Math.max(1000L, this.validationTimeout) / 1000;
            if (this.isUseJdbc4Validation) {
               return connection.isValid(value);
            }

            try (Statement statement = connection.createStatement()) {
               if (this.isNetworkTimeoutSupported != 1) {
                  this.setQueryTimeout(statement, value);
               }

               statement.execute(this.hikariConfig.getConnectionTestQuery());
            }
         } finally {
            this.setNetworkTimeout(connection, this.networkTimeout);
            if (this.isIsolateInternalQueries && !this.isAutoCommit) {
               connection.rollback();
            }
         }

         return true;
      } catch (Exception e) {
         this.lastConnectionFailure.set(e);
         this.logger
            .handleText4(
               "%s - Failed to validate connection %s (%s). Possibly consider using a shorter maxLifetime value.",
               new Object[]{this.poolName, connection.toString(), e.getMessage()}
         );
         return false;
      }
   }

   Exception getLastConnectionFailure() {
      return (Exception)this.lastConnectionFailure.get();
   }

   public DataSource getUnwrappedDataSource() {
      return this.dataSource;
   }

   PoolEntry newPoolEntry() throws Exception {
      return new PoolEntry(this.newConnection(), this, this.isReadOnly, this.isAutoCommit);
   }

   void resetConnectionState(Connection connection, ProxyConnection proxyConnection, int value) {
      try {
         byte b2 = 0;
         if ((value & 1) != 0 && proxyConnection.obtainFlag2() != this.isReadOnly) {
            connection.setReadOnly(this.isReadOnly);
            b2 |= 1;
         }

         if ((value & 2) != 0 && proxyConnection.obtainFlag() != this.isAutoCommit) {
            connection.setAutoCommit(this.isAutoCommit);
            b2 |= 2;
         }

         if ((value & 4) != 0 && proxyConnection.obtainIntValue() != this.transactionIsolation) {
            connection.setTransactionIsolation(this.transactionIsolation);
            b2 |= 4;
         }

         if ((value & 8) != 0 && this.catalog != null && !this.catalog.equals(proxyConnection.obtainText())) {
            connection.setCatalog(this.catalog);
            b2 |= 8;
         }

         if ((value & 16) != 0 && proxyConnection.obtainIntValue2() != this.networkTimeout) {
            this.setNetworkTimeout(connection, this.networkTimeout);
            b2 |= 16;
         }

         if ((value & 32) != 0 && this.schema != null && !this.schema.equals(proxyConnection.obtainText2())) {
            connection.setSchema(this.schema);
            b2 |= 32;
         }

         if (b2 != 0 && this.logger.obtainFlag()) {
            this.logger.handleText("%s - Reset (%s) on connection %s", new Object[]{this.poolName, this.stringFromResetBits(b2), connection.toString()});
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   void shutdownNetworkTimeoutExecutor() {
      if (this.netTimeoutExecutor instanceof ThreadPoolExecutor) {
         ((ThreadPoolExecutor)this.netTimeoutExecutor).shutdownNow();
      }
   }

   long getLoginTimeout() {
      try {
         return this.dataSource != null ? this.dataSource.getLoginTimeout() : TimeUnit.SECONDS.toSeconds(5L);
      } catch (SQLException e) {
         return TimeUnit.SECONDS.toSeconds(5L);
      }
   }

   void handleMBeans(HikariPool hikariPool, boolean flag) {
      if (this.hikariConfig.isRegisterMbeans()) {
         try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName;
            ObjectName objectName2;
            if ("true".equals(System.getProperty("hikaricp.jmx.register2.0"))) {
               objectName = new ObjectName("com.zaxxer.hikari:type=PoolConfig,name=" + this.poolName);
               objectName2 = new ObjectName("com.zaxxer.hikari:type=Pool,name=" + this.poolName);
            } else {
               objectName = new ObjectName("com.zaxxer.hikari:type=PoolConfig (" + this.poolName + ")");
               objectName2 = new ObjectName("com.zaxxer.hikari:type=Pool (" + this.poolName + ")");
            }

            if (flag) {
               if (!platformMBeanServer.isRegistered(objectName)) {
                  platformMBeanServer.registerMBean(this.hikariConfig, objectName);
                  platformMBeanServer.registerMBean(hikariPool, objectName2);
               } else {
                  this.logger.handleText2("{} - JMX name ({}) is already registered.", new Object[]{this.poolName, this.poolName});
               }
            } else if (platformMBeanServer.isRegistered(objectName)) {
               platformMBeanServer.unregisterMBean(objectName);
               platformMBeanServer.unregisterMBean(objectName2);
            }
         } catch (Exception e) {
            this.logger.handleText4("{} - Failed to {} management beans.", new Object[]{this.poolName, flag ? "register" : "unregister", e});
         }
      }
   }

   private final void initializeDataSource() {
      String text = this.hikariConfig.getJdbcUrl();
      String text2 = this.hikariConfig.getUsername();
      String text3 = this.hikariConfig.getPassword();
      String text4 = this.hikariConfig.getDataSourceClassName();
      String text5 = this.hikariConfig.getDriverClassName();
      String text6 = this.hikariConfig.getDataSourceJndiName();
      Properties properties = this.hikariConfig.getDataSourceProperties();
      DataSource dataSource = this.hikariConfig.getDataSource();
      if (text4 != null && dataSource == null) {
         dataSource = (DataSource)TransactionIsolation.computeObj(text4, DataSource.class, new Object[0]);
         PropertyElf.handleObj(dataSource, properties);
      } else if (text != null && dataSource == null) {
         dataSource = new DriverDataSource(text, text5, properties, text2, text3);
      } else if (text6 != null && dataSource == null) {
         try {
            InitialContext initialContext = new InitialContext();
            dataSource = (DataSource)initialContext.lookup(text6);
         } catch (NamingException e) {
            throw new PoolInitializationException(e);
         }
      }

      if (dataSource != null) {
         this.setLoginTimeout(dataSource);
         this.handleDataSource(dataSource, text4, text);
      }

      this.dataSource = dataSource;
   }

   private final Connection newConnection() throws Exception {
      long currentTime2 = ClockSource.currentTime();
      Connection connection = null;

      try {
         String text = this.hikariConfig.getUsername();
         String text2 = this.hikariConfig.getPassword();
         connection = text == null ? this.dataSource.getConnection() : this.dataSource.getConnection(text, text2);
         if (connection == null) {
            throw new SQLTransientConnectionException("DataSource returned null unexpectedly");
         }

         this.setupConnection(connection);
         this.lastConnectionFailure.set(null);
         return connection;
      } catch (Exception e) {
         if (connection != null) {
            this.quietlyCloseConnection(connection, "(Failed to create/setup connection)");
         } else if (this.getLastConnectionFailure() == null) {
            this.logger.handleText("%s - Failed to create/setup connection: %s", new Object[]{this.poolName, e.getMessage()});
         }

         this.lastConnectionFailure.set(e);
         throw e;
      }
   }

   private final void setupConnection(Connection connection) throws ConnectionSetupException {
      try {
         if (this.networkTimeout == -1) {
            this.networkTimeout = this.getAndSetNetworkTimeout(connection, this.validationTimeout);
         } else {
            this.setNetworkTimeout(connection, this.validationTimeout);
         }

         if (connection.isReadOnly() != this.isReadOnly) {
            connection.setReadOnly(this.isReadOnly);
         }

         if (connection.getAutoCommit() != this.isAutoCommit) {
            connection.setAutoCommit(this.isAutoCommit);
         }

         this.checkDriverSupport(connection);
         if (this.transactionIsolation != this.defaultTransactionIsolation) {
            connection.setTransactionIsolation(this.transactionIsolation);
         }

         if (this.catalog != null) {
            connection.setCatalog(this.catalog);
         }

         if (this.schema != null) {
            connection.setSchema(this.schema);
         }

         this.executeSql(connection, this.hikariConfig.getConnectionInitSql(), true);
         this.setNetworkTimeout(connection, this.networkTimeout);
      } catch (SQLException e) {
         throw new ConnectionSetupException(e);
      }
   }

   private final void checkDriverSupport(Connection connection) throws ConnectionSetupException {
      if (!this.isShutdown) {
         this.checkValidationSupport(connection);
         this.checkDefaultIsolation(connection);
         this.isShutdown = true;
      }
   }

   private final void checkValidationSupport(Connection connection) {
      try {
         try {
            if (this.isUseJdbc4Validation) {
               connection.isValid(1);
            } else {
               this.executeSql(connection, this.hikariConfig.getConnectionTestQuery(), false);
            }
         } catch (Exception | AbstractMethodError e2) {
            this.logger
               .handleText2(
                  "%s - Failed to execute%s connection test query (%s).",
                  new Object[]{this.poolName, this.isUseJdbc4Validation ? " isValid() for connection, configure" : "", e2.getMessage()}
            );
            throw e2;
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void checkDefaultIsolation(Connection connection) {
      try {
         try {
            this.defaultTransactionIsolation = connection.getTransactionIsolation();
            if (this.transactionIsolation == -1) {
               this.transactionIsolation = this.defaultTransactionIsolation;
            }
         } catch (SQLException e2) {
            this.logger.handleText4("%s - Default transaction isolation level detection failed (%s).", new Object[]{this.poolName, e2.getMessage()});
            if (e2.getSQLState() != null && !e2.getSQLState().startsWith("08")) {
               throw e2;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void setQueryTimeout(Statement statement, int value) {
      if (this.isQueryTimeoutSupported != 0) {
         try {
            statement.setQueryTimeout(value);
            this.isQueryTimeoutSupported = 1;
         } catch (Exception e) {
            if (this.isQueryTimeoutSupported == -1) {
               this.isQueryTimeoutSupported = 0;
               this.logger.handleText3("%s - Failed to set query timeout for statement. (%s)", new Object[]{this.poolName, e.getMessage()});
            }
         }
      }
   }

   private final int getAndSetNetworkTimeout(Connection connection, long value) {
      if (this.isNetworkTimeoutSupported != 0) {
         try {
            int networkTimeout = connection.getNetworkTimeout();
            connection.setNetworkTimeout(this.netTimeoutExecutor, (int)value);
            this.isNetworkTimeoutSupported = 1;
            return networkTimeout;
         } catch (Exception | AbstractMethodError e) {
            if (this.isNetworkTimeoutSupported == -1) {
               this.isNetworkTimeoutSupported = 0;
               this.logger.handleText3("%s - Driver does not support get/set network timeout for connections. (%s)", new Object[]{this.poolName, e.getMessage()});
               if (this.validationTimeout < TimeUnit.SECONDS.toMillis(1L)) {
                  this.logger
                     .handleText4(
                        "%s - A validationTimeout of less than 1 second cannot be honored on drivers without setNetworkTimeout() support.",
                        new Object[]{this.poolName}
                  );
               } else if (this.validationTimeout % TimeUnit.SECONDS.toMillis(1L) != 0L) {
                  this.logger
                     .handleText4(
                        "%s - A validationTimeout with fractional second granularity cannot be honored on drivers without setNetworkTimeout() support.",
                        new Object[]{this.poolName}
                  );
               }
            }
         }
      }

      return 0;
   }

   private final void setNetworkTimeout(Connection connection, long value) {
      try {
         if (this.isNetworkTimeoutSupported == 1) {
            connection.setNetworkTimeout(this.netTimeoutExecutor, (int)value);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void executeSql(Connection connection, String text, boolean flag) {
      try {
         if (text != null) {
            try (Statement statement = connection.createStatement()) {
               statement.execute(text);
            }

            if (this.isIsolateInternalQueries && !this.isAutoCommit) {
               if (flag) {
                  connection.commit();
               } else {
                  connection.rollback();
               }
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleDataSource(DataSource dataSource, String text, String text2) {
      if ((text == null || !text.contains("Mysql"))
         && (text2 == null || !text2.contains("mysql"))
         && (dataSource == null || !dataSource.getClass().getName().contains("Mysql"))) {
         ThreadFactory threadFactory = this.hikariConfig.getThreadFactory();
         ThreadFactory threadFactory2 = threadFactory != null ? threadFactory : new PoolThreadFactory(this.poolName + " network timeout executor", true);
         ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor)Executors.newCachedThreadPool(threadFactory2);
         cachedThreadPool.setKeepAliveTime(15L, TimeUnit.SECONDS);
         cachedThreadPool.allowCoreThreadTimeOut(true);
         this.netTimeoutExecutor = cachedThreadPool;
      } else {
         this.netTimeoutExecutor = new CallerRunsExecutor(null);
      }
   }

   private final void setLoginTimeout(DataSource dataSource) {
      if (this.connectionTimeout != 2147483647L) {
         try {
            dataSource.setLoginTimeout(Math.max(1, (int)TimeUnit.MILLISECONDS.toSeconds(500L + this.connectionTimeout)));
         } catch (Exception e) {
            this.logger.handleText3("%s - Failed to set login timeout for data source. (%s)", new Object[]{this.poolName, e.getMessage()});
         }
      }
   }

   private final String stringFromResetBits(int value) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < RESET_STATES.length; i++) {
         if ((value & 1 << i) != 0) {
            stringBuilder.append(RESET_STATES[i]).append(", ");
         }
      }

      stringBuilder.setLength(stringBuilder.length() - 2);
      return stringBuilder.toString();
   }
}
