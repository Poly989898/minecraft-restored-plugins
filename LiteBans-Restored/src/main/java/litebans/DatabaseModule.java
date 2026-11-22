package litebans;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipException;
import litebans.api.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DatabaseModule extends PluginModule {
   public static final DatabaseType DATABASE_TYPE = new DatabaseType(null);
   private HikariDataSource dataSource;
   private final ReentrantLock reentrantLock = new ReentrantLock();
   private final Lazy configModule = KotlinStub5.computeLazy(() -> (ConfigModule)this.platform.getModule(ConfigModule.class));
   private final Lazy broadcastModule = KotlinStub5.computeLazy(() -> (BroadcastModule)this.platform.getModule(BroadcastModule.class));
   private QueryExecutor queryExecutor;
   private UserRecord serverRecord;
   private HashMap serverRecords;
   private AtomicLong atomicLong;
   private final AtomicBoolean initialized;
   private final AtomicBoolean atomicBoolean5;
   private final AtomicBoolean transientErrorSeen;
   private boolean convertSqlite;
   private boolean closing;
   private boolean disclaimerAccepted;
   private byte byteValue2;
   private long longValue2;
   private long lastExpiredUpdate;
   private int intValue3;
   private String currentTime;
   private int databaseTimeOffset;
   public static final int RETRY_DELAY_MILLIS = 3000;
   private static final String PROXY_STATEMENT_FRAME = "at " + ProxyPreparedStatement.class.getName() + ".executeQuery(";
   private static final String HIKARI_PROXY_STATEMENT_FRAME = "at " + HikariProxyPreparedStatement.class.getName() + ".executeQuery(";
   private static final String PREPARED_QUERY_FRAME = "at " + PreparedQuery.class.getName() + '.';

   public DatabaseModule(@NotNull Platform platform) {
      super(platform);
      this.queryExecutor = new QueryExecutor(platform);
      this.serverRecords = new HashMap();
      this.atomicLong = new AtomicLong(0L);
      this.initialized = new AtomicBoolean();
      this.atomicBoolean5 = new AtomicBoolean();
      this.transientErrorSeen = new AtomicBoolean();
      this.convertSqlite = true;
      this.currentTime = "UTC";
   }

   public final HikariDataSource getDataSource() {
      return this.dataSource;
   }

   public final ConfigModule getConfigModule() {
      Lazy lazy = this.configModule;
      return (ConfigModule)lazy.getValue();
   }

   public final BroadcastModule getBroadcastModule() {
      Lazy lazy = this.broadcastModule;
      return (BroadcastModule)lazy.getValue();
   }

   public final UserRecord getServerRecord() {
      return this.serverRecord;
   }

   public final String getServerName() {
      if (this.serverRecord == null) {
         return this.getConfigModule().getSettings().getServerName();
      }

      UserRecord userRecord = this.serverRecord;
      return userRecord.toString();
   }

   public final HashMap getServerRecords() {
      return this.serverRecords;
   }

   public final boolean isConnected() {
      return (this.dataSource != null ? !this.dataSource.obtainFlag() : false) && this.initialized.get();
   }

   public final void setConvertSqlite(boolean flag) {
      this.convertSqlite = flag;
   }

   public final void setClosing(boolean flag) {
      this.closing = flag;
   }

   public final boolean isDisclaimerAccepted() {
      return this.disclaimerAccepted;
   }

   public final void setDisclaimerAccepted(boolean flag) {
      this.disclaimerAccepted = flag;
   }

   public final byte getByteValue2() {
      return this.byteValue2;
   }

   public final void setByteValue2(byte b) {
      this.byteValue2 = b;
   }

   public final long getLongValue2() {
      return this.longValue2;
   }

   public final void setLongValue2(long value) {
      this.longValue2 = value;
   }

   public final long getLastExpiredUpdate() {
      return this.lastExpiredUpdate;
   }

   public final void setLastExpiredUpdate(long value) {
      this.lastExpiredUpdate = value;
   }

   public final int getIntValue3() {
      return this.intValue3;
   }

   public final String getCurrentTime() {
      return this.currentTime;
   }

   public final void setCurrentTime(@NotNull String text) {
      this.currentTime = text;
   }

   public final int getDatabaseTimeOffset() {
      return this.databaseTimeOffset;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Connection getRawConnection() {
      long value = System.nanoTime();
      Connection connection = null;
      boolean flag = false /* VF: Semaphore variable */;

      label87: {
         try {
            Connection connection2;
            label71: {
               flag = true;
               HikariDataSource hikariDataSource = this.dataSource;
               if (hikariDataSource != null) {
                  if (!hikariDataSource.obtainFlag()) {
                     connection2 = hikariDataSource.getConnection();
                     break label71;
                  }
               }

               connection2 = null;
            }

            connection = connection2;
            flag = false;
            break label87;
         } catch (SQLException e) {
            this.logConnectionError(e);
            flag = false;
         } finally {
            if (flag) {
               long value2 = System.nanoTime() - value;
               if (value2 > 1000000000L) {
               }
            }
         }

         long value3 = System.nanoTime() - value;
         if (value3 > 1000000000L) {
         }

         return connection;
      }

      long value4 = System.nanoTime() - value;
      if (value4 > 1000000000L && connection != null && !this.isPoolHealthy()) {
         int int2 = (int)(value4 / 1.0E9);
         if (int2 > 12) {
            this.platform.getLogger().warning("Database is slow! Establishing connection took " + int2 + " seconds.");
         }
      }

      return connection;
   }

   public final String computeText(@NotNull LiteSender liteSender) {
      byte platformId = this.platform.getPlatformId();
      String text;
      if (platformId != 0 && platformId != 3) {
         if (platformId != 1 && platformId != 2) {
            StandardKt.notImplemented();
            throw new CommandAbortException();
         }

         text = liteSender.getServerName();
      } else {
         text = null;
      }

      return text;
   }

   public final boolean isSyncEnabled() {
      return this.getConfigModule().getSettings().isSync() && (this.serverRecord != null ? this.serverRecord.obtainFlag() : false);
   }

   public final boolean initializeSchema(@NotNull DatabaseSession databaseSession, @Nullable SchemaBuilder schemaBuilder, boolean flag, boolean flag2) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      File dataFolder = this.platform.getDataFolder();
      String text = ".version";
      File file = new File(dataFolder, text);
      String text2 = String.valueOf(getCurrentTimeWithDefaults(this, false, 1, null));
      boolean flag3 = false;
      boolean flag4 = !file.exists();

      try {
         File file2 = FileUtils.computeFile(file);
         short value = 128;
         Charset charset = KotlinCharsets.CHARSET;
         Reader inputStreamReader = new InputStreamReader(new FileInputStream(file2), charset);
         var bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader)inputStreamReader : new BufferedReader(inputStreamReader, value);
         Throwable error = null;

         try {
            String line = bufferedReader.readLine();
            Settings settings = module.getSettings();

            try {
               MessageDigest instance = MessageDigest.getInstance("SHA-512");
               if (instance != null) {
                  instance.reset();
                  String[] texts = new String[]{BuildInfoUtil.obtainText3(), settings.getSqlDriver(), settings.getSqlAddress(), settings.getSqlUsername(), settings.getSqlPassword(), settings.getSqlDatabase(), settings.getSqlTablePrefix()};
                  String joinToString = ArraysKt.joinToStringWithDefaults(texts, "\u0001", null, null, 0, null, null, 62, null);
                  byte[] digest2 = instance.digest(joinToString.getBytes(KotlinCharsets.CHARSET));
                  instance.reset();
                  text2 = LiteHelper9.computeText(digest2, false);
               }
            } catch (NoSuchAlgorithmException e) {
               ConfigModule.handleConfigModule(module, e, 0, 2, null);
               CommandArguments commandArguments;
               Intrinsics.checkNotNull(module);
               String message = e.getMessage();
               if (message == null) {
                  message = "";
               }

               String text3 = message;
               String[] texts2;
               if (Intrinsics.areEqual(text3, "sort")) {
                  String[] texts3 = new String[]{"00"};
                  texts2 = texts3;
               } else {
                  String[] texts4 = new String[]{"01:" + text3};
                  texts2 = texts4;
               }

               String[] texts5 = texts2;
               commandArguments = new CommandArguments(module, Arrays.copyOf(texts5, texts5.length));
               commandArguments.obtainIntValue();
            }

            if (Intrinsics.areEqual(text2, line) && !flag) {
               module.debug(2, "Table upgrade checks skipped.");
               return true;
            }

         } catch (Throwable e2) {
            error = e2;
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         } finally {
            CloseableKt.handleCloseable(bufferedReader, error);
         }
      } catch (Exception e3) {
         this.logConnectionError(e3);
         flag3 = true;
      }

      if (schemaBuilder != null) {
         if (!databaseSession.isOpen()) {
            String text4 = "Connection is closed. Only open connections can be used to upgrade tables.";
            throw new IllegalArgumentException(text4.toString());
         }

         try {
            Intrinsics.checkNotNullExpressionValue(databaseSession, "");
            DatabaseSessionImpl databaseSession2 = (DatabaseSessionImpl)databaseSession;
            Intrinsics.checkNotNull(module);
            databaseSession2.handleFlag(flag2, module, schemaBuilder, flag4);
         } catch (Throwable e4) {
            this.logConnectionError(e4);
            flag3 = true;
         }

         if (!flag3) {
            try {
               KotlinStub17.handleFile(file, text2, null, 2, null);
            } catch (Throwable e5) {
               this.logConnectionError(e5);
            }
         }
      }

      return false;
   }

   public static boolean initializeSchemaWithDefaults(DatabaseModule databaseModule, DatabaseSession databaseSession, SchemaBuilder schemaBuilder, boolean flag, boolean flag2, int value, Object obj) {
      if ((value & 4) != 0) {
         flag = false;
      }

      if ((value & 8) != 0) {
         flag2 = false;
      }

      return databaseModule.initializeSchema(databaseSession, schemaBuilder, flag, flag2);
   }

   @Override
   public void disable() {
      try {
         Lock lock2 = this.reentrantLock;
         lock2.lock();

         try {
            CommandQueueModule module = (CommandQueueModule)this.platform.getModule(CommandQueueModule.class);
            if (!module.getArrayBlockingQueue().isEmpty()) {

               try {
                  DatabaseSession session = this.openSession();
                  Throwable error = null;

                  try {
                     Iterable arrayBlockingQueue = module.getArrayBlockingQueue();

                     for (Object obj : arrayBlockingQueue) {
                        PlayerInfo playerInfo = (PlayerInfo)obj;
                        PunishmentQueries.saveLoginWithDefaults(session, playerInfo.getName(), playerInfo.getUuid(), playerInfo.getIp(), false, false, 24, null);
                     }

                  } catch (Throwable e2) {
                     error = e2;
                     throw SneakyThrow.<RuntimeException>rethrow(e2);
                  } finally {
                     CloseableKt.handleCloseable(session, error);
                  }
               } catch (Throwable e3) {
                  if (!(e3 instanceof SQLException)) {
                     throw SneakyThrow.<RuntimeException>rethrow(e3);
                  }
                  SQLException sqlException = (SQLException) e3;
                  if (!this.isConnectionFailure(sqlException)) {
                     throw sqlException;
                  }
               }
            }

            AtomicBoolean atomicBoolean = this.initialized;
            atomicBoolean.set(false);
            if (this.dataSource != null) {
               this.dataSource.close();
            }

            this.dataSource = null;
         } finally {
            lock2.unlock();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final ResultSet executeQuery(@NotNull PreparedQuery preparedQuery) {
      return this.queryExecutor.computeResultSet(preparedQuery);
   }

   public final int[] executeBatch(@NotNull Statement statement) {
      return this.queryExecutor.computeInts(statement);
   }

   public final ResultSet executeQueryKeepOpen(@NotNull PreparedQuery preparedQuery) {
      return this.queryExecutor.computeResultSet2(preparedQuery);
   }

   public final int executeUpdate(@NotNull PreparedQuery preparedQuery) {
      return this.queryExecutor.computeIntValue(preparedQuery);
   }

   public final ProxyResultSet execute(@NotNull PreparedQuery preparedQuery) {
      this.queryExecutor.computeIntValue(preparedQuery);
      return ResultSetUtil.obtainProxyResultSet();
   }

   public final List getDriverDefinitions(@NotNull ConfigModule configModule) {
      Settings settings = configModule.getSettings();
      String[] sqlDrivers = settings.getSqlDrivers();
      var arrayList = new ArrayList(sqlDrivers.length);
      int value = 0;

      for (int i = sqlDrivers.length; value < i; value++) {
         String text = sqlDrivers[value];
         CharSequence message = text;
         Regex regex = new Regex(":(?!(//))");
         byte b = 5;
         Collection collection = regex.split(message, b);
         String[] array = (String[])collection.toArray(new String[0]);
         String orNull = (String)ArraysKt.getOrNull(array, 2);
         String orNull2 = (String)ArraysKt.getOrNull(array, 3);
         if (orNull2 != null) {
            orNull = orNull != null ? StringsKt.computeText(orNull, "{}", orNull2, false, 4, null) : null;
         }

         String orNull3 = (String)ArraysKt.getOrNull(array, 4);
         String text2 = array[0];
         arrayList.add(new DriverDefinition(text2.toLowerCase(Locale.ENGLISH), array[1], orNull, orNull2, orNull3));
      }

      return arrayList;
   }

   public final UserRecord getServerRecordById(@NotNull DatabaseSession databaseSession, int value, boolean flag) {
      UserRecord userRecord = (UserRecord)this.serverRecords.get(value);
      if (userRecord == null && flag) {
         updateServerRecordWithDefaults(this, databaseSession, false, 2, null);
         return this.getServerRecordById(databaseSession, value, false);
      } else {
         return userRecord;
      }
   }

   public final boolean isConnectionAvailable(int value) {
      int sqlPoolMaxConnections = this.getConfigModule().getSettings().getSqlPoolMaxConnections();
      HikariDataSource dataSource = this.getDataSource();
      if (dataSource != null) {
         HikariPoolMXBean poolBInterface = dataSource.obtainHikariPoolMXBean();
         if (poolBInterface != null) {
            return poolBInterface.getActiveConnections() >= sqlPoolMaxConnections - value && sqlPoolMaxConnections >= 3;
         }
      }

      return 0 >= sqlPoolMaxConnections - value && sqlPoolMaxConnections >= 3;
   }

   public static boolean isConnectionAvailableWithDefaults(DatabaseModule databaseModule, int value, int value2, Object obj) {
      if ((value2 & 1) != 0) {
         value = 1;
      }

      return databaseModule.isConnectionAvailable(value);
   }

   public final void logConnectionError(@NotNull Throwable error) {
      try {
         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         String message = error.getMessage();
         if (message == null) {
            message = "";
         }

         String text = message;
         if (error instanceof ChecksumMismatchException) {
            PluginModule pluginModule = this;
            pluginModule.platform.getLogger().severe(text);
            return;
         }

         if (!this.platform.isEnabled()) {
            if (error instanceof SQLException && StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"Interrupted during connection acquisition", false, 2, null)) {
               return;
            }

            error.printStackTrace();
            return;
         }

         if (error instanceof VirtualMachineError) {
            throw error;
         }

         StringWriter stringWriter = new StringWriter();
         error.printStackTrace(new PrintWriter(stringWriter));
         String string = stringWriter.toString();
         boolean flag = false;
         if (error instanceof SQLException) {
            Throwable error2 = error;
            int value = 4;

            while (value-- > 0) {
               Throwable cause = error2.getCause();
               if (cause == null) {
                  break;
               }

               error2 = cause;
            }

            if (!(error2 instanceof SocketTimeoutException) && (!(error instanceof SQLTransientConnectionException) || !this.transientErrorSeen.get())) {
               PluginModule pluginModule2 = this;
               String text2 = "An unexpected database error has occurred.";
               pluginModule2.platform.getLogger().severe(text2);
            } else {
               this.transientErrorSeen.set(true);
               flag = true;
               PluginModule pluginModule3 = this;
               String text3 = "Unfortunately, a request has failed because your database server did not respond within "
                  + NumberFormats.computeText(module.getSettings().getSqlPoolTimeout() / 1000.0)
                  + " seconds. (Socket timeout)";
               pluginModule3.platform.getLogger().severe(text3);
            }

            String sqlDriver = module.getSettings().getSqlDriver();
            Throwable error3 = this.platform.getConfigFile().getError();
            int sqlPoolMaxConnections = module.getSettings().getSqlPoolMaxConnections();
            if (sqlPoolMaxConnections < 5) {
               PluginModule pluginModule4 = this;
               String text4 = "max_connections in config.yml is smaller than 5 (max="
                  + sqlPoolMaxConnections
                  + ")! This can cause connection pool exhaustion (\"Connection is not available, request timed out ...\").";
               pluginModule4.platform.getLogger().warning(text4);
            }

            if (error instanceof SQLTransientConnectionException && !this.transientErrorSeen.get() && this.isPoolHealthy()) {
               this.perform3();
            }

            if (StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"Unknown column '", false, 2, null)
                  && StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"' in '", false, 2, null)
               || StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"Column '", false, 2, null)
                  && StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"' not found.", false, 2, null)
               || StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)": Column \"", false, 2, null)
                  && StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"\" not found", false, 2, null)) {
               this.warnAccessDeniedOnce();
            }

            if (StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"ALTER command denied to user ", false, 2, null)) {
               this.warnMissingAlterPermission();
               return;
            }

            if (error3 != null) {
               PluginModule pluginModule5 = this;
               String text5 = "WARNING: config.yml was not loaded successfully, errors were encountered. Check startup logs for more information.";
               pluginModule5.platform.getLogger().severe(text5);
            } else if (Intrinsics.areEqual(sqlDriver, "h2") && StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"The database is read only", false, 2, null)) {
               PluginModule pluginModule6 = this;
               String text6 = "[!!!] Database is read-only. This indicates that LiteBans does not have write access to its own database!";
               pluginModule6.platform.getLogger().severe(text6);
            }
         }

         if (flag && !module.isDebugAtLeast(1)) {
            return;
         }

         module.handleText(string);
      } catch (Throwable e) {
         PluginModule pluginModule7 = this;
         String text7 = "Exception in exception handler";
         pluginModule7.platform.getLogger().severe(text7);
         e.printStackTrace();
         PluginModule pluginModule8 = this;
         String text8 = "Original exception:";
         pluginModule8.platform.getLogger().severe(text8);
         error.printStackTrace();
      }
   }

   private final boolean isPoolHealthy() {
      HikariDataSource dataSource = this.getDataSource();
      if (dataSource != null) {
         HikariPoolMXBean poolBInterface = dataSource.obtainHikariPoolMXBean();
         if (poolBInterface != null) {
            return poolBInterface.getActiveConnections() >= this.getConfigModule().getSettings().getSqlPoolMaxConnections();
         }
      }

      return 0 >= this.getConfigModule().getSettings().getSqlPoolMaxConnections();
   }

   private final void warnUseSslDisabled() {
      if (StringsKt.containsWithDefaults((CharSequence)this.getConfigModule().getSettings().getA__f(), (CharSequence)"useSSL=false", false, 2, null)) {
         PluginModule pluginModule = this;
         String text = "Database SSL connection support is disabled. Enabling SSL should fix this error.";
         pluginModule.platform.getLogger().severe(text);
         PluginModule pluginModule2 = this;
         String text2 = "Change \"useSSL=false\" to \"useSSL=true\" in config.yml, and then run \"litebans reload\".";
         pluginModule2.platform.getLogger().severe(text2);
      }
   }

   private final void warnMissingAlterPermission() {
      PluginModule pluginModule = this;
      String text = "Your database user (" + this.getConfigModule().getSettings().getSqlUsername() + ") is lacking ALTER permission for the " + this.getConfigModule().getSettings().getSqlDatabase() + " database!";
      pluginModule.platform.getLogger().severe(text);
      PluginModule pluginModule2 = this;
      String text2 = "Without this permission, LiteBans cannot upgrade the database.";
      pluginModule2.platform.getLogger().severe(text2);
      PluginModule pluginModule3 = this;
      String text3 = "Please add the necessary permissions!";
      pluginModule3.platform.getLogger().severe(text3);
      PluginModule pluginModule4 = this;
      String text4 = "The required permissions are SELECT, CREATE, INSERT, UPDATE, DELETE, ALTER, DROP.";
      pluginModule4.platform.getLogger().severe(text4);
   }

   private final void warnAccessDeniedOnce() {
      AtomicBoolean atomicBoolean = this.atomicBoolean5;
      if (atomicBoolean.compareAndSet(false, true)) {
         PluginModule pluginModule = this;
         String text = "Executing full upgrade check in response to 'Unknown column' exception.";
         pluginModule.platform.getLogger().warning(text);
         LiteBansCommand liteBansCommand = (LiteBansCommand)this.platform.getCommandManager().computeLiteCommand("litebans");
         if (liteBansCommand != null) {
            liteBansCommand.handleLiteSender2(this.platform.getConsole());
         }
      } else {
         PluginModule pluginModule2 = this;
         String text2 = "[!!!] An 'unknown column' exception likely indicates that tables could not be upgraded successfully - consider the following exception as a consequence of a startup error, not the original cause.";
         pluginModule2.platform.getLogger().severe(text2);
      }
   }

   private final void perform3() {
      CommandQueueModule commandQueueModule;
      int value;
      int value2;
      label23: {
         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         commandQueueModule = (CommandQueueModule)this.platform.getModule(CommandQueueModule.class);
         value = module.getSettings().getSqlPoolMaxConnections();
         HikariDataSource dataSource = this.getDataSource();
         if (dataSource != null) {
            HikariPoolMXBean poolBInterface = dataSource.obtainHikariPoolMXBean();
            if (poolBInterface != null) {
               value2 = poolBInterface.getActiveConnections();
               break label23;
            }
         }

         value2 = 0;
      }

      int value3 = value2;
      Database database = Database.get();
      Intrinsics.checkNotNullExpressionValue(database, "");
      int value4 = ((KotlinDatabaseSub)database).obtainIntValue();
      Lazy lazy = KotlinStub5.computeLazy(() -> this.platform.createBridge().findPlugins(this.platform.getName()));
      int value5 = commandQueueModule.getArrayBlockingQueue().size();
      PluginModule pluginModule = this;
      String text = "**Connection pool exhaustion detected** (open=" + value3 + ", max=" + value + ", logins=" + value5 + ')';
      pluginModule.platform.getLogger().severe(text);
      if (value4 > 0) {
         PluginModule pluginModule2 = this;
         String text2 = "There have been " + value4 + " API calls to prepareStatement().";
         pluginModule2.platform.getLogger().severe(text2);
         PluginModule pluginModule3 = this;
         String text3 = "Be aware that plugins which use the LiteBans API incorrectly can create a connection leak, which inevitably leads to exhaustion after "
            + value
            + " connections have been used.";
         pluginModule3.platform.getLogger().severe(text3);
         if (!computeList(lazy).isEmpty()) {
            PluginModule pluginModule4 = this;
            String text4 = "Plugin(s) which may be using LiteBans API: [" + CollectionsKt.joinToStringWithDefaults(computeList(lazy), ", ", null, null, 0, null, null, 62, null) + ']';
            pluginModule4.platform.getLogger().severe(text4);
         }
      }
   }

   @Override
   public void enable() {
      Lock lock2 = this.reentrantLock;
      lock2.lock();

      try {
         this.getConfigModule().enable();

         try {
            if (this.closing) {
               return;
            }

            backupDatabaseWithDefaults(this, this.getConfigModule().getSettings().getSqlDriver(), 0L, 2, null);
            new ExpiryUpdateTask(this.platform).run();
         } catch (Throwable e) {
            this.logConnectionError(e);
         }

      } finally {
         lock2.unlock();
      }
   }

   public final Object loadDriver(@NotNull DriverDefinition namedRecord, boolean flag, boolean flag2) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      String text = namedRecord.getName();
      String text2 = "Loading: " + namedRecord;
      if (!access$initialized(this).get()) {
         PluginModule pluginModule = this;
         pluginModule.platform.getLogger().info(text2);
      }

      if (module.isVerbose()) {
         StringBuilder builder = new StringBuilder().append("> ");
         DriverDefinition namedRecord2 = namedRecord;
         String text3;
         if (namedRecord2.getUrl() == null) {
            text3 = namedRecord2.getName() + ".jar";
         } else {
            int lastIndexOf = StringsKt.lastIndexOfWithDefaults(namedRecord2.getUrl(), '/', 0, false, 6, null);
            if (lastIndexOf >= 0 && lastIndexOf <= namedRecord2.getUrl().length() - 1) {
               text3 = namedRecord2.getUrl().substring(lastIndexOf + 1);
            } else {
               text3 = namedRecord2.getName() + ".jar";
            }
         }

         module.logDebug(builder.append(text3).toString());
      }

      File dataFolder = this.platform.getDataFolder();
      String text4 = "lib";
      File file = FileUtils.computeFile3(new File(dataFolder, text4));
      DriverDefinition namedRecord3 = namedRecord;
      String text5;
      if (namedRecord3.getUrl() == null) {
         text5 = namedRecord3.getName() + ".jar";
      } else {
         int lastIndexOf2 = StringsKt.lastIndexOfWithDefaults(namedRecord3.getUrl(), '/', 0, false, 6, null);
         if (lastIndexOf2 >= 0 && lastIndexOf2 <= namedRecord3.getUrl().length() - 1) {
            text5 = namedRecord3.getUrl().substring(lastIndexOf2 + 1);
         } else {
            text5 = namedRecord3.getName() + ".jar";
         }
      }

      String text6 = text5;
      File file2 = new File(file, text6);

      try {
         Object obj = null;

         try {
            obj = this.downloadDriver(namedRecord, file2, flag2);
         } catch (ChecksumMismatchException e) {
            this.logConnectionError(e);
         }

         if (obj == null) {
            String text7 = namedRecord.getUrl();
            if (text7 != null) {
               String text8 = "Download URL for " + text + ": " + text7;
               if (!access$initialized(this).get()) {
                  PluginModule pluginModule2 = this;
                  pluginModule2.platform.getLogger().info(text8);
               }

               String text9 = "Downloading " + text + "...";
               if (!access$initialized(this).get()) {
                  PluginModule pluginModule3 = this;
                  pluginModule3.platform.getLogger().info(text9);
               }

               try {
                  FileDownloader.handleFileDownloader(ConfigModule.FILE_DOWNLOADER, file2.toString(), text7, null, null, 12, null);
               } catch (Exception e2) {
                  PluginModule pluginModule4 = this;
                  String text10 = "Failed to download " + text + '.';
                  pluginModule4.platform.getLogger().severe(text10);
                  this.logConnectionError(e2);
                  return null;
               }

               if (file2.exists()) {
                  String text11 = "Downloaded " + text + " successfully.";
                  if (!access$initialized(this).get()) {
                     PluginModule pluginModule5 = this;
                     pluginModule5.platform.getLogger().info(text11);
                  }
               } else {
                  PluginModule pluginModule6 = this;
                  String text12 = "Something happened.";
                  pluginModule6.platform.getLogger().warning(text12);
               }
            }
         }

         if (obj == null) {
            try {
               obj = this.downloadDriver(namedRecord, file2, flag2);
               if (obj == null) {
                  PluginModule pluginModule7 = this;
                  String text13 = "What happened?";
                  pluginModule7.platform.getLogger().severe(text13);
                  return null;
               }

               String text14 = "Loaded " + text + '!';
               if (!access$initialized(this).get()) {
                  PluginModule pluginModule8 = this;
                  pluginModule8.platform.getLogger().info(text14);
               }
            } catch (Throwable e3) {
               PluginModule pluginModule9 = this;
               String text15 = "Failed to load " + namedRecord + '.';
               pluginModule9.platform.getLogger().severe(text15);
               this.logConnectionError(e3);
               if (e3 instanceof ZipException && flag) {
                  PluginModule pluginModule10 = this;
                  String text16 = "Attempting to recover from this error...";
                  pluginModule10.platform.getLogger().severe(text16);
                  if (file2.exists() && file2.delete()) {
                     PluginModule pluginModule11 = this;
                     String text17 = "File removed: " + file2;
                     pluginModule11.platform.getLogger().severe(text17);
                  }

                  return loadDriverWithDefaults(this, namedRecord, false, false, 4, null);
               }

               return null;
            }
         }

         return obj;
      } catch (Exception e4) {
         String text18 = "Failed to load from " + file2 + ':';
         if (!access$initialized(this).get()) {
            PluginModule pluginModule12 = this;
            pluginModule12.platform.getLogger().info(text18);
         }

         this.logConnectionError(e4);
         return null;
      }
   }

   public static Object loadDriverWithDefaults(DatabaseModule databaseModule, DriverDefinition namedRecord, boolean flag, boolean flag2, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = true;
      }

      if ((value & 4) != 0) {
         flag2 = true;
      }

      return databaseModule.loadDriver(namedRecord, flag, flag2);
   }

   public final void updateServerRecord(@NotNull DatabaseSession databaseSession, boolean flag) {
      if (System.currentTimeMillis() - this.atomicLong.get() >= 3000L || flag) {
         ArrayList arrayList = new ArrayList();
         Settings settings = this.getConfigModule().getSettings();
         boolean flag2 = false;
         PunishmentTable punishmentTable = PunishmentTable.SERVERS;
         CharSequence message = "*";
         var prepare2 = databaseSession.prepare(SqlText.box(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable)));
         Throwable error = null;

         try {
            var executeQueryKeepOpen2 = this.executeQueryKeepOpen(prepare2);
            Throwable error2 = null;

            try {
               ResultSet resultSet = (ResultSet)executeQueryKeepOpen2;

               while (resultSet.next()) {
                  UserRecord userRecord = new UserRecord(UInt.constructorImpl((int)resultSet.getLong("id")), resultSet.getString("name"), resultSet.getString("uuid"), null);
                  arrayList.add(userRecord);
                  if (!flag2 && Intrinsics.areEqual(userRecord.getUuid(), settings.getServerUuid())) {
                     flag2 = true;
                     this.serverRecord = userRecord;
                     if (Integer.compareUnsigned(userRecord.getId(), 65000) >= 0) {
                        int id = userRecord.getId();
                        SyncModule module = (SyncModule)this.platform.getModule(SyncModule.class);
                        PluginModule pluginModule = this;
                        String text = "[Sync] Server limit reached (" + UInt.toStringImpl(id) + " servers). Resetting server information.";
                        pluginModule.platform.getLogger().severe(text);
                        PunishmentTable punishmentTable2 = PunishmentTable.SERVERS;
                        SqlUtil.computeIntValue(databaseSession.prepare(SqlText.box(SqlText.constructorImpl("DROP TABLE " + punishmentTable2))));
                        connectWithDefaults(this, 0L, false, false, false, 9, null);
                        SyncModule.sendSyncMessageWithDefaults(module, SyncMessageType.SERVERS_RELOAD, databaseSession, null, 65535, 4, null);
                        this.platform.runAsyncLater(new KotlinRunnableImpl3(this), this.getConfigModule().getSettings().getSyncPollTicks() * 3);
                        return;
                     }
                  }
               }

            } catch (Throwable e) {
               error2 = e;
               throw SneakyThrow.<RuntimeException>rethrow(e);
            } finally {
               KotlinUtil15.handleAutoCloseable(executeQueryKeepOpen2, error2);
            }

         } catch (Throwable e2) {
            error = e2;
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         } finally {
            CloseableKt.handleCloseable(prepare2, error);
         }

         this.serverRecords.clear();

         for (UserRecord userRecord2 : (Iterable<UserRecord>) arrayList) {
            this.serverRecords.put(userRecord2.getId(), userRecord2);
         }

         this.atomicLong.set(System.currentTimeMillis());
      }
   }

   public static void updateServerRecordWithDefaults(DatabaseModule databaseModule, DatabaseSession databaseSession, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      databaseModule.updateServerRecord(databaseSession, flag);
   }

   public final DatabaseSession openSession() {
      if (this.platform.isPrimaryThread() && this.platform.obtainFlag2()) {
         throw new AssertionError("Main server thread is not permitted to perform database queries.");
      }

      Connection rawConnection = this.getRawConnection();
      return rawConnection != null && this.isConnected() && !this.closing ? new DatabaseSessionImpl(this.platform, rawConnection) : new KotlinDatabaseSessionImpl(this.platform);
   }

   public final void handleFunction(@NotNull Function1 callback) {
      LiteSender liteSender = (LiteSender)CommandTask.COMMAND_OUTPUT.getSenderHolder().get();
      if (liteSender == null) {
         liteSender = this.platform.getConsole();
      }

      LiteSender liteSender2 = liteSender;
      this.platform.execute(() -> {
         CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
         Intrinsics.checkNotNull(liteSender2);

         try {
            commandOutput.getSenderHolder().set(liteSender2);

            try {

               try {
                  DatabaseSession session = this.openSession();
                  Throwable error = null;

                  try {
                     callback.computeObj(session);
                  } catch (Throwable e) {
                     error = e;
                     throw SneakyThrow.<RuntimeException>rethrow(e);
                  } finally {
                     CloseableKt.handleCloseable(session, error);
                  }
               } catch (Throwable e2) {
                  if (!(e2 instanceof SQLException)) {
                     throw SneakyThrow.<RuntimeException>rethrow(e2);
                  }
                  SQLException sqlException = (SQLException) e2;
                  if (!this.isConnectionFailure(sqlException)) {
                     throw sqlException;
                  }
               }
            } catch (ResultException e3) {
               CharSequence message = String.valueOf(e3.obtainObj());
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender2, message, null, 4, null);
            } catch (Throwable e4) {
               if (!(e4 instanceof SQLException)) {
                  throw SneakyThrow.<RuntimeException>rethrow(e4);
               }
               SQLException sqlException2 = (SQLException) e4;
               this.logConnectionError(sqlException2);
            }
         } finally {
            commandOutput.getSenderHolder().remove();
         }
      });
   }

   public final boolean isConnectionFailure(@NotNull Throwable error) {
      String message = error.getMessage();
      if (message == null) {
         message = "";
      }

      String text = message;
      Platform platform = this.platform;
      return (!platform.isEnabled() || platform.obtainFlag3())
         && (
            StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"Interrupted during connection", false, 2, null)
               || StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"SQLSTATE(08S01)", false, 2, null)
               || StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"Socket closed", false, 2, null)
               || StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"Connection is closed", false, 2, null)
               || StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"closed connection", false, 2, null)
         );
   }

   public final void connect(long value, boolean flag, boolean flag2, boolean flag3) {
      Settings settings = this.getConfigModule().getSettings();
      Connection rawConnection = this.getRawConnection();
      if (rawConnection != null) {
         Throwable error = null;

         try {
            Connection connection = (Connection)rawConnection;
            var statement = connection.createStatement();
            Throwable error2 = null;

            try {
               Statement statement2 = (Statement)statement;
               var databaseSession = new DatabaseSessionImpl(this.platform, connection);
               Throwable error3 = null;

               try {
                  SchemaBuilder schemaBuilder = databaseSession.createSchemaBuilder();
                  boolean flag4 = DATABASE_TYPE.checkText2(settings.getSqlDriver());
                  Intrinsics.checkNotNull(statement2);
                  if (flag) {
                     Iterable iterable = PunishmentTable.PUNISHMENT_TABLE_COMPANION.obtainList();
                     var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

                     for (Object obj : iterable) {
                        PunishmentTable punishmentTable = (PunishmentTable)obj;
                        arrayList.add(databaseSession.computeMessage(punishmentTable, schemaBuilder));
                     }


                     for (Object obj2 : arrayList) {
                        CharSequence charSequence = (CharSequence)obj2;
                        statement2.addBatch(charSequence.toString());
                     }
                  }

                  if (flag2) {
                     byte usernameMaxLength = databaseSession.getConfigModule().getSettings().getUsernameMaxLength();
                     PunishmentTable punishmentTable2 = PunishmentTable.HISTORY;
                     String[] texts = new String[6];
                     String text = "id";
                     texts[0] = Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "sqlite") ? text + " INTEGER PRIMARY KEY AUTOINCREMENT" : text + " SERIAL";
                     String text2 = "date";
                     String text3 = " DEFAULT CURRENT_TIMESTAMP";
                     texts[1] = text2 + " TIMESTAMP" + text3;
                     String text4 = "name";
                     int value2 = usernameMaxLength & 255;
                     String text5 = "";
                     String text6 = schemaBuilder.getColumnDefinition().getText7();
                     texts[2] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                        ? text4 + " VARCHAR(" + value2 + ')' + text6 + text5
                        : text4 + " VARCHAR(" + value2 + ')' + text5;
                     String text7 = "uuid";
                     byte b = 36;
                     String text8 = "";
                     String text9 = schemaBuilder.getColumnDefinition().getText7();
                     texts[3] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                        ? text7 + " VARCHAR(" + b + ')' + text9 + text8
                        : text7 + " VARCHAR(" + b + ')' + text8;
                     String text10 = "ip";
                     byte b2 = 45;
                     String text11 = "";
                     String text12 = schemaBuilder.getColumnDefinition().getText7();
                     texts[4] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                        ? text10 + " VARCHAR(" + b2 + ')' + text12 + text11
                        : text10 + " VARCHAR(" + b2 + ')' + text11;
                     String text13 = "id";
                     texts[5] = "PRIMARY KEY(" + text13 + ')';
                     SqlText sqlText = SqlText.box(schemaBuilder.computeText(punishmentTable2, texts));
                     statement2.addBatch(sqlText.toString());
                  }

                  if (flag3 && flag4) {
                     PunishmentTable punishmentTable3 = PunishmentTable.SERVERS;
                     String[] texts2 = new String[5];
                     String text14 = "id";
                     texts2[0] = Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "sqlite") ? text14 + " INTEGER PRIMARY KEY AUTOINCREMENT" : text14 + " SERIAL";
                     String text15 = "name";
                     byte b3 = 32;
                     String text16 = "";
                     String text17 = schemaBuilder.getColumnDefinition().getText7();
                     String text18 = text16 + " NOT NULL";
                     texts2[1] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                        ? text15 + " VARCHAR(" + b3 + ')' + text17 + text18
                        : text15 + " VARCHAR(" + b3 + ')' + text18;
                     String text19 = "uuid";
                     byte b4 = 32;
                     String text20 = "";
                     String text21 = schemaBuilder.getColumnDefinition().getText7();
                     String text22 = text20 + " NOT NULL";
                     texts2[2] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                        ? text19 + " VARCHAR(" + b4 + ')' + text21 + text22
                        : text19 + " VARCHAR(" + b4 + ')' + text22;
                     String text23 = "date";
                     String text24 = " DEFAULT CURRENT_TIMESTAMP";
                     texts2[3] = text23 + " TIMESTAMP" + text24;
                     String text25 = "id";
                     texts2[4] = "PRIMARY KEY(" + text25 + ')';
                     SqlText sqlText2 = SqlText.box(schemaBuilder.computeText(punishmentTable3, texts2));
                     statement2.addBatch(sqlText2.toString());
                     punishmentTable3 = PunishmentTable.SYNC;
                     String[] texts3 = new String[5];
                     String text26 = "id";
                     texts3[0] = Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "sqlite") ? text26 + " INTEGER PRIMARY KEY AUTOINCREMENT" : text26 + " SERIAL";
                     String text27 = "info";
                     texts3[1] = text27 + " INT" + schemaBuilder.getColumnDefinition().getText8() + " NOT NULL";
                     String text28 = "msg";
                     short value3 = 4096;
                     String text29 = "";
                     String text30 = schemaBuilder.getColumnDefinition().getText7();
                     String text31 = text29 + " NOT NULL";
                     texts3[2] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                        ? text28 + " VARCHAR(" + value3 + ')' + text30 + text31
                        : text28 + " VARCHAR(" + value3 + ')' + text31;
                     String text32 = "time";
                     String text33 = " DEFAULT CURRENT_TIMESTAMP";
                     texts3[3] = text32 + " TIMESTAMP" + text33;
                     String text34 = "id";
                     texts3[4] = "PRIMARY KEY(" + text34 + ')';
                     SqlText sqlText3 = SqlText.box(schemaBuilder.computeText(punishmentTable3, texts3));
                     statement2.addBatch(sqlText3.toString());
                  }

                  PunishmentTable punishmentTable4 = PunishmentTable.CACHE8K;
                  String[] texts4 = new String[3];
                  String text35 = "id";
                  String text36 = "";
                  String text37 = schemaBuilder.getColumnDefinition().getText5();
                  texts4[0] = !Intrinsics.areEqual(text37, "pgsql") && !Intrinsics.areEqual(text37, "h2")
                     ? text35 + " TINYINT" + schemaBuilder.getColumnDefinition().getText8() + " NOT NULL" + text36
                     : text35 + " SMALLINT NOT NULL" + text36;
                  String text38 = "b";
                  short value4 = 8192;
                  String text39;
                  if (Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "pgsql")) {
                     text39 = text38 + " BYTEA NOT NULL";
                  } else {
                     String text40 = "BLOB";
                     text39 = text38 + ' ' + text40 + '(' + value4 + ")NOT NULL";
                  }

                  texts4[1] = text39;
                  String text41 = "id";
                  texts4[2] = "PRIMARY KEY(" + text41 + ')';
                  SqlText sqlText4 = SqlText.box(schemaBuilder.computeText(punishmentTable4, texts4));
                  statement2.addBatch(sqlText4.toString());
                  punishmentTable4 = PunishmentTable.CONFIG;
                  String[] texts5 = new String[6];
                  String text42 = "id";
                  texts5[0] = Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "sqlite") ? text42 + " INTEGER PRIMARY KEY AUTOINCREMENT" : text42 + " SERIAL";
                  String text43 = "version";
                  short value5 = 128;
                  String text44 = "";
                  String text45 = schemaBuilder.getColumnDefinition().getText7();
                  String text46 = text44 + " NOT NULL";
                  texts5[1] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                     ? text43 + " VARCHAR(" + value5 + ')' + text45 + text46
                     : text43 + " VARCHAR(" + value5 + ')' + text46;
                  String text47 = "build";
                  short value6 = 128;
                  String text48 = "";
                  String text49 = schemaBuilder.getColumnDefinition().getText7();
                  String text50 = text48 + " NOT NULL";
                  texts5[2] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                     ? text47 + " VARCHAR(" + value6 + ')' + text49 + text50
                     : text47 + " VARCHAR(" + value6 + ')' + text50;
                  String text51 = "timezone";
                  byte b5 = 64;
                  String text52 = " DEFAULT '+00:00'";
                  String text53 = schemaBuilder.getColumnDefinition().getText7();
                  String text54 = text52 + " NOT NULL";
                  texts5[3] = DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5())
                     ? text51 + " VARCHAR(" + b5 + ')' + text53 + text54
                     : text51 + " VARCHAR(" + b5 + ')' + text54;
                  String text55 = "accept";
                  String text56 = " DEFAULT 0";
                  String text57 = schemaBuilder.getColumnDefinition().getText5();
                  texts5[4] = !Intrinsics.areEqual(text57, "pgsql") && !Intrinsics.areEqual(text57, "h2")
                     ? text55 + " TINYINT" + schemaBuilder.getColumnDefinition().getText8() + " NOT NULL" + text56
                     : text55 + " SMALLINT NOT NULL" + text56;
                  String text58 = "id";
                  texts5[5] = "PRIMARY KEY(" + text58 + ')';
                  SqlText sqlText5 = SqlText.box(schemaBuilder.computeText(punishmentTable4, texts5));
                  statement2.addBatch(sqlText5.toString());
                  punishmentTable4 = PunishmentTable.ALLOW;
                  String[] texts6 = new String[4];
                  String text59 = "id";
                  texts6[0] = Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "sqlite") ? text59 + " INTEGER PRIMARY KEY AUTOINCREMENT" : text59 + " SERIAL";
                  String text60 = "uuid";
                  byte b6 = 16;
                  if (Intrinsics.areEqual(schemaBuilder.getColumnDefinition().getText5(), "pgsql")) {
                     text39 = text60 + " BYTEA NOT NULL";
                  } else {
                     String text61 = "BINARY";
                     text39 = text60 + ' ' + text61 + '(' + b6 + ")NOT NULL";
                  }

                  texts6[1] = text39;
                  String text62 = "type";
                  String text63 = "";
                  String text64 = schemaBuilder.getColumnDefinition().getText5();
                  texts6[2] = !Intrinsics.areEqual(text64, "pgsql") && !Intrinsics.areEqual(text64, "h2")
                     ? text62 + " TINYINT" + schemaBuilder.getColumnDefinition().getText8() + " NOT NULL" + text63
                     : text62 + " SMALLINT NOT NULL" + text63;
                  String text65 = "id";
                  texts6[3] = "PRIMARY KEY(" + text65 + ')';
                  SqlText sqlText6 = SqlText.box(schemaBuilder.computeText(punishmentTable4, texts6));
                  statement2.addBatch(sqlText6.toString());

                  try {
                     int[] executeBatch2 = this.executeBatch(statement2);
                  } catch (Exception e) {
                     this.logConnectionError(e);
                  }

                  boolean initializeSchema = initializeSchemaWithDefaults(this, databaseSession, schemaBuilder, false, false, 12, null);
                  this.perform4();
                  databaseSession.getPlatform().runAsyncLater(() -> {
                     try {

                        try {
                           DatabaseSession session = this.openSession();
                           Throwable error4 = null;

                           try {
                              if (session.isOpen()) {
                                 Intrinsics.checkNotNullExpressionValue(session, "");
                                 var statement3 = ((DatabaseSessionImpl)session).createStatement();
                                 Throwable error22 = null;

                                 try {
                                    Statement statement22 = (Statement)statement3;
                                    if (!initializeSchema) {
                                       if (this.getIntValue3() <= 674) {
                                          KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
                                          kotlinSerializable.obj = "";
                                          if (DATABASE_TYPE.checkText(this.getConfigModule().getSettings().getSqlDriver())) {
                                             try {

                                                try {
                                                   DatabaseSession session2 = this.openSession();
                                                   Throwable error32 = null;

                                                   try {
                                                      ResultSet resultSet = SqlUtil.computeResultSet(session2.prepare("SELECT @@version"));
                                                      byte b7 = 1;
                                                      String text68;
                                                      if (resultSet.next()) {

                                                         String text210;
                                                         try {
                                                            text210 = resultSet.getString(b7);
                                                         } catch (SQLException e5) {
                                                            text210 = null;
                                                         }

                                                         text68 = text210;
                                                      } else {
                                                         text68 = null;
                                                      }

                                                      String text310 = text68;
                                                      if (text68 == null) {
                                                         text310 = "";
                                                      }

                                                      kotlinSerializable.obj = text310;
                                                   } catch (Throwable e22) {
                                                      error32 = e22;
                                                      throw SneakyThrow.<RuntimeException>rethrow(e22);
                                                   } finally {
                                                      CloseableKt.handleCloseable(session2, error32);
                                                   }
                                                } catch (Throwable e32) {
                                                   if (!(e32 instanceof SQLException)) {
                                                      throw SneakyThrow.<RuntimeException>rethrow(e32);
                                                   }
                                                   SQLException sqlException = (SQLException) e32;
                                                   if (!this.isConnectionFailure(sqlException)) {
                                                      throw sqlException;
                                                   }
                                                }
                                             } catch (Throwable e42) {
                                                if (!(e42 instanceof SQLException)) {
                                                   throw SneakyThrow.<RuntimeException>rethrow(e42);
                                                }
                                                SQLException sqlException2 = (SQLException) e42;
                                                this.logConnectionError(sqlException2);
                                             }
                                          }

                                          String[] texts7 = new String[]{"template", "ipban_wildcard", "uuid", "ip", "banned_by_uuid", "time", "until", "ipban", "active"};
                                          String[] texts22 = new String[]{"uuid", "name", "ip"};

                                          try {
                                             PunishmentTable[] punishmentTables = PunishmentTable.values();
                                             int value7 = 0;

                                             for (int i = punishmentTables.length; value7 < i; value7++) {
                                                PunishmentTable punishmentTable5 = punishmentTables[value7];
                                                if (punishmentTable5.obtainFlag()) {
                                                   access$createIndexes(this, texts7, statement22, punishmentTable5, (String)kotlinSerializable.obj);
                                                } else {
                                                   if (punishmentTable5 == PunishmentTable.HISTORY) {
                                                      access$createIndexes(this, texts22, statement22, punishmentTable5, (String)kotlinSerializable.obj);
                                                   }
                                                }
                                             }
                                          } catch (Throwable e52) {
                                             if (!(e52 instanceof SQLException)) {
                                                throw SneakyThrow.<RuntimeException>rethrow(e52);
                                             }
                                             SQLException sqlException3 = (SQLException) e52;
                                             ConfigModule.handleConfigModule(this.getConfigModule(), sqlException3, 0, 2, null);
                                          }
                                       }
                                    }

                                 } catch (Throwable e6) {
                                    error22 = e6;
                                    throw SneakyThrow.<RuntimeException>rethrow(e6);
                                 } finally {
                                    KotlinUtil15.handleAutoCloseable(statement3, error22);
                                 }
                              }

                           } catch (Throwable e7) {
                              error4 = e7;
                              throw SneakyThrow.<RuntimeException>rethrow(e7);
                           } finally {
                              CloseableKt.handleCloseable(session, error4);
                           }
                        } catch (Throwable e8) {
                           if (!(e8 instanceof SQLException)) {
                              throw SneakyThrow.<RuntimeException>rethrow(e8);
                           }
                           SQLException sqlException4 = (SQLException) e8;
                           if (!this.isConnectionFailure(sqlException4)) {
                              throw sqlException4;
                           }
                        }
                     } catch (Exception e9) {
                        this.logConnectionError(e9);
                     }

                     try {
                        if (!initializeSchema) {

                           try {
                              DatabaseSession session3 = this.openSession();
                              Throwable error42 = null;

                              try {
                                 session3.saveLogin("CONSOLE", "CONSOLE", "#", false, false);
                              } catch (Throwable e10) {
                                 error42 = e10;
                                 throw SneakyThrow.<RuntimeException>rethrow(e10);
                              } finally {
                                 CloseableKt.handleCloseable(session3, error42);
                              }
                           } catch (Throwable e11) {
                              if (!(e11 instanceof SQLException)) {
                                 throw SneakyThrow.<RuntimeException>rethrow(e11);
                              }
                              SQLException sqlException5 = (SQLException) e11;
                              if (!this.isConnectionFailure(sqlException5)) {
                                 throw sqlException5;
                              }
                           }
                        }
                     } catch (Throwable e12) {
                        this.logConnectionError(e12);
                     }
                  }, 200L);
                  this.serverRecord = null;
                  if (flag4 && settings.isSync()) {
                     registerServerWithDefaults(this, databaseSession, 0L, true, 2, null);
                  }

                  String text66 = NumberFormats.computeText((System.nanoTime() - value) / 1000.0 / 1000.0);
                  PluginModule pluginModule = this;
                  String text67 = "Database connection fully initialized (" + text66 + " ms).";
                  pluginModule.platform.getLogger().info(text67);
               } catch (Throwable e2) {
                  error3 = e2;
                  throw SneakyThrow.<RuntimeException>rethrow(e2);
               } finally {
                  CloseableKt.handleCloseable(databaseSession, error3);
               }

            } catch (Throwable e3) {
               error2 = e3;
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            } finally {
               KotlinUtil15.handleAutoCloseable(statement, error2);
            }

         } catch (Throwable e4) {
            error = e4;
            throw SneakyThrow.<RuntimeException>rethrow(e4);
         } finally {
            KotlinUtil15.handleAutoCloseable(rawConnection, error);
         }
      } else {
      }
   }

   public static void connectWithDefaults(DatabaseModule databaseModule, long value, boolean flag, boolean flag2, boolean flag3, int value2, Object obj) {
      if ((value2 & 1) != 0) {
         value = System.nanoTime();
      }

      if ((value2 & 2) != 0) {
         flag = true;
      }

      if ((value2 & 4) != 0) {
         flag2 = true;
      }

      if ((value2 & 8) != 0) {
         flag3 = true;
      }

      databaseModule.connect(value, flag, flag2, flag3);
   }

   public final long getCurrentTime(boolean flag) {
      long value = System.currentTimeMillis();
      if (flag) {
         return value;
      } else {
         long databaseTimeOffset = this.getConfigModule().getSettings().getDatabaseTimeOffset();
         if (this.getConfigModule().getSettings().isUseDatabaseTime()) {
            TimeZone timeZone = TimeZone.getTimeZone(this.currentTime);
            Calendar instance = Calendar.getInstance(timeZone);
            long timeInMillis = instance.getTimeInMillis();
            return databaseTimeOffset + timeInMillis + timeZone.getOffset(timeInMillis);
         } else {
            return value + databaseTimeOffset;
         }
      }
   }

   public static long getCurrentTimeWithDefaults(DatabaseModule databaseModule, boolean flag, int value, Object obj) {
      if ((value & 1) != 0) {
         flag = false;
      }

      return databaseModule.getCurrentTime(flag);
   }

   public final void registerServer(@NotNull DatabaseSession databaseSession, long value, boolean flag) {
      Settings settings = this.getConfigModule().getSettings();
      String text = settings.getServerName();
      String text2 = settings.getServerUuid();
      PunishmentTable punishmentTable = PunishmentTable.SERVERS;
      CharSequence message = "*";
      String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
      CharSequence message2 = "uuid=?";
      DatabaseSession databaseSession2 = databaseSession;
      CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
      int value2 = 0;

      for (int i = messages.length; value2 < i; value2++) {
         CharSequence message3 = messages[value2];
         if (message3.length() <= 0) {
            String text3 = "Failed requirement.";
            throw new IllegalArgumentException(text3.toString());
         }
      }

      PreparedQuery prepare2 = databaseSession2.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2)));
      prepare2.bindString(text2);
      ResultSet resultSet = SqlUtil.computeResultSet(prepare2);

      try {
         if (!resultSet.next()) {
            PunishmentTable punishmentTable2 = PunishmentTable.SERVERS;
            CharSequence message4 = "name,uuid";
            DatabaseSession databaseSession3 = databaseSession;
            byte b = 44;
            int value3 = 0;

            for (int i2 = 0; i2 < message4.length(); i2++) {
               char charAt2 = message4.charAt(i2);
               if (charAt2 == b) {
                  value3++;
               }
            }

            int value4 = value3;
            String text4 = "?";
            if (value4 > 0) {
               StringBuilder stringBuilder = new StringBuilder(value4 * 2);
               Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(0, value4);
               Iterator iterator2 = kotlinIntProgressionSub.iterator();

               while (iterator2.hasNext()) {
                  int value5 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
                  stringBuilder.append("?,");
               }

               text4 = StringsKt.computeText(stringBuilder.toString(), 1);
            }

            PreparedQuery prepare3 = databaseSession3.prepare(SqlText.box(SqlText.constructorImpl("INSERT INTO " + punishmentTable2 + '(' + message4 + ")VALUES(" + text4 + ')')));
            String[] texts = new String[]{text, text2};
            int value6 = 0;

            for (int i3 = texts.length; value6 < i3; value6++) {
               String text5 = texts[value6];
               prepare3.bindString(text5);
            }

            SqlUtil.computeResultSet(prepare3);
         } else {
            String string = resultSet.getString("name");
            if (!Intrinsics.areEqual(text, string)) {
               this.getConfigModule().logDebug("[Sync] Updating stored server name: \"" + string + "\" -> \"" + text + '"');
               PunishmentTable punishmentTable3 = PunishmentTable.SERVERS;
               String text6 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable3), "name=?");
               CharSequence message5 = "uuid=?";
               DatabaseSession databaseSession4 = databaseSession;
               CharSequence[] messages2 = new CharSequence[]{SqlText.box(text6), message5};
               int value7 = 0;

               for (int i4 = messages2.length; value7 < i4; value7++) {
                  CharSequence message6 = messages2[value7];
                  if (message6.length() <= 0) {
                     String text7 = "Failed requirement.";
                     throw new IllegalArgumentException(text7.toString());
                  }
               }

               PreparedQuery prepare4 = databaseSession4.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(text6) + " WHERE " + message5)));
               String[] texts2 = new String[]{text, text2};
               int value8 = 0;

               for (int i5 = texts2.length; value8 < i5; value8++) {
                  String text8 = texts2[value8];
                  prepare4.bindString(text8);
               }

               SqlUtil.computeResultSet(prepare4);
               PunishmentTable[] punishmentTables = PunishmentTable.values();
               int value9 = 0;

               for (int i6 = punishmentTables.length; value9 < i6; value9++) {
                  PunishmentTable punishmentTable4 = punishmentTables[value9];
                  if (punishmentTable4.obtainFlag()) {
                     String text9 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable4), "server_scope=?");
                     CharSequence message7 = "server_scope=?";
                     DatabaseSession databaseSession5 = databaseSession;
                     CharSequence[] messages3 = new CharSequence[]{SqlText.box(text9), message7};
                     byte b2 = 0;

                     for (int i7 = messages3.length; b2 < i7; b2 += 1) {
                        CharSequence message8 = messages3[b2];
                        if (message8.length() <= 0) {
                           String text10 = "Failed requirement.";
                           throw new IllegalArgumentException(text10.toString());
                        }
                     }

                     PreparedQuery prepare5 = databaseSession5.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(text9) + " WHERE " + message7)));
                     String[] texts3 = new String[]{text, string};
                     byte b3 = 0;

                     for (int i8 = texts3.length; b3 < i8; b3 += 1) {
                        String text11 = texts3[b3];
                        prepare5.bindString(text11);
                     }

                     SqlUtil.computeResultSet(prepare5);
                     String text12 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable4), "server_origin=?");
                     CharSequence message9 = "server_origin=?";
                     DatabaseSession databaseSession6 = databaseSession;
                     CharSequence[] messages4 = new CharSequence[]{SqlText.box(text12), message9};
                     byte b4 = 0;

                     for (int i9 = messages4.length; b4 < i9; b4 += 1) {
                        CharSequence message10 = messages4[b4];
                        if (message10.length() <= 0) {
                           String text13 = "Failed requirement.";
                           throw new IllegalArgumentException(text13.toString());
                        }
                     }

                     PreparedQuery prepare6 = databaseSession6.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(text12) + " WHERE " + message9)));
                     String[] texts4 = new String[]{text, string};
                     byte b5 = 0;

                     for (int i10 = texts4.length; b5 < i10; b5 += 1) {
                        String text14 = texts4[b5];
                        prepare6.bindString(text14);
                     }

                     SqlUtil.computeResultSet(prepare6);
                  }
               }
            }
         }

         if (value <= 0L) {
            this.updateServerRecord(databaseSession, flag);
         } else {
            this.platform.runAsyncLater(() -> {
               this.updateServerRecord(databaseSession, flag);
            }, value);
         }
      } catch (Exception e) {
         if (e instanceof SQLException) {
            this.logConnectionError(e);
         } else {
            ConfigModule.handleConfigModule(this.getConfigModule(), e, 0, 2, null);
         }
      }
   }

   public static void registerServerWithDefaults(DatabaseModule databaseModule, DatabaseSession databaseSession, long value, boolean flag, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = 0L;
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      databaseModule.registerServer(databaseSession, value, flag);
   }

   private final void backupDatabase(String text, long value) {
      boolean flag = false;
      if (Intrinsics.areEqual(text, "sqlite") && this.convertSqlite) {
         flag = DateFormats.CONNECTION_UTIL.checkDatabaseModule(this);
      }

      if (!flag) {
         this.initialize(this.getConfigModule());
         connectWithDefaults(this, value, false, false, false, 14, null);
      }
   }

   static void backupDatabaseWithDefaults(DatabaseModule databaseModule, String text, long value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = System.nanoTime();
      }

      databaseModule.backupDatabase(text, value);
   }

   public final void initialize(@NotNull ConfigModule configModule) {
      long value = System.nanoTime();
      this.dataSource = null;
      Settings settings = configModule.getSettings();
      String sqlDriver = settings.getSqlDriver();
      List driverDefinitions = this.getDriverDefinitions(configModule);
      DriverDefinition driver = this.findDriver(configModule, sqlDriver);
      if (driver == null) {
         PluginModule pluginModule = this;
         StringBuilder builder = new StringBuilder().append("Unknown SQL driver '").append(sqlDriver).append("'. Please use one of these drivers: ");
         Iterable iterable = driverDefinitions;
         var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

         for (Object obj : iterable) {
            DriverDefinition namedRecord = (DriverDefinition)obj;
            arrayList.add(namedRecord.getName());
         }

         String string = builder.append(arrayList).toString();
         pluginModule.platform.getLogger().severe(string);
      } else {
         Object driver2 = loadDriverWithDefaults(this, driver, false, false, 6, null);
         if (driver2 != null) {
            String sqlAddress = settings.getSqlAddress();
            String[] texts = new String[]{"h2", "sqlite"};
            boolean flag = KotlinStub.computeSet(texts).contains(sqlDriver);
            if (!StringsKt.containsWithDefaults(sqlAddress, ':', false, 2, null)) {
               String sqlDriver2 = this.getConfigModule().getSettings().getSqlDriver();
               String text;
               if (DATABASE_TYPE.checkText(sqlDriver2)) {
                  text = sqlAddress + ":3306";
               } else {
                  text = Intrinsics.areEqual(sqlDriver2, "pgsql") || Intrinsics.areEqual(sqlDriver2, "postgresql") ? sqlAddress + ":5432" : sqlAddress;
               }

               sqlAddress = (String)text;
            }

            String text2 = sqlDriver;
            switch (text2.hashCode()) {
               case -894935028:
                  if (text2.equals("sqlite")) {
                     File dataFolder = this.platform.getDataFolder();
                     String text3 = "litebans.sqlite";
                     sqlAddress = new File(dataFolder, text3).getPath();
                     this.queryExecutor = new LockingQueryExecutor(this.platform);
                  }
                  break;
               case 3274:
                  if (text2.equals("h2")) {
                     File dataFolder2 = this.platform.getDataFolder();
                     String text4 = "litebans";
                     sqlAddress = new File(dataFolder2, text4).getAbsolutePath();
                  }
                  break;
               case 106616951:
                  if (text2.equals("pgsql")) {
                     sqlDriver = "postgresql";
                  }
            }

            String text5 = this.queryExecutor.computeText(sqlAddress);
            String text6 = "jdbc:" + sqlDriver + "://" + text5;
            if (!flag) {
               text6 = text6 + '/' + settings.getSqlDatabase();
            }

            String text7 = settings.getA__f();
            if (text7.length() > 0) {
               if (!flag && !StringsKt.startsWithWithDefaults(text7, '?', false, 2, null)) {
                  text7 = '?' + text7;
               }

               if ((!Intrinsics.areEqual(sqlDriver, "h2") || !Intrinsics.areEqual(text7, "useSSL=false") && !Intrinsics.areEqual(text7, "useSSL=false&serverTimezone=UTC")) && !Intrinsics.areEqual(sqlDriver, "sqlite")) {
                  text6 = text6 + text7;
               }
            }

            String text8 = text6;
            String text9 = StringsKt.computeText(text8, "?useSSL=", ";useSSL=", false, 4, null);
            if (Intrinsics.areEqual(sqlDriver, "h2")) {
               text6 = text6 + ";TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0";
            }

            long value2 = Math.max(2000L, settings.getSqlPoolTimeout());
            boolean initializeSchema = initializeSchemaWithDefaults(this, new KotlinDatabaseSessionImpl(this.platform), null, false, false, 12, null);
            long value3 = initializeSchema ? value2 : 2147483647L;
            if (Intrinsics.areEqual(sqlDriver, "pgsql") || Intrinsics.areEqual(sqlDriver, "postgresql")) {
               value3 = Math.min(value3, 2147483L);
            }

            ConfigModule configModule2 = configModule;
            if (configModule2.isVerbose()) {
               configModule2.logDebug(text6);
            }

            ConfigModule configModule3 = configModule;
            if (configModule3.isVerbose()) {
               configModule3.logDebug(this.platform.createBridge().getServerVersion());
            }

            String text10 = "Connecting to database...";
            if (!access$initialized(this).get()) {
               PluginModule pluginModule2 = this;
               pluginModule2.platform.getLogger().info(text10);
            }

            try {
               PoolLogger.handleLogger(this.platform.getLogger());
               HikariConfig hikariConfig = new HikariConfig();
               hikariConfig.setPoolName("litebans-pool");
               hikariConfig.setJdbcUrl(text6);
               String text11 = "user";
               String sqlUsername = settings.getSqlUsername();
               hikariConfig.addDataSourceProperty(text11, sqlUsername);
               String text12 = "password";
               String sqlPassword = settings.getSqlPassword();
               hikariConfig.addDataSourceProperty(text12, sqlPassword);
               String text13 = "connectTimeout";
               Number number = value2;
               String string2 = number.toString();
               hikariConfig.addDataSourceProperty(text13, string2);
               String text14 = "socketTimeout";
               Number number2 = value3;
               String string3 = number2.toString();
               hikariConfig.addDataSourceProperty(text14, string3);
               String text15 = "timezone";
               String text16 = "UTC";
               hikariConfig.addDataSourceProperty(text15, text16);
               String text17 = "utf-8";
               String text18 = "utf8mb4_unicode_ci";
               String text19 = "useUnicode";
               String text20 = "true";
               hikariConfig.addDataSourceProperty(text19, text20);
               String text21 = "characterEncoding";
               hikariConfig.addDataSourceProperty(text21, text17);
               String text22 = "characterSetResults";
               hikariConfig.addDataSourceProperty(text22, text17);
               String text23 = "connectionCollation";
               hikariConfig.addDataSourceProperty(text23, text18);
               String text24 = "cachePrepStmts";
               String text25 = "true";
               hikariConfig.addDataSourceProperty(text24, text25);
               String[] texts2 = new String[]{
                  "useServerPrepStmts",
                  "cacheResultSetMetadata",
                  "cacheServerConfiguration",
                  "elideSetAutoCommits",
                  "useLocalSessionState",
                  "useLocalTransactionState"
               };
               byte b2 = 0;

               for (int i = texts2.length; b2 < i; b2 += 1) {
                  String text26 = texts2[b2];
                  String text27 = "true";
                  hikariConfig.addDataSourceProperty(text26, text27);
               }

               String[] texts3 = new String[]{"maintainTimeStats", "alwaysSendSetIsolation"};
               byte b3 = 0;

               for (int i2 = texts3.length; b3 < i2; b3 += 1) {
                  String text28 = texts3[b3];
                  String text29 = "false";
                  hikariConfig.addDataSourceProperty(text28, text29);
               }

               hikariConfig.setConnectionTimeout(value2);
               hikariConfig.setInitializationFailTimeout(Math.min(30000L, value2));
               if (settings.getSqlPoolMinConnections() != settings.getSqlPoolMaxConnections()) {
                  hikariConfig.setIdleTimeout(settings.getSqlPoolIdleTimeout());
               }

               hikariConfig.setMinimumIdle(Math.max(0, settings.getSqlPoolMinConnections()));
               hikariConfig.setMaximumPoolSize(Math.max(1, settings.getSqlPoolMaxConnections()));
               hikariConfig.setKeepaliveTime(Math.max(0L, settings.getSqlPoolKeepalive()));
               ThreadPoolExecutor executor = this.platform.getExecutor();
               int value4 = Math.max(20, hikariConfig.getMaximumPoolSize() + 8);
               executor.setMaximumPoolSize(value4);
               executor.setRejectedExecutionHandler(new QueueRejectedHandler(this));
               executor.setCorePoolSize(Math.min(value4, hikariConfig.getMinimumIdle() + 1));
               executor.setKeepAliveTime(4L, TimeUnit.MINUTES);
               if (Intrinsics.areEqual(sqlDriver, "sqlite")) {
                  hikariConfig.setConnectionTestQuery("SELECT 1");
               } else if (Intrinsics.areEqual(sqlDriver, "h2")) {
                  hikariConfig.setConnectionInitSql("SET IGNORECASE TRUE");
               }

               hikariConfig.handleDataSource(new PoolDataSourceImpl((Driver)driver2, hikariConfig));
               if (this.platform.getPlatformId() == 1) {
                  try {
                     PlatformBridge bridge = this.platform.createBridge();
                     Intrinsics.checkNotNullExpressionValue(bridge, "");
                     hikariConfig.handleThreadFactory(((BungeeBridge)bridge).computeThreadFactory(this.platform));
                  } catch (Throwable e) {
                     this.logConnectionError(e);
                  }
               }

               this.dataSource = new HikariDataSource(hikariConfig);
            } catch (Exception e2) {
               PluginModule pluginModule3 = this;
               String text30 = "Failed to connect to [" + text9 + "].";
               pluginModule3.platform.getLogger().severe(text30);
               if (!Intrinsics.areEqual(sqlDriver, "h2") && StringsKt.isBlank(settings.getSqlUsername()) && StringsKt.isBlank(settings.getSqlPassword())) {
                  PluginModule pluginModule4 = this;
                  String text31 = "[!!] No username and password has been provided in the configuration!";
                  pluginModule4.platform.getLogger().severe(text31);
               }

               String message = e2.getMessage();
               Throwable cause = e2.getCause();
               String text32 = cause != null ? cause.getMessage() : null;
               if (text32 == null || message == null || !StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)text32, false, 2, null)) {
                  PluginModule pluginModule5 = this;
                  String text33 = "Error: " + e2.getMessage();
                  pluginModule5.platform.getLogger().severe(text33);
               }

               String text34 = "";
               Throwable error = e2;
               byte b4 = 16;

               while (true) {
                  byte b5 = b4;
                  b4 -= 1;
                  if (b5 <= 0) {
                     break;
                  }

                  cause = error.getCause();
                  if (cause == null) {
                     break;
                  }

                  error = cause;
                  String localizedMessage = error.getLocalizedMessage();
                  if (localizedMessage == null) {
                     localizedMessage = "";
                  }

                  String text35 = localizedMessage;
                  if (text35.length() <= 0
                     || !StringsKt.containsWithDefaults((CharSequence)text35, (CharSequence)"Connection is not available, request timed out", false, 2, null)
                        && !StringsKt.containsWithDefaults((CharSequence)text35, (CharSequence)"The driver has not received any packets from the server.", false, 2, null)) {
                     if (text35.length() == 0) {
                        String message2 = error.getMessage();
                        if (message2 == null) {
                           message2 = "";
                        }

                        text35 = message2;
                     }

                     if (error instanceof UnknownHostException) {
                        text35 = "Unknown host: " + text35;
                     }

                     if (!Intrinsics.areEqual(text35, "Communications link failure")
                        && text35.length() > 0
                        && !StringsKt.containsWithDefaults((CharSequence)text34, (CharSequence)text35, false, 2, null)) {
                        PluginModule pluginModule6 = this;
                        String text36 = "Error: " + text35;
                        pluginModule6.platform.getLogger().severe(text36);
                     }

                     String message3 = error.getMessage();
                     if (message3 == null) {
                        message3 = "";
                     }

                     String text37 = message3;
                     if (text37.length() > 0) {
                        if (StringsKt.checkText(text37, "Access denied for user", false, 2, null)) {
                           this.logAccessDenied(settings);
                           break;
                        }

                        if (StringsKt.containsWithDefaults((CharSequence)text37, (CharSequence)"Public Key Retrieval is not allowed", false, 2, null)) {
                           this.warnUseSslDisabled();
                           break;
                        }

                        text34 = text37;
                     }
                  }
               }

               if (configModule.isDebugAtLeast(1)) {
                  e2.printStackTrace();
               }

               return;
            }

            String text38 = NumberFormats.computeText((System.nanoTime() - value) / 1000.0 / 1000.0);
            if (configModule.isDebugAtLeast(1)) {
               String text39 = "Connected to [" + text9 + "] successfully (" + text38 + " ms).";
               if (!access$initialized(this).get()) {
                  PluginModule pluginModule7 = this;
                  pluginModule7.platform.getLogger().info(text39);
               }
            } else {
               String text40 = "Connected to " + DATABASE_TYPE.computeText(sqlDriver) + " database successfully (" + text38 + " ms).";
               if (!access$initialized(this).get()) {
                  PluginModule pluginModule8 = this;
                  pluginModule8.platform.getLogger().info(text40);
               }
            }

            this.initialized.set(true);
            if (Intrinsics.areEqual(sqlDriver, "h2")) {
               try {
                  File dataFolder3 = this.platform.getDataFolder();
                  String text41 = "h2.yml";
                  File file = new File(dataFolder3, text41);
                  if (!file.exists() || YamlConfigFile.obtainArchiveHelper3() == null) {
                     File dataFolder4 = this.platform.getDataFolder();
                     String text42 = "h2.json";
                     file = new File(dataFolder4, text42);
                  }

                  ConfigFile throwIfFailed2 = this.platform.loadConfigFile(file).throwIfFailed();
                  throwIfFailed2.set("user", (Object) settings.getSqlUsername()).set("password", (Object) settings.getSqlPassword()).save();
               } catch (Exception e3) {
                  e3.printStackTrace();
               }
            }
         }
      }
   }

   private final void logAccessDenied(Settings settings) {
      PluginModule pluginModule = this;
      String text = "[!] Access to the database \"" + settings.getSqlDatabase() + "\" was denied.";
      pluginModule.platform.getLogger().severe(text);
      PluginModule pluginModule2 = this;
      String text2 = "[!] Ensure that the database \"" + settings.getSqlDatabase() + "\" has been created and that you have granted permission to access this database.";
      pluginModule2.platform.getLogger().severe(text2);
      if (Intrinsics.areEqual(settings.getSqlDatabase(), "litebans")) {
         PluginModule pluginModule3 = this;
         String text3 = "[!!] Your database is named \"litebans\"; this is the default database name. Ensure that you have defined the correct database name.";
         pluginModule3.platform.getLogger().severe(text3);
      }
   }

   public final DriverDefinition findDriver(@NotNull ConfigModule configModule, @NotNull String text) {
      Iterable driverDefinitions = this.getDriverDefinitions(configModule);
      Iterator iterator2 = driverDefinitions.iterator();

      Object obj;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj2 = iterator2.next();
            DriverDefinition namedRecord = (DriverDefinition)obj2;
            if (!Intrinsics.areEqual(namedRecord.getName(), text)) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      return (DriverDefinition)obj;
   }

   private final void createIndexes(String[] texts, Statement statement, PunishmentTable punishmentTable, String text) {
      try {
         String text2 = "CREATE INDEX IF NOT EXISTS";
         if (StringsKt.checkText(text, "5.", false, 2, null) || StringsKt.checkText(text, "8.", false, 2, null) || StringsKt.checkText(text, "9.", false, 2, null)) {
            text2 = "CREATE INDEX";
         }

         var arrayList = new ArrayList(texts.length);
         int value = 0;

         for (int i = texts.length; value < i; value++) {
            String text3 = texts[value];
            arrayList.add(text2 + " idx_" + punishmentTable + '_' + text3 + " ON " + punishmentTable + " (" + text3 + ')');
         }


         for (Object obj : arrayList) {
            String string = (String)obj;
            statement.execute(string);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final void perform4() {
      this.handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         try {
            if (databaseSession.isClosed()) {
               return Unit.UNIT;
            }

            ResultSet resultSet = SqlUtil.computeResultSet(databaseSession.prepare("SELECT NOW() as now"));
            resultSet.next();
            Timestamp timestamp = resultSet.getTimestamp("now");
            Calendar instance = Calendar.getInstance();
            instance.setTimeZone(TimeZone.getTimeZone("UTC"));
            instance.setTime(timestamp);
            this.databaseTimeOffset = (int)(this.getCurrentTime(true) - instance.getTimeInMillis());
            String text = BuildInfoUtil.obtainText();
            String text2 = BuildInfoUtil.obtainText3();
            String text3 = "UTC";
            PunishmentTable punishmentTable = PunishmentTable.CONFIG;
            CharSequence message = "*";
            ResultSet resultSet2 = SqlUtil.computeResultSet2(databaseSession.prepare(SqlText.box(SqlText.limit(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable), 1))));
            if (resultSet2.next()) {
               this.disclaimerAccepted = resultSet2.getInt("accept") > 0;
               this.currentTime = resultSet2.getString("timezone");
               if (!Intrinsics.areEqual(text2, resultSet2.getString("build"))) {
                  PunishmentTable punishmentTable2 = PunishmentTable.CONFIG;
                  SqlUtil.computeIntValue(databaseSession.prepare(SqlText.box(SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable2), "version='" + text + "',build='" + text2 + '\''))));
               }
            } else {
               PunishmentTable punishmentTable3 = PunishmentTable.CONFIG;
               CharSequence message2 = "version,build,timezone";
               byte b = 44;
               int value = 0;

               for (int i = 0; i < message2.length(); i++) {
                  char charAt2 = message2.charAt(i);
                  if (charAt2 == b) {
                     value++;
                  }
               }

               int value2 = value;
               String text4 = "?";
               if (value2 > 0) {
                  StringBuilder stringBuilder = new StringBuilder(value2 * 2);
                  Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(0, value2);
                  Iterator iterator2 = kotlinIntProgressionSub.iterator();

                  while (iterator2.hasNext()) {
                     int value3 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
                     stringBuilder.append("?,");
                  }

                  text4 = StringsKt.computeText(stringBuilder.toString(), 1);
               }

               PreparedQuery prepare2 = databaseSession.prepare(SqlText.box(SqlText.constructorImpl("INSERT INTO " + punishmentTable3 + '(' + message2 + ")VALUES(" + text4 + ')')));
               String[] texts = new String[]{text, text2, text3};
               int value4 = 0;

               for (int i2 = texts.length; value4 < i2; value4++) {
                  String text5 = texts[value4];
                  prepare2.bindString(text5);
               }

               SqlUtil.computeIntValue(prepare2);
            }
         } catch (Exception e) {
            this.logConnectionError(e);
         }

         if (this.getConfigModule().getSettings().getByteValue() > 0 && !this.disclaimerAccepted) {
            LiteSender console = databaseSession.getPlatform().getConsole();
            CharSequence message3 = Message.ALTS_GUARDRAIL_DISCLAIMER;
            CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, console, message3, null, 4, null);
         }

         return Unit.UNIT;
      });
   }

   public final Object downloadDriver(@NotNull DriverDefinition namedRecord, @NotNull File file, boolean flag) {
      try {
         try {
            try {
               if (!file.exists()) {
                  return null;
               } else {
                  String text = ConfigModule.FILE_DOWNLOADER.computeText(KotlinStub17.computeBytes(file));
                  if (namedRecord.getChecksum() != null && !Intrinsics.areEqual(namedRecord.getChecksum(), text)) {
                     file.delete();
                     throw new ChecksumMismatchException(file, namedRecord.getChecksum(), text);
                  } else {
                     URL[] urls = new URL[]{file.toURI().toURL(), DatabaseModule.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL()};
                     URLClassLoader urlClassLoader = new URLClassLoader(urls, Driver.class.getClassLoader());
                     Class class2 = urlClassLoader.loadClass(namedRecord.getClassName());
                     return flag ? class2.getConstructor().newInstance() : class2;
                  }
               }
            } catch (ClassNotFoundException | InstantiationException | MalformedURLException | NoSuchMethodException | URISyntaxException e) {
               throw SneakyThrow.<RuntimeException>rethrow(e);
            }
         } catch (IllegalAccessException e) {
            throw SneakyThrow.<RuntimeException>rethrow(e);
         }
      } catch (InvocationTargetException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private static final List computeList(Lazy lazy) {
      return (List)lazy.getValue();
   }

   public static final void access$createIndexes(DatabaseModule databaseModule, String[] texts, Statement statement, PunishmentTable punishmentTable, String text) {
      databaseModule.createIndexes(texts, statement, punishmentTable, text);
   }

   public static final AtomicBoolean access$initialized(DatabaseModule databaseModule) {
      return databaseModule.initialized;
   }

   public static final String obtainText() {
      return PROXY_STATEMENT_FRAME;
   }

   public static final String obtainText2() {
      return HIKARI_PROXY_STATEMENT_FRAME;
   }

   public static final String obtainText3() {
      return PREPARED_QUERY_FRAME;
   }
}
