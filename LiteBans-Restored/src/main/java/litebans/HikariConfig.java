package litebans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

public class HikariConfig implements HikariConfigMXBean {
   private static final PoolLogger LOGGER = PoolLogger.computePoolLogger(HikariConfig.class);
   private static final char[] ID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
   private static final long VALIDATION_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
   private static final long SOFT_TIMEOUT_FLOOR = Long.getLong("com.zaxxer.hikari.timeoutMs.floor", 250L);
   private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10L);
   private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30L);
   private static final long DEFAULT_KEEPALIVE_TIME = 0L;
   private static final int DEFAULT_POOL_SIZE = 10;
   private static boolean unitTest = false;
   private volatile String catalog;
   private volatile long connectionTimeout;
   private volatile long validationTimeout;
   private volatile long idleTimeout;
   private volatile long leakDetectionThreshold;
   private volatile long maxLifetime;
   private volatile int maximumPoolSize;
   private volatile int minimumIdle;
   private volatile String username;
   private volatile String password;
   private long initializationFailTimeout;
   private String connectionInitSql;
   private String connectionTestQuery;
   private String dataSourceClassName;
   private String dataSourceJndiName;
   private String driverClassName;
   private String exceptionOverrideClassName;
   private String jdbcUrl;
   private String poolName;
   private String schema;
   private String transactionIsolationName;
   private boolean autoCommit;
   private boolean readOnly;
   private boolean isolateInternalQueries;
   private boolean registerMbeans;
   private boolean allowPoolSuspension;
   private DataSource dataSource;
   private Properties dataSourceProperties = new Properties();
   private ThreadFactory threadFactory;
   private ScheduledExecutorService scheduledExecutor;
   private Properties healthCheckProperties = new Properties();
   private long keepaliveTime;
   private volatile boolean sealed;

   public HikariConfig() {
      this.minimumIdle = -1;
      this.maximumPoolSize = -1;
      this.maxLifetime = MAX_LIFETIME;
      this.connectionTimeout = CONNECTION_TIMEOUT;
      this.validationTimeout = VALIDATION_TIMEOUT;
      this.idleTimeout = IDLE_TIMEOUT;
      this.initializationFailTimeout = 1L;
      this.autoCommit = true;
      this.keepaliveTime = 0L;
      String property = System.getProperty("hikaricp.configurationFile");
      if (property != null) {
         this.loadFromPropertiesFile(property);
      }
   }

   @Override
   public String getCatalog() {
      return this.catalog;
   }

   @Override
   public void setCatalog(String text) {
      this.catalog = text;
   }

   @Override
   public long getConnectionTimeout() {
      return this.connectionTimeout;
   }

   @Override
   public void setConnectionTimeout(long value) {
      if (value == 0L) {
         this.connectionTimeout = 2147483647L;
      } else {
         if (value < SOFT_TIMEOUT_FLOOR) {
            throw new IllegalArgumentException("connectionTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
         }

         this.connectionTimeout = value;
      }
   }

   @Override
   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   @Override
   public void setIdleTimeout(long value) {
      if (value < 0L) {
         throw new IllegalArgumentException("idleTimeout cannot be negative");
      }

      this.idleTimeout = value;
   }

   @Override
   public long getLeakDetectionThreshold() {
      return this.leakDetectionThreshold;
   }

   @Override
   public void setLeakDetectionThreshold(long value) {
      this.leakDetectionThreshold = value;
   }

   @Override
   public long getMaxLifetime() {
      return this.maxLifetime;
   }

   @Override
   public void setMaxLifetime(long value) {
      this.maxLifetime = value;
   }

   @Override
   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   @Override
   public void setMaximumPoolSize(int value) {
      if (value < 1) {
         throw new IllegalArgumentException("maxPoolSize cannot be less than 1");
      }

      this.maximumPoolSize = value;
   }

   @Override
   public int getMinimumIdle() {
      return this.minimumIdle;
   }

   @Override
   public void setMinimumIdle(int value) {
      if (value < 0) {
         throw new IllegalArgumentException("minimumIdle cannot be negative");
      }

      this.minimumIdle = value;
   }

   public String getPassword() {
      return this.password;
   }

   @Override
   public void setPassword(String text) {
      this.password = text;
   }

   public String getUsername() {
      return this.username;
   }

   @Override
   public void setUsername(String text) {
      this.username = text;
   }

   @Override
   public long getValidationTimeout() {
      return this.validationTimeout;
   }

   @Override
   public void setValidationTimeout(long value) {
      if (value < SOFT_TIMEOUT_FLOOR) {
         throw new IllegalArgumentException("validationTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
      }

      this.validationTimeout = value;
   }

   public String getConnectionTestQuery() {
      return this.connectionTestQuery;
   }

   public void setConnectionTestQuery(String text) {
      this.checkIfSealed();
      this.connectionTestQuery = text;
   }

   public String getConnectionInitSql() {
      return this.connectionInitSql;
   }

   public void setConnectionInitSql(String text) {
      this.checkIfSealed();
      this.connectionInitSql = text;
   }

   public DataSource getDataSource() {
      return this.dataSource;
   }

   public void handleDataSource(DataSource dataSource) {
      this.checkIfSealed();
      this.dataSource = dataSource;
   }

   public String getDataSourceClassName() {
      return this.dataSourceClassName;
   }

   public void addDataSourceProperty(String text, Object obj) {
      this.checkIfSealed();
      this.dataSourceProperties.put(text, obj);
   }

   public String getDataSourceJndiName() {
      return this.dataSourceJndiName;
   }

   public Properties getDataSourceProperties() {
      return this.dataSourceProperties;
   }

   public String getDriverClassName() {
      return this.driverClassName;
   }

   public String getJdbcUrl() {
      return this.jdbcUrl;
   }

   public void setJdbcUrl(String text) {
      this.checkIfSealed();
      this.jdbcUrl = text;
   }

   public boolean isAutoCommit() {
      return this.autoCommit;
   }

   public boolean isAllowPoolSuspension() {
      return this.allowPoolSuspension;
   }

   public long getInitializationFailTimeout() {
      return this.initializationFailTimeout;
   }

   public void setInitializationFailTimeout(long value) {
      this.checkIfSealed();
      this.initializationFailTimeout = value;
   }

   public boolean isIsolateInternalQueries() {
      return this.isolateInternalQueries;
   }

   public long getKeepaliveTime() {
      return this.keepaliveTime;
   }

   public void setKeepaliveTime(long value) {
      this.keepaliveTime = value;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public boolean isRegisterMbeans() {
      return this.registerMbeans;
   }

   @Override
   public String getPoolName() {
      return this.poolName;
   }

   public void setPoolName(String text) {
      this.checkIfSealed();
      this.poolName = text;
   }

   public ScheduledExecutorService getScheduledExecutor() {
      return this.scheduledExecutor;
   }

   public String getTransactionIsolation() {
      return this.transactionIsolationName;
   }

   public String getSchema() {
      return this.schema;
   }

   public String getExceptionOverrideClassName() {
      return this.exceptionOverrideClassName;
   }

   public ThreadFactory getThreadFactory() {
      return this.threadFactory;
   }

   public void handleThreadFactory(ThreadFactory threadFactory) {
      this.checkIfSealed();
      this.threadFactory = threadFactory;
   }

   void seal() {
      this.sealed = true;
   }

   public void copyStateTo(HikariConfig hikariConfig) {
      for (Field field : HikariConfig.class.getDeclaredFields()) {
         if (!Modifier.isFinal(field.getModifiers())) {
            field.setAccessible(true);

            try {
               field.set(hikariConfig, field.get(this));
            } catch (Exception e) {
               throw new RuntimeException("Failed to copy HikariConfig state: " + e.getMessage(), e);
            }
         }
      }

      hikariConfig.sealed = false;
   }

   public void validatePoolName() {
      if (this.poolName == null) {
         this.poolName = this.generatePoolName();
      } else if (this.registerMbeans && this.poolName.contains(":")) {
         throw new IllegalArgumentException("poolName cannot contain ':' when used with JMX");
      }

      this.catalog = TransactionIsolation.computeText(this.catalog);
      this.connectionInitSql = TransactionIsolation.computeText(this.connectionInitSql);
      this.connectionTestQuery = TransactionIsolation.computeText(this.connectionTestQuery);
      this.transactionIsolationName = TransactionIsolation.computeText(this.transactionIsolationName);
      this.dataSourceClassName = TransactionIsolation.computeText(this.dataSourceClassName);
      this.dataSourceJndiName = TransactionIsolation.computeText(this.dataSourceJndiName);
      this.driverClassName = TransactionIsolation.computeText(this.driverClassName);
      this.jdbcUrl = TransactionIsolation.computeText(this.jdbcUrl);
      if (this.dataSource != null) {
         if (this.dataSourceClassName != null) {
            LOGGER.handleText4("%s - using dataSource and ignoring dataSourceClassName.", new Object[]{this.poolName});
         }
      } else if (this.dataSourceClassName != null) {
         if (this.driverClassName != null) {
            LOGGER.handleText2("%s - cannot use driverClassName and dataSourceClassName together.", new Object[]{this.poolName});
            throw new IllegalStateException("cannot use driverClassName and dataSourceClassName together.");
         }

         if (this.jdbcUrl != null) {
            LOGGER.handleText4("{} - using dataSourceClassName and ignoring jdbcUrl.", new Object[]{this.poolName});
         }
      } else if (this.jdbcUrl == null && this.dataSourceJndiName == null) {
         if (this.driverClassName != null) {
            LOGGER.handleText2("{} - jdbcUrl is required with driverClassName.", new Object[]{this.poolName});
            throw new IllegalArgumentException("jdbcUrl is required with driverClassName.");
         }

         LOGGER.handleText2("{} - dataSource or dataSourceClassName or jdbcUrl is required.", new Object[]{this.poolName});
         throw new IllegalArgumentException("dataSource or dataSourceClassName or jdbcUrl is required.");
      }

      this.validateMaxLifetime();
      if (LOGGER.obtainFlag() || unitTest) {
         this.logConfiguration();
      }
   }

   private final void validateMaxLifetime() {
      if (this.maxLifetime != 0L && this.maxLifetime < TimeUnit.SECONDS.toMillis(30L)) {
         LOGGER.handleText4("%s - maxLifetime is less than 30000ms, setting to default %dms.", new Object[]{this.poolName, MAX_LIFETIME});
         this.maxLifetime = MAX_LIFETIME;
      }

      if (this.keepaliveTime != 0L && this.keepaliveTime < TimeUnit.SECONDS.toMillis(30L)) {
         LOGGER.handleText4("%s - keepaliveTime is less than 30000ms, disabling it.", new Object[]{this.poolName});
         this.keepaliveTime = 0L;
      }

      if (this.keepaliveTime != 0L && this.maxLifetime != 0L && this.keepaliveTime >= this.maxLifetime) {
         LOGGER.handleText4("%s - keepaliveTime is greater than or equal to maxLifetime, disabling it.", new Object[]{this.poolName});
         this.keepaliveTime = 0L;
      }

      if (this.leakDetectionThreshold > 0L && !unitTest && (this.leakDetectionThreshold < TimeUnit.SECONDS.toMillis(2L) || this.leakDetectionThreshold > this.maxLifetime && this.maxLifetime > 0L)) {
         LOGGER.handleText4("%s - leakDetectionThreshold is less than 2000ms or more than maxLifetime, disabling it.", new Object[]{this.poolName});
         this.leakDetectionThreshold = 0L;
      }

      if (this.connectionTimeout < SOFT_TIMEOUT_FLOOR) {
         LOGGER.handleText4("%s - connectionTimeout is less than %dms, setting to %dms.", new Object[]{this.poolName, SOFT_TIMEOUT_FLOOR, CONNECTION_TIMEOUT});
         this.connectionTimeout = CONNECTION_TIMEOUT;
      }

      if (this.validationTimeout < SOFT_TIMEOUT_FLOOR) {
         LOGGER.handleText4("%s - validationTimeout is less than %dms, setting to %dms.", new Object[]{this.poolName, SOFT_TIMEOUT_FLOOR, VALIDATION_TIMEOUT});
         this.validationTimeout = VALIDATION_TIMEOUT;
      }

      if (this.maximumPoolSize < 1) {
         this.maximumPoolSize = 10;
      }

      if (this.minimumIdle < 0 || this.minimumIdle > this.maximumPoolSize) {
         this.minimumIdle = this.maximumPoolSize;
      }

      if (this.idleTimeout + TimeUnit.SECONDS.toMillis(1L) > this.maxLifetime && this.maxLifetime > 0L && this.minimumIdle < this.maximumPoolSize) {
         LOGGER.handleText4("%s - idleTimeout is close to or more than maxLifetime, disabling it.", new Object[]{this.poolName});
         this.idleTimeout = 0L;
      } else if (this.idleTimeout != 0L && this.idleTimeout < TimeUnit.SECONDS.toMillis(10L) && this.minimumIdle < this.maximumPoolSize) {
         LOGGER.handleText4("%s - idleTimeout is less than 10000ms, setting to default {}ms.", new Object[]{this.poolName, IDLE_TIMEOUT});
         this.idleTimeout = IDLE_TIMEOUT;
      } else if (this.idleTimeout != IDLE_TIMEOUT && this.idleTimeout != 0L && this.minimumIdle == this.maximumPoolSize) {
         LOGGER.handleText4("%s - idleTimeout has been set but has no effect because the pool is operating as a fixed size pool.", new Object[]{this.poolName});
      }
   }

   private final void checkIfSealed() {
      if (this.sealed) {
         throw new IllegalStateException("The configuration of the pool is sealed once started. Use HikariConfigMXBean for runtime changes.");
      }
   }

   private final void logConfiguration() {
      LOGGER.handleText("%s - configuration:", new Object[]{this.poolName});

      for (String text : (Iterable<String>) new TreeSet(PropertyElf.computeSet(HikariConfig.class))) {
         try {
            Object obj = PropertyElf.computeObj(text, this);
            if ("dataSourceProperties".equals(text)) {
               Properties properties = PropertyElf.computeProperties(this.dataSourceProperties);
               properties.setProperty("password", "<masked>");
               obj = properties;
            }

            if ("initializationFailTimeout".equals(text) && this.initializationFailTimeout == Long.MAX_VALUE) {
               obj = "infinite";
            } else if ("transactionIsolation".equals(text) && this.transactionIsolationName == null) {
               obj = "default";
            } else if (text.matches("scheduledExecutorService|threadFactory") && obj == null) {
               obj = "internal";
            } else if (text.contains("jdbcUrl") && obj instanceof String) {
               obj = ((String)obj).replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
            } else if (text.contains("password")) {
               obj = "<masked>";
            } else if (obj instanceof String) {
               obj = "\"" + obj + "\"";
            } else if (obj == null) {
               obj = "none";
            }

            LOGGER.handleText("{}{}", new Object[]{text + "................................................".substring(0, 32), obj});
         } catch (Exception e) {
         }
      }
   }

   private final void loadFromPropertiesFile(String text) {
      File file = new File(text);

      try (InputStream inputStream = file.isFile() ? new FileInputStream(file) : this.getClass().getResourceAsStream(text)) {
         if (inputStream == null) {
            throw new IllegalArgumentException("Cannot find property file: " + text);
         }

         Properties properties = new Properties();
         properties.load(inputStream);
         PropertyElf.handleObj(this, properties);
      } catch (IOException e) {
         throw new RuntimeException("Failed to read property file", e);
      }
   }

   private final String generatePoolName() {

      try {
         synchronized (System.getProperties()) {
            String text = String.valueOf(Integer.getInteger("com.zaxxer.hikari.pool_number", 0) + 1);
            System.setProperty("com.zaxxer.hikari.pool_number", text);
            return "HikariPool-" + text;
         }
      } catch (AccessControlException e) {
         ThreadLocalRandom current2 = ThreadLocalRandom.current();
         StringBuilder stringBuilder = new StringBuilder("HikariPool-");

         for (int i = 0; i < 4; i++) {
            stringBuilder.append(ID_CHARACTERS[current2.nextInt(62)]);
         }

         LOGGER.handleText3("assigned random pool name '{}' (security manager prevented access to system properties)", new Object[]{stringBuilder});
         return stringBuilder.toString();
      }
   }
}
