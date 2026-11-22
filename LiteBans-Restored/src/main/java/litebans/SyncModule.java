package litebans;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;

public final class SyncModule extends PluginModule implements Runnable {
   public static final BomSplitter BOM_SPLITTER = new BomSplitter(null);
   private final Lazy configModuleLazy = KotlinStub5.computeLazy(() -> (ConfigModule)this.platform.getModule(ConfigModule.class));
   private final Lazy databaseModuleLazy = KotlinStub5.computeLazy(() -> (DatabaseModule)this.platform.getModule(DatabaseModule.class));
   private final Lazy broadcastModuleLazy = KotlinStub5.computeLazy(() -> (BroadcastModule)this.platform.getModule(BroadcastModule.class));
   private final Lazy punishmentLookupModuleLazy = KotlinStub5.computeLazy(() -> (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class));
   private final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private final CacheSyncTask cacheSyncTask = new CacheSyncTask(this.platform);
   private long longValue;
   private int intValue2;
   private byte byteValue;
   private byte[][] bytesArray4 = new byte[0][];
   private byte[][] bytesArray5;
   private CopyOnWriteArrayList copyOnWriteArrayList;
   private final CopyOnWriteArrayList listeners;
   private final Triple triple;
   public static final char CHAR_VALUE = '\ufeff';
   public static final String TEXT = "\ufeff";

   public SyncModule(@NotNull Platform platform) {
      super(platform);
      int value = 0;
      byte[][] bytesArray = new byte[4][];

      while (value < 4) {
         int value2 = value;
         bytesArray[value2] = StringMatching.obtainBytes();
         value++;
      }

      this.bytesArray5 = bytesArray;
      this.copyOnWriteArrayList = new CopyOnWriteArrayList();
      this.listeners = new CopyOnWriteArrayList();
      this.triple = new Triple(new AtomicLong(), new AtomicLong(), new AtomicLong());
   }

   public final ConfigModule getConfigModule() {
      Lazy lazy = this.configModuleLazy;
      return (ConfigModule)lazy.getValue();
   }

   public final DatabaseModule getDatabaseModule() {
      Lazy lazy = this.databaseModuleLazy;
      return (DatabaseModule)lazy.getValue();
   }

   public final BroadcastModule getBroadcastModule() {
      Lazy lazy = this.broadcastModuleLazy;
      return (BroadcastModule)lazy.getValue();
   }

   public final PunishmentLookupModule getLookupModule() {
      Lazy lazy = this.punishmentLookupModuleLazy;
      return (PunishmentLookupModule)lazy.getValue();
   }

   public final CacheSyncTask getKotlinCommandTaskSub7() {
      return this.cacheSyncTask;
   }

   public final int obtainIntValue2() {
      return this.intValue2;
   }

   public final void handleIntValue(int value) {
      this.intValue2 = value;
   }

   public final byte obtainByteValue() {
      return this.byteValue;
   }

   public final void handleByteValue(byte b) {
      this.byteValue = b;
   }

   public final void handleBytesArray(@NotNull byte[][] bytesArray) {
      this.bytesArray4 = bytesArray;
   }

   public final CopyOnWriteArrayList getCopyOnWriteArrayList() {
      return this.copyOnWriteArrayList;
   }

   public final void handleCopyOnWriteArrayList(@NotNull CopyOnWriteArrayList copyOnWriteArrayList) {
      this.copyOnWriteArrayList = copyOnWriteArrayList;
   }

   public final Triple obtainTriple() {
      return this.triple;
   }

   public final boolean addListener(@NotNull LiteAInterface liteAInterface) {
      return this.listeners.add(liteAInterface);
   }

   public final boolean removeListener(@NotNull LiteAInterface liteAInterface) {
      return this.listeners.remove(liteAInterface);
   }

   public final KotlinHelper8 computeKotlinHelper(int value) {
      Iterable iterable = this.copyOnWriteArrayList;
      Iterator iterator2 = iterable.iterator();

      Object obj;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj2 = iterator2.next();
            Reference reference = (Reference)obj2;
            KotlinHelper8 kotlinHelper8 = (KotlinHelper8)reference.get();
            if ((kotlinHelper8 != null ? kotlinHelper8.getByteValue() : -1) != value) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      return (Reference)obj != null ? (KotlinHelper8)((Reference)obj).get() : null;
   }

   @Override
   public void enable() {
      this.getConfigModule().enable();
      Settings settings = this.getConfigModule().getSettings();
      if (DatabaseModule.DATABASE_TYPE.checkText2(settings.getSqlDriver()) && settings.isSync()) {
         this.atomicBoolean2.set(false);
         settings.getPlatform().runAsyncTimer(this, settings.getSyncPollTicks(), settings.getSyncPollTicks());
      }
   }

   @Override
   public void reloadConfig() {
      this.enable();
   }

   @Override
   public void run() {
      AtomicBoolean atomicBoolean = this.atomicBoolean2;
      if (atomicBoolean.compareAndSet(false, true)) {
         try {
            DatabaseModule databaseModule = this.getDatabaseModule();

            try {
               DatabaseSession session = databaseModule.openSession();
               Throwable error = null;

               try {
                  this.longValue = this.processSyncMessages(session, this.listeners, this.longValue);
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
               if (!databaseModule.isConnectionFailure(sqlException)) {
                  throw sqlException;
               }
            }
         } catch (Throwable e3) {
            if (!(e3 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            }
            SQLException sqlException2 = (SQLException) e3;
            this.getDatabaseModule().logConnectionError(sqlException2);
         } finally {
            atomicBoolean.set(false);
         }
      }
   }

   private final void checkDuplicateServer(DatabaseSession databaseSession) {
      long incrementAndGet2 = ((AtomicLong)this.triple.getThird()).incrementAndGet();
      long value = incrementAndGet2 - ((AtomicLong)this.triple.getSecond()).get();
      if (value > 1L) {
         this.handleDuplicateServer(databaseSession);
      }
   }

   private final void handleDuplicateServer(DatabaseSession databaseSession) {
      this.getConfigModule().logDebug("[Sync] Detected duplicate server. This will be resolved automatically.");
      StringMatching.handleTriple(this.triple, EnumSwitchMap4.ENUM_SWITCH_MAP);
      File dataFolder = databaseSession.getPlatform().getDataFolder();
      String text = "server.yml";
      FileUtils.computeFile2(new File(dataFolder, text));
      File dataFolder2 = databaseSession.getPlatform().getDataFolder();
      String text2 = "server.json";
      FileUtils.computeFile2(new File(dataFolder2, text2));
      Pair pair = this.getConfigModule().getSettings().loadServerIdentity(true);
      this.getConfigModule().getSettings().setText57((String)pair.getFirst());
      this.getConfigModule().getSettings().setText56((String)pair.getSecond());
      DatabaseModule.registerServerWithDefaults(databaseSession.getDatabaseModule(), databaseSession, 0L, false, 6, null);
   }

   public final void sendSyncMessage(@NotNull SyncMessageType syncMessageType, @NotNull DatabaseSession databaseSession, @NotNull CharSequence message, int value) {
      if (this.getDatabaseModule().isSyncEnabled() && !databaseSession.isClosed()) {
         CharSequence message2 = message;
         short value2 = 4092;
         String text = TextUtil.access$length(message2.toString(), value2);
         UserRecord serverRecord = this.getDatabaseModule().getServerRecord();
         if (serverRecord != null && (!serverRecord.obtainFlag2() || value != -1)) {
            try {
               int id = serverRecord.getId();
               if (value != -1 && value != id) {
                  id = value;
               } else {
                  ((AtomicLong)this.triple.getSecond()).incrementAndGet();
               }

               int value3 = (syncMessageType.obtainIntValue() << 16 | id) + 42;
               PunishmentTable punishmentTable = PunishmentTable.SYNC;
               CharSequence message3 = "info,msg";
               DatabaseSession databaseSession2 = databaseSession;
               byte b = 44;
               int value4 = 0;

               for (int i = 0; i < message3.length(); i++) {
                  char charAt2 = message3.charAt(i);
                  if (charAt2 == b) {
                     value4++;
                  }
               }

               int value5 = value4;
               String text2 = "?";
               if (value5 > 0) {
                  StringBuilder stringBuilder = new StringBuilder(value5 * 2);
                  Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(0, value5);
                  Iterator iterator2 = kotlinIntProgressionSub.iterator();

                  while (iterator2.hasNext()) {
                     int value6 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
                     stringBuilder.append("?,");
                  }

                  text2 = StringsKt.computeText(stringBuilder.toString(), 1);
               }

               PreparedQuery bindInt2 = databaseSession2.prepare(SqlText.box(SqlText.constructorImpl("INSERT INTO " + punishmentTable + '(' + message3 + ")VALUES(" + text2 + ')'))).bindInt(value3);
               bindInt2.bindString(text);
               SqlUtil.computeResultSet(bindInt2);
               DatabaseSession databaseSession3 = databaseSession;
               PeriodicTaskModule module = (PeriodicTaskModule)databaseSession3.getPlatform().getModule(PeriodicTaskModule.class);
               long value7 = System.currentTimeMillis();
               if (value7 - databaseSession3.getDatabaseModule().getLongValue2() > 28800000L) {
                  databaseSession3.getDatabaseModule().setLongValue2(value7);
                  String text3 = "1 minute";
                  if (Intrinsics.areEqual(this.getConfigModule().getSettings().getSqlDriver(), "pgsql")) {
                     text3 = '\'' + text3 + '\'';
                  }

                  PunishmentTable punishmentTable2 = PunishmentTable.SYNC;
                  String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable2);
                  CharSequence message4 = "time < (NOW() - INTERVAL " + text3 + ')';
                  CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message4};
                  int value8 = 0;

                  for (int i2 = messages.length; value8 < i2; value8++) {
                     CharSequence message5 = messages[value8];
                     if (message5.length() <= 0) {
                        String text4 = "Failed requirement.";
                        throw new IllegalArgumentException(text4.toString());
                     }
                  }

                  SqlUtil.computeResultSet(databaseSession3.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message4))));
                  module.handleDatabaseSession2(databaseSession3);
               }
            } catch (Throwable e) {
               if (!(e instanceof SQLException)) {
                  throw SneakyThrow.<RuntimeException>rethrow(e);
               }
               SQLException sqlException = (SQLException) e;
               this.getDatabaseModule().logConnectionError(sqlException);
            }
         } else {
            char c = 'x';
            ConfigModule configModule = this.getConfigModule();
            if (configModule.isVerbose()) {
               configModule.logDebug("0x11 <" + c + "> " + serverRecord);
            }
         }
      }
   }

   public static void sendSyncMessageWithDefaults(SyncModule syncModule, SyncMessageType syncMessageType, DatabaseSession databaseSession, CharSequence message, int value, int value2, Object obj) {
      if ((value2 & 4) != 0) {
         message = "";
      }

      if ((value2 & 8) != 0) {
         value = -1;
      }

      syncModule.sendSyncMessage(syncMessageType, databaseSession, message, value);
   }

   public void handleSyncMessage(@NotNull DatabaseSession databaseSession, @NotNull String text, @NotNull String text2, @NotNull SyncMessageType syncMessageType, @NotNull Set set) {
      boolean flag = false;
      boolean flag2 = false;
      if (text.length() == 36) {
         SyncMessageType syncMessageType2 = syncMessageType;
         PunishmentType punishmentType;
         switch (SyncMessageTypeSwitchMap.value[syncMessageType2.ordinal()]) {
            case 6:
               punishmentType = PunishmentType.BAN;
               break;
            case 7:
               punishmentType = PunishmentType.MUTE;
               break;
            case 8:
               punishmentType = PunishmentType.WARN;
               break;
            case 9:
            case 10:
               punishmentType = PunishmentType.KICK;
               break;
            case 11:
               punishmentType = PunishmentType.BAN;
               break;
            case 12:
               punishmentType = PunishmentType.MUTE;
               break;
            default:
               punishmentType = null;
         }

         PunishmentType punishmentType2 = punishmentType;
         if (punishmentType2 == null) {
            ConfigModule configModule = this.getConfigModule();
            if (configModule.isVerbose()) {
               configModule.logDebug("[!!!] NF " + syncMessageType);
            }
         } else if (syncMessageType == SyncMessageType.UNMUTE) {
            PunishmentLookupModule lookupModule = this.getLookupModule();
            boolean flag3 = CollectionsKt.checkIterable((Iterable)lookupModule.getMuteChatTask().getMutedPlayers().entrySet(), new KotlinFunction1Impl2(text, text2));
            if (flag3) {
               lookupModule.handleText(text, text2);
            }
         } else if (!set.isEmpty()) {
            if (!syncMessageType.obtainFlag() && !set.contains(text)) {
               if (text2.length() == 0) {
                  return;
               }

               Object[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
               int value = 0;
               int length2 = onlineSenders.length;

               while (true) {
                  if (value >= length2) {
                     flag = true;
                     break;
                  }

                  Object obj = onlineSenders[value];
                  LiteSender liteSender = (LiteSender)obj;
                  if (Punishment.PUNISHMENT_COMPANION.checkFlag(true, liteSender.getIpAddress(), text2, Punishment.PUNISHMENT_COMPANION.checkText(text2))) {
                     flag = false;
                     break;
                  }

                  value++;
               }

               if (flag) {
                  return;
               }
            }

            Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(databaseSession, text, text2, punishmentType2, true, false, 16, null);
            if (punishment != null) {
               long currentTime = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);
               if (databaseSession.getPlatform().getPlatformId() == 1) {
                  BroadcastModule broadcastModule = this.getBroadcastModule();
                  if (syncMessageType == SyncMessageType.BAN) {
                     BroadcastModule.handleBroadcastModule(broadcastModule, databaseSession, punishment, null, 2, null);
                  } else if (syncMessageType == SyncMessageType.UNBAN) {
                     Object[] onlineSenders2 = broadcastModule.platform.getOnlineSenders();
                     var arrayList = new ArrayList();
                     int value2 = 0;

                     for (int i = onlineSenders2.length; value2 < i; value2++) {
                        Object obj2 = onlineSenders2[value2];
                        LiteSender liteSender2 = (LiteSender)obj2;
                        if (punishment.checkText(liteSender2.getUuidString(), liteSender2.getIpAddress(), currentTime)) {
                           arrayList.add(obj2);
                        }
                     }


                     for (Object obj3 : arrayList) {
                        LiteSender liteSender3 = (LiteSender)obj3;
                        Intrinsics.checkNotNull(liteSender3);
                        broadcastModule.checkBanOnJoin(databaseSession, liteSender3);
                     }
                  }
               }

               Object[] onlineSenders3 = databaseSession.getPlatform().getOnlineSenders();
               var arrayList2 = new ArrayList();
               int value3 = 0;

               for (int i2 = onlineSenders3.length; value3 < i2; value3++) {
                  Object obj4;
                  label114: {
                     obj4 = onlineSenders3[value3];
                     LiteSender liteSender4 = (LiteSender)obj4;
                     if (punishment.checkText(liteSender4.getUuidString(), liteSender4.getIpAddress(), currentTime)) {
                        ServerScope serverScope = ServerScopes.SERVER_SCOPE;
                        String serverName = liteSender4.getServerName();
                        if (serverName == null) {
                           serverName = databaseSession.getDatabaseModule().getServerName();
                        }

                        String serverScope2 = punishment.getServerScope();
                        if (serverScope2 == null) {
                           serverScope2 = ServerScopes.SERVER_SCOPE.getLocalScope();
                        }

                        if (serverScope.scopesMatch(serverName, serverScope2)) {
                           flag2 = true;
                           break label114;
                        }
                     }

                     flag2 = false;
                  }

                  if (flag2) {
                     arrayList2.add(obj4);
                  }
               }


               for (Object obj5 : arrayList2) {
                  LiteSender liteSender5 = (LiteSender)obj5;
                  switch (punishmentType2 == null ? -1 : SyncModuleSwitchMap.value[punishmentType2.ordinal()]) {
                     case 1:
                        Intrinsics.checkNotNull(liteSender5);
                        if (AltAccountPunisher.isAllowedFor(databaseSession, punishment, liteSender5)) {
                           this.getBroadcastModule().sendMessageSync(liteSender5, Placeholders.getPunishmentMessage(databaseSession, punishment, liteSender5, -1));
                        }
                        break;
                     case 2:
                        this.getLookupModule().handleText(text, punishment);
                        if (this.getConfigModule().getSettings().isSyncNotifications()) {
                           BroadcastModule broadcastModule2 = this.getBroadcastModule();
                           Intrinsics.checkNotNull(liteSender5);
                           broadcastModule2.handleDatabaseSession(databaseSession, liteSender5, punishment, -1, "muted");
                        }
                        break;
                     case 3:
                        if (this.getConfigModule().getSettings().isSyncNotifications()) {
                           databaseSession.markWarned(text);
                           BroadcastModule broadcastModule3 = this.getBroadcastModule();
                           Intrinsics.checkNotNull(liteSender5);
                           broadcastModule3.handleDatabaseSession(databaseSession, liteSender5, punishment, -1, "warned");
                        }
                        break;
                     case 4:
                        BroadcastModule broadcastModule4 = this.getBroadcastModule();
                        Intrinsics.checkNotNull(liteSender5);
                        broadcastModule4.sendMessageSync(liteSender5, Placeholders.getKickMessage(databaseSession, punishment, liteSender5));
                  }
               }
            }
         }
      }
   }

   public final long processSyncMessages(@NotNull DatabaseSession databaseSession, @NotNull Collection collection, long value) {
      LiteSender liteSender = null;
      long value2 = value;

      try {
         UserRecord serverRecord = databaseSession.getDatabaseModule().getServerRecord();
         if (databaseSession.isClosed() || serverRecord == null) {
            char c = 's';
            ConfigModule configModule = this.getConfigModule();
            if (configModule.isVerbose()) {
               configModule.logDebug("0x11 <" + c + "> " + serverRecord);
            }

            return value2;
         }

         int syncPollSkip = this.getConfigModule().getSettings().getSyncPollSkip();
         LiteSender[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
         Object[] objs = onlineSenders;
         var arrayList = new ArrayList(objs.length);
         int value3 = 0;

         for (int i = objs.length; value3 < i; value3++) {
            Object obj = objs[value3];
            LiteSender liteSender2 = (LiteSender)obj;
            String uuidString = liteSender2.getUuidString();
            arrayList.add(uuidString);
         }

         HashSet hashSet = CollectionsKt.toHashSet(arrayList);
         if (hashSet.size() <= 0) {
            DatabaseModule databaseModule = databaseSession.getDatabaseModule();
            databaseModule.setByteValue2((byte)(databaseModule.getByteValue2() + 1));
            if (databaseModule.getByteValue2() < syncPollSkip) {
               return value2;
            }

            databaseSession.getDatabaseModule().setByteValue2((byte)0);
         }

         long currentTime = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);
         if (value2 == 0L) {
            PunishmentTable punishmentTable = PunishmentTable.SYNC;
            CharSequence message = "id";
            ResultSet resultSet = SqlUtil.computeResultSet(databaseSession.prepare(SqlText.box(SqlText.limit(SqlText.orderByDesc(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable), "id"), 1))));
            if (resultSet.next()) {
               value2 = resultSet.getLong("id");
               ConfigModule configModule2 = this.getConfigModule();
               if (configModule2.isVerbose()) {
                  String text = "L+ " + value2;
                  configModule2.logDebug(text);
               }
            }
         }

         ResultSet resultSet2 = SqlUtil.computeResultSet(databaseSession.prepare("SELECT * FROM " + PunishmentTable.SYNC + " WHERE id>?").bindLong(value2));

         label1388:
         while (resultSet2.next()) {
            long long2 = resultSet2.getLong("id");
            long value4 = resultSet2.getLong("info") - 42;
            int constructor = UInt.constructorImpl((int)(value4 & 65535L));
            long value5 = value4 >> 16;
            UserRecord serverRecordById = databaseSession.getDatabaseModule().getServerRecordById(databaseSession, constructor, true);
            value2 = Math.max(value2, long2);
            if (serverRecordById == null) {
               char c2 = 'l';
               ConfigModule configModule3 = this.getConfigModule();
               if (configModule3.isVerbose()) {
                  configModule3.logDebug("0x11 <" + c2 + "> " + serverRecordById);
               }
            } else if (constructor == serverRecord.getId()) {
               this.checkDuplicateServer(databaseSession);
            } else {
               String string = resultSet2.getString("msg");
               SyncMessageType syncMessageType = SyncMessageType.SYNC_MESSAGE_TYPE_COMPANION.computeSyncMessageType((int)value5);
               if (syncMessageType == null) {
                  char c3 = 'c';
                  Long boxed = value5;
                  ConfigModule configModule4 = this.getConfigModule();
                  if (configModule4.isVerbose()) {
                     configModule4.logDebug("0x11 <" + c3 + "> " + boxed);
                  }
               } else {
                  ((AtomicLong)this.triple.getFirst()).incrementAndGet();
                  ConfigModule configModule5 = this.getConfigModule();
                  if (configModule5.isVerbose()) {
                     String text2 = "id: " + long2 + ", type: " + value5 + ", server: " + UInt.toStringImpl(constructor) + ", message: \"" + string + '"';
                     configModule5.logDebug(text2);
                  }

                  label1380: {
                     String text3 = string;
                     if (!StringsKt.containsWithDefaults((CharSequence)text3, (CharSequence)"server", false, 2, null)) {
                        String text4 = string;
                        if (!StringsKt.containsWithDefaults((CharSequence)text4, (CharSequence)"Server", false, 2, null)) {
                           break label1380;
                        }
                     }

                     String text5 = Message.computeText(serverRecordById.getName(), false);
                     String text6 = string;
                     string = Placeholders.replacePlaceholder(Placeholders.replacePlaceholder(text6, "server", text5), "playerServer", text5);
                  }

                  var collection2 = collection;
                  boolean flag;
                  if (collection2 instanceof Collection && ((Collection)collection2).isEmpty()) {
                     flag = false;
                  } else {
                     label1456: {
                        for (Object obj2 : collection2) {
                           LiteAInterface liteAInterface = (LiteAInterface)obj2;
                           if (!liteAInterface.checkUserRecord(serverRecordById, syncMessageType, string)) {
                              flag = true;
                              break label1456;
                           }
                        }

                        flag = false;
                     }
                  }

                  if (flag) {
                     CommandArguments commandArguments;
                     ConfigModule configModule6 = this.getConfigModule();
                     String text7 = "0x11 <Q> " + value5;
                     String[] texts;
                     if (Intrinsics.areEqual(text7, "sort")) {
                        String[] texts2 = new String[]{"00"};
                        texts = texts2;
                     } else {
                        String[] texts3 = new String[]{"01:" + text7};
                        texts = texts3;
                     }

                     String[] texts4 = texts;
                     commandArguments = new CommandArguments(configModule6, Arrays.copyOf(texts4, texts4.length));
                     commandArguments.obtainIntValue();
                  } else {
                     Settings settings = this.getConfigModule().getSettings();
                     switch (SyncModuleSwitchMap.value2[syncMessageType.ordinal()]) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                           BomSplitter bomSplitter = BOM_SPLITTER;
                           Intrinsics.checkNotNullField(string, "element");
                           String[] texts5 = BomSplitter.computeTexts(bomSplitter, string, 0, 2, null);
                           this.handleSyncMessage(databaseSession, TargetParser.normalize(texts5[0]), texts5[1], syncMessageType, hashSet);
                           break;
                        case 6:
                        case 7:
                           if (settings.isSyncBroadcasts()) {
                              BomSplitter bomSplitter2 = BOM_SPLITTER;
                              Intrinsics.checkNotNullField(string, "element");
                              String[] texts6 = bomSplitter2.computeTexts(string, 3);
                              String orNull = (String)ArraysKt.getOrNull(texts6, 1);
                              if (orNull == null) {
                                 orNull = "BAN";
                              }

                              String text8 = orNull;
                              String upperCase = text8.toUpperCase(Locale.ROOT);
                              PunishmentType[] punishmentTypes = PunishmentType.values();
                              int value6 = 0;

                              for (int i2 = punishmentTypes.length; value6 < i2; value6++) {
                                 PunishmentType punishmentType = punishmentTypes[value6];
                                 if (Intrinsics.areEqual(punishmentType.name(), upperCase)) {
                                    String orNull2 = (String)ArraysKt.getOrNull(texts6, 2);
                                    if (orNull2 == null) {
                                       orNull2 = databaseSession.getDatabaseModule().getServerName();
                                    }

                                    String text9 = orNull2;
                                    BroadcastModule broadcastModule = this.getBroadcastModule();
                                    BroadcastModule.handleBroadcastModule(broadcastModule, databaseSession, texts6[0], syncMessageType, punishmentType, text9, false, false, null, 112, null);
                                    continue label1388;
                                 }
                              }

                              throw new NoSuchElementException("Array contains no element matching the predicate.");
                           }
                           break;
                        case 8:
                           BroadcastModule broadcastModule2 = this.getBroadcastModule();
                           Intrinsics.checkNotNullField(string, "element");
                           BroadcastModule.handleBroadcastModule(broadcastModule2, databaseSession, string, "console", null, null, settings.isSyncBroadcasts() && settings.isNotifyNotifyConsole() && settings.isLocalMessages(), false, null, 108, null);
                           break;
                        case 9:
                           BomSplitter bomSplitter3 = BOM_SPLITTER;
                           Intrinsics.checkNotNullField(string, "element");
                           String[] texts7 = BomSplitter.computeTexts(bomSplitter3, string, 0, 2, null);
                           String text10 = texts7[0];
                           String text11 = texts7[1];
                           BroadcastModule broadcastModule3 = this.getBroadcastModule();
                           BroadcastModule.handleBroadcastModule(broadcastModule3, databaseSession, text11, text10, null, null, settings.isSyncBroadcasts(), false, null, 108, null);
                           break;
                        case 10:
                           if (settings.isSyncNotifications()) {
                              BomSplitter bomSplitter4 = BOM_SPLITTER;
                              Intrinsics.checkNotNullField(string, "element");
                              String[] texts8 = BomSplitter.computeTexts(bomSplitter4, string, 0, 2, null);
                              this.getBroadcastModule().handleText2(texts8[0], texts8[1], false);
                           }
                           break;
                        case 11:
                           BroadcastModule broadcastModule4 = this.getBroadcastModule();
                           Intrinsics.checkNotNullField(string, "element");
                           BroadcastModule.handleBroadcastModule(broadcastModule4, databaseSession, string, syncMessageType, null, null, settings.isSyncDupeipNotifications() && settings.isNotifyDupeipOnJoin() && settings.isSyncNotifications(), false, null, 108, null);
                           break;
                        case 12:
                           BroadcastModule broadcastModule5 = this.getBroadcastModule();
                           Intrinsics.checkNotNullField(string, "element");
                           BroadcastModule.handleBroadcastModule(broadcastModule5, databaseSession, string, syncMessageType, PunishmentType.MUTE, null, settings.isSyncNotifications(), false, null, 104, null);
                           break;
                        case 13:
                           LiteSender[] onlineSenders2 = settings.getPlatform().getOnlineSenders();
                           Intrinsics.checkNotNull(onlineSenders2);
                           if (onlineSenders2.length != 0) {
                              String[] texts9;
                              String text12;
                              String text13;
                              String text14;
                              String text15;
                              boolean flag2;
                              boolean flag3;
                              byte b2;
                              label1334: {
                                 BomSplitter bomSplitter5 = BOM_SPLITTER;
                                 Intrinsics.checkNotNullField(string, "element");
                                 texts9 = bomSplitter5.computeTexts(string, 8);
                                 text12 = TargetParser.normalize(texts9[0]);
                                 text13 = texts9[1];
                                 text14 = texts9[2];
                                 text15 = texts9[3];
                                 String orNull3 = (String)ArraysKt.getOrNull(texts9, 4);
                                 flag2 = Intrinsics.areEqual(orNull3, "1");
                                 String orNull4 = (String)ArraysKt.getOrNull(texts9, 5);
                                 flag3 = Intrinsics.areEqual(orNull4, "1");
                                 String orNull5 = (String)ArraysKt.getOrNull(texts9, 6);
                                 if (orNull5 != null) {
                                    UByte uByte = KotlinUtil20.computeUByte(orNull5);
                                    if (uByte != null) {
                                       b2 = uByte.unbox();
                                       break label1334;
                                    }
                                 }

                                 b2 = (byte)-1;
                              }

                              byte b3;
                              int value7;
                              label1329: {
                                 b3 = b2;
                                 String orNull6 = (String)ArraysKt.getOrNull(texts9, 7);
                                 if (orNull6 != null) {
                                    Integer boxed2 = StringsKt.computeInteger(orNull6);
                                    if (boxed2 != null) {
                                       value7 = boxed2;
                                       break label1329;
                                    }
                                 }

                                 value7 = 0;
                              }

                              int value8 = value7;
                              int value9 = 0;
                              int length2 = onlineSenders2.length;

                              while (true) {
                                 if (value9 >= length2) {
                                    liteSender = null;
                                    break;
                                 }

                                 LiteSender liteSender3 = onlineSenders2[value9];
                                 if (StringsKt.checkText(liteSender3.getName(), text14, true)) {
                                    liteSender = liteSender3;
                                    break;
                                 }

                                 value9++;
                              }

                              LiteSender liteSender4 = liteSender;
                              if (liteSender4 != null) {
                                 String text16 = databaseSession.getDatabaseModule().computeText(liteSender4);
                                 if (text16 == null) {
                                    text16 = databaseSession.getDatabaseModule().getServerName();
                                 }

                                 String text17 = text16;
                                 Punishment punishment = new Punishment(
                                    PunishmentType.KICK,
                                    liteSender4.getUuidString(),
                                    liteSender4.getIpAddress(),
                                    text15,
                                    text12,
                                    text13,
                                    ServerScopes.SERVER_SCOPE.getLocalScope(),
                                    text17,
                                    currentTime,
                                    -1L,
                                    b3,
                                    flag2,
                                    false,
                                    false,
                                    0L,
                                    16384,
                                    null
                                 );
                                 LiteCommand liteCommand = settings.getPlatform().getCommandManager().computeLiteCommand("kick");
                                 Intrinsics.checkNotNullExpressionValue(liteCommand, "");
                                 KickCommand kickCommand = (KickCommand)liteCommand;
                                 Template template = this.getConfigModule().getTemplateManager().computeTemplate(PunishmentType.KICK, b3);
                                 if (template == null) {
                                    template = Template.TEMPLATE_COMPANION.obtainTemplate();
                                 }

                                 Template template2 = template;
                                 PunishmentType punishmentType2 = PunishmentType.KICK;
                                 LoginRecord loginRecord = new LoginRecord(liteSender4.getName(), liteSender4.getUuidString(), liteSender4.getIpAddress(), new Date());
                                 String text18 = text13;
                                 if (text13 == null) {
                                    text18 = "";
                                 }

                                 String text19 = text18;
                                 String serverOrigin = punishment.getServerOrigin();
                                 String serverScope = punishment.getServerScope();
                                 if (serverScope == null) {
                                    serverScope = ServerScopes.SERVER_SCOPE.getLocalScope();
                                 }

                                 String text20 = serverScope;
                                 PunishmentRequest punishmentRequest = new PunishmentRequest(
                                    punishmentType2,
                                    false,
                                    flag2,
                                    flag3,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    currentTime,
                                    0L,
                                    0L,
                                    (byte)0,
                                    template2,
                                    null,
                                    false,
                                    null,
                                    value8,
                                    null,
                                    loginRecord,
                                    null,
                                    null,
                                    liteSender4,
                                    text19,
                                    text12,
                                    text20,
                                    serverOrigin,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    112967666,
                                    31,
                                    null
                                 );
                                 punishmentRequest.setPunishment(punishment);
                                 kickCommand.handlePunishmentRequest(punishmentRequest, databaseSession, liteSender4);
                              }
                           }
                           break;
                        case 14:
                           DatabaseModule databaseModule2 = databaseSession.getDatabaseModule();
                           int value10 = 21600000 + new KotlinRandom(0L, 1, null).nextInt(32400000);
                           databaseModule2.setLastExpiredUpdate(currentTime + value10);
                           this.intValue2 = 0;
                           break;
                        case 15:
                           sendSyncMessageWithDefaults(this, SyncMessageType.PONG, databaseSession, string, 0, 8, null);
                           break;
                        case 16:
                           DatabaseModule.registerServerWithDefaults(databaseSession.getDatabaseModule(), databaseSession, this.getConfigModule().getSettings().getSyncPollTicks() * 2, false, 4, null);
                           break;
                        case 17:
                           databaseSession.getDatabaseModule().perform4();
                     }
                  }
               }
            }
         }

         int offlineTabSize = this.getConfigModule().getSettings().getOfflineTabSize();
         if (offlineTabSize > 0 && (this.intValue2 <= 0 || this.byteValue >= 32)) {
            this.intValue2 = 0;
            ArrayList arrayList2 = new ArrayList(6000);
            int[] ints = new int[4];
            long value11 = System.currentTimeMillis();
            int value12 = offlineTabSize * 2;
            if (value12 >= 60000) {
               value12 = offlineTabSize;
            }

            ResultSet selectRecentNames2 = this.selectRecentNames(databaseSession, value12);
            HashSet hashSet2 = new HashSet(6000);
            HashMap hashMap = new HashMap();
            Throwable error = null;

            try {

               while (selectRecentNames2.next()) {
                  String string2 = selectRecentNames2.getString("name");
                  String lowerCase = string2.toLowerCase(Locale.ENGLISH);
                  if (lowerCase.length() >= 2) {
                     if (lowerCase.charAt(0) == '.' || lowerCase.charAt(0) == '*') {
                        lowerCase = lowerCase.substring(1);
                     }

                     if (hashSet2.add(lowerCase)) {
                        Timestamp timestamp = selectRecentNames2.getTimestamp("date");
                        long value13 = timestamp != null ? timestamp.getTime() : System.currentTimeMillis();
                        arrayList2.add(TuplesKt.computePair(value13, lowerCase));
                     }
                  }
               }

            } catch (Throwable e) {
               error = e;
               throw SneakyThrow.<RuntimeException>rethrow(e);
            } finally {
               KotlinUtil15.handleAutoCloseable(selectRecentNames2, error);
            }

            if (hashSet2.size() <= 5) {
               this.intValue2 = 1;
               return value2;
            }

            List list = arrayList2;
            if (list.size() > 1) {
               CollectionsKt.handleList(list, new KotlinComparatorImpl5());
            }

            Iterable take2 = CollectionsKt.take(arrayList2, offlineTabSize);

            for (Object obj3 : take2) {
               Pair pair = (Pair)obj3;
               int value14 = this.computeIntValue(StringsKt.computeCharValue((CharSequence)pair.getSecond()));
               Map map = hashMap;
               Integer boxed3 = value14;
               Object obj4 = map.get(boxed3);
               Object obj5;
               if (obj4 == null) {
                  List arrayList3 = new ArrayList((32 + arrayList2.size()) / 5);
                  map.put(boxed3, arrayList3);
                  obj5 = arrayList3;
               } else {
                  obj5 = obj4;
               }

               List list2 = (List)obj5;
               list2.add(pair);
               ints[value14] += ((String)pair.getSecond()).length() + 1;
            }

            KotlinData5 kotlinData5 = (KotlinData5)KotlinUtil2.KOTLIN_UTIL2_COMPANION.obtainUnsafeLazyImpl().getValue();
            synchronized (kotlinData5) {
               ResultSet resultSet3;
               if (this.byteValue >= 20 && currentTime - databaseSession.getDatabaseModule().getLastExpiredUpdate() > 57600000L) {
                  this.clearCacheEntries(databaseSession);
                  resultSet3 = ResultSetUtil.obtainProxyResultSet();
               } else {
                  resultSet3 = this.selectCacheEntries(databaseSession);
               }

               byte b4 = 0;
               int length3 = ((Object[])this.bytesArray5).length;
               byte[][] bytesArray = new byte[length3][];

               while (b4 < length3) {
                  byte b5 = b4;
                  bytesArray[b5] = StringMatching.obtainBytes();
                  b4 += 1;
               }

               ResultSet resultSet4 = resultSet3;
               Throwable error2 = null;

               try {

                  while (resultSet4.next()) {
                     bytesArray[resultSet4.getInt("id")] = resultSet4.getBytes("b");
                  }

               } catch (Throwable e2) {
                  error2 = e2;
                  throw SneakyThrow.<RuntimeException>rethrow(e2);
               } finally {
                  KotlinUtil15.handleAutoCloseable(resultSet4, error2);
               }

               this.copyOnWriteArrayList.clear();
               this.bytesArray4 = new byte[0][];
               boolean flag4 = hashMap.size() >= ((Object[])bytesArray).length;
               byte b6 = 0;
               int length4 = ((Object[])this.bytesArray5).length;
               byte[][] bytesArray2 = new byte[length4][];

               while (b6 < length4) {
                  byte b7 = b6;
                  bytesArray2[b7] = StringMatching.obtainBytes();
                  b6 += 1;
               }

               kotlinData5.setBytesArray(bytesArray2);
               KotlinRandom kotlinRandom = new KotlinRandom(kotlinData5.getLongValue() << 32 << 32);
               byte[] bytes = new byte[40];
               Iterable iterable = hashMap.entrySet();
               Iterable sortedWith2 = CollectionsKt.sortedWith((Iterable)iterable, new SyncMessageComparator());

               for (Object obj6 : sortedWith2) {
                  Entry entry = (Entry)obj6;
                  byte[] bytes2 = null;
                  Object[] objs2 = (Object[])bytesArray;
                  byte b8 = 0;

                  boolean flag5;
                  label1166: {
                     for (int i3 = objs2.length; b8 < i3; b8 += 1) {
                        Object obj7 = objs2[b8];
                        byte[] bytes3 = (byte[])obj7;
                        if (bytes3.length == 0) {
                           flag5 = false;
                           break label1166;
                        }
                     }

                     flag5 = true;
                  }

                  if (flag5) {
                     bytes2 = bytesArray[((Number)entry.getKey()).intValue()];
                     flag4 = false;
                  }

                  Intrinsics.checkNotNull(entry);
                  byte[] bytes4 = bytes2;
                  if (bytes2 == null) {
                     bytes4 = StringMatching.obtainBytes();
                  }

                  byte[] bytes5 = bytes4;
                  Iterable value15 = (Iterable)entry.getValue();
                  var arrayList4 = new ArrayList();

                  for (Object obj8 : value15) {
                     label1192: {
                        Pair pair2 = (Pair)obj8;
                        if (((String)pair2.getSecond()).length() < 20) {
                           CharSequence charSequence = (CharSequence)pair2.getSecond();

                           label1188: {
                              for (int i4 = 0; i4 < charSequence.length(); i4++) {
                                 char charAt2 = charSequence.charAt(i4);
                                 if (('a' <= charAt2 ? charAt2 >= '{' : true) && ('0' <= charAt2 ? charAt2 >= ':' : true) && charAt2 != ',') {
                                    flag5 = false;
                                    break label1188;
                                 }
                              }

                              flag5 = true;
                           }

                           if (flag5) {
                              flag5 = true;
                              break label1192;
                           }
                        }

                        flag5 = false;
                     }

                     if (flag5) {
                        arrayList4.add(obj8);
                     }
                  }

                  Iterable iterable2 = arrayList4;
                  var arrayList5 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList4, 10));

                  for (Object obj9 : iterable2) {
                     Pair pair3 = (Pair)obj9;
                     arrayList5.add((String)pair3.getSecond());
                  }

                  List sortedWith3 = CollectionsKt.sortedWith((Iterable)arrayList5, new KotlinComparatorImpl2(kotlinData5));
                  String joinToString = CollectionsKt.joinToStringWithDefaults(sortedWith3, ",", null, null, 0, null, null, 62, null);
                  byte[] bytes6 = joinToString.getBytes(KotlinCharsets.CHARSET_5);
                  int value16 = 0;
                  int value17;
                  byte b9 = 0;

                  for (int i5 = bytes6.length; b9 < i5; b9 += 1) {
                     byte b10 = bytes6[b9];
                     value17 = value16;
                     value16 += 1;
                     int value18 = value17;
                     bytes6[value18] = kotlinData5.getBytes6()[b10 - 44];
                  }

                  byte[] bytes7 = new byte[bytes6.length];
                  kotlinData5.setFlag6(kotlinData5.isFlag4());
                  if (bytes2 != null && bytes2.length != 0) {
                     kotlinData5.getBitflipper().setBytes2(bytes2);
                  } else {
                     byte[] bytes8 = new byte[8192];
                     kotlinData5.getBitflipper().setBytes2(bytes8);
                     kotlinData5.setFlag5(true);
                     KotlinData5.handleKotlinData(kotlinData5, bytes6, bytes7, false, false, 12, null);
                     bytes5 = kotlinData5.obtainBytes();
                     kotlinData5.setFlag5(false);
                  }

                  byte[][] bytesArray3 = kotlinData5.getBytesArray();
                  if (bytesArray3 != null) {
                     bytesArray3[((Number)entry.getKey()).intValue()] = kotlinData5.getBitflipper().getBytes2();
                  } else {
                     flag5 = false;
                  }

                  byte[] bytes9 = kotlinData5.computeBytes(this.platform, joinToString, true, kotlinData5.isFlag4(), kotlinRandom, bytes);
                  KotlinData5.handleKotlinData(kotlinData5, bytes9, bytes7, false, false, 12, null);
                  boolean flag6 = !this.checkKotlinData(kotlinData5);
                  if (flag6) {
                     KotlinData5.handleKotlinData(kotlinData5, bytes7, 600, false, false, 12, null);
                  }

                  byte[] bytes10;
                  if (flag6) {
                     ArrayList arrayList6 = new ArrayList(71);
                     bytes10 = KotlinData5.computeBytes(kotlinData5, bytes7, 0, 0, new KotlinFunction1Impl4(arrayList6, this), 6, null);
                     Collection collection3 = arrayList6;
                     this.handleBytesArray((byte[][]) collection3.toArray(new byte[0][]));
                     kotlinData5.perform2();
                  } else {
                     bytes10 = KotlinData5.computeBytes(kotlinData5, bytes7, 0, 0, null, 14, null);
                  }

                  ConfigModule configModule7 = this.getConfigModule();
                  if (configModule7.isVerbose()) {
                     StringBuilder builder = new StringBuilder()
                        .append("Bucket ")
                        .append(((Number)entry.getKey()).intValue())
                        .append(" filled with (")
                        .append(bytes9.length)
                        .append(" => ")
                        .append(bytes10.length)
                        .append(") bytes\n")
                        .append(ArraysKt.joinToStringWithDefaults(bytes7, ",", null, null, 0, null, null, 62, null))
                        .append('\n')
                        .append(joinToString)
                        .append('\n');
                     CommandArguments commandArguments2;
                     ConfigModule configModule8 = this.getConfigModule();
                     String joinToString2 = ArraysKt.joinToStringWithDefaults(bytes10, null, null, null, 0, null, null, 63, null);
                     String[] texts10;
                     if (Intrinsics.areEqual(joinToString2, "sort")) {
                        String[] texts11 = new String[]{"00"};
                        texts10 = texts11;
                     } else {
                        String[] texts12 = new String[]{"01:" + joinToString2};
                        texts10 = texts12;
                     }

                     String[] texts13 = texts10;
                     commandArguments2 = new CommandArguments(configModule8, Arrays.copyOf(texts13, texts13.length));
                     configModule7.logDebug(builder.append((Object)UInt.toStringImpl(commandArguments2.obtainIntValue())).toString());
                  }

                  kotlinData5.getBitflipper().setBytes2(StringMatching.obtainBytes());
                  access$bytesArray5(this)[((Number)entry.getKey()).intValue()] = bytes10;
                  this.handleIntValue(this.obtainIntValue2() + ((List)entry.getValue()).size());
                  byte[] bytes11 = bytes5;
                  if (flag4) {
                     try {
                        PunishmentTable punishmentTable2 = PunishmentTable.CACHE8K;
                        CharSequence message2 = "id,b";
                        DatabaseSession databaseSession2 = databaseSession;
                        byte b11 = 44;
                        byte b12 = 0;

                        for (byte b13 = 0; b13 < message2.length(); b13 += 1) {
                           char charAt3 = message2.charAt(b13);
                           if (charAt3 == b11) {
                              b12 += 1;
                           }
                        }

                        byte b14 = b12;
                        String text21 = "?";
                        if (b14 > 0) {
                           StringBuilder stringBuilder = new StringBuilder(b14 * 2);
                           Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(0, b14);
                           Iterator iterator2 = kotlinIntProgressionSub.iterator();

                           while (iterator2.hasNext()) {
                              int value19 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
                              stringBuilder.append("?,");
                           }

                           text21 = StringsKt.computeText(stringBuilder.toString(), 1);
                        }

                        PreparedQuery prepare2 = databaseSession2.prepare(SqlText.box(SqlText.constructorImpl("INSERT INTO " + punishmentTable2 + '(' + message2 + ")VALUES(" + text21 + ')')));
                        SqlUtil.computeResultSet(prepare2.bindInt(((Number)entry.getKey()).intValue()).bindBytes(bytes11));
                     } catch (Throwable e3) {
                        if (!(e3 instanceof SQLException)) {
                           throw SneakyThrow.<RuntimeException>rethrow(e3);
                        }
                        SQLException sqlException = (SQLException) e3;
                        String message3 = sqlException.getMessage();
                        if (message3 == null) {
                           message3 = "";
                        }

                        String text22 = message3;
                        if (!StringsKt.containsWithDefaults((CharSequence)text22, (CharSequence)"Duplicate entry", false, 2, null)
                           && !StringsKt.containsWithDefaults((CharSequence)text22, (CharSequence)"duplicate key", false, 2, null)) {
                           databaseSession.getDatabaseModule().logConnectionError(sqlException);
                        }
                     }
                  }
               }

               if (flag4) {
                  sendSyncMessageWithDefaults(this, SyncMessageType.EXPIRED_NOTIFY, databaseSession, null, 0, 12, null);
               }

            }

            long value20 = System.currentTimeMillis() - value11;
            this.byteValue = 0;
            int value21 = ArraysKt.sum(ints);
            Object[] objs3 = (Object[])this.bytesArray5;
            int value22 = 0;
            int value23 = 0;

            for (int i6 = objs3.length; value23 < i6; value23++) {
               Object obj10 = objs3[value23];
               byte[] bytes12 = (byte[])obj10;
               int value24 = value22;
               int length5 = bytes12.length;
               value22 = value24 + length5;
            }

            Object[] objs4 = (Object[])this.bytesArray4;
            int value25 = value22;
            int value26 = 0;
            int value27 = 0;

            for (int i7 = objs4.length; value27 < i7; value27++) {
               Object obj11 = objs4[value27];
               byte[] bytes13 = (byte[])obj11;
               int value28 = value26;
               int length6 = bytes13.length;
               value26 = value28 + length6;
            }

            int value29 = value26;
            int value30 = value25 + value29;
            int value31 = Math.abs(100 - (int)((double)value30 / value21 * 100.0));
            String text23 = " (reduced " + value31 + "% from " + value21 + " bytes)";
            if (value30 >= value21) {
               text23 = "";
            }

            this.getConfigModule()
               .logDebug("[Sync] Cached " + this.intValue2 + " offline users for tab completion in " + value20 + " ms. Memory used: " + value30 + " bytes" + text23);
         }
      } catch (Throwable e4) {
         databaseSession.getDatabaseModule().logConnectionError(e4);
      }

      return value2;
   }

   private final ResultSet selectRecentNames(DatabaseSession databaseSession, int value) {
      PunishmentTable punishmentTable = PunishmentTable.HISTORY;
      CharSequence message = "name,date";
      return SqlUtil.computeResultSet(databaseSession.prepare(SqlText.box(SqlText.limit(SqlText.orderByDesc(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable), "id"), value))));
   }

   public final ResultSet selectCacheEntries(@NotNull DatabaseSession databaseSession) {
      PunishmentTable punishmentTable = PunishmentTable.CACHE8K;
      CharSequence message = "*";
      return SqlUtil.computeResultSet(databaseSession.prepare(SqlText.box(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable))));
   }

   public final ResultSet clearCacheEntries(@NotNull DatabaseSession databaseSession) {
      PunishmentTable punishmentTable = PunishmentTable.CACHE8K;
      return SqlUtil.computeResultSet(databaseSession.prepare(SqlText.box(SqlText.constructorImpl("DELETE FROM " + punishmentTable))));
   }

   public final boolean checkKotlinData(@NotNull KotlinData5 kotlinData) {
      if (((Object[])this.bytesArray4).length == 0) {
         return false;
      }

      if (kotlinData.getList2().isEmpty()) {
         byte b = -128;
         Object[] objs = (Object[])this.bytesArray4;
         KotlinData5 kotlinData2 = kotlinData;
         var arrayList = new ArrayList(objs.length);
         int value = 0;

         for (int i = objs.length; value < i; value++) {
            Object obj = objs[value];
            byte[] bytes = (byte[])obj;
            byte b2 = b;
            b = (byte)(b2 + 1);
            arrayList.add(new KotlinUtil26(bytes, b2, (short)0, 4, null));
         }

         kotlinData2.setList2(arrayList);
      }

      return true;
   }

   public final ByteArrayOutputStream computeByteArrayOutputStream(int value) {
      return new ArchiveByteArrayOutputStreamSub(value);
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final KotlinHelper8 computeKotlinHelper(int value, @NotNull KotlinData5 kotlinData) {
      ConfigModule configModule = this.getConfigModule();
      if (configModule.isVerbose()) {
         StringBuilder builder = new StringBuilder()
            .append("Read bucket ")
            .append(value)
            .append(". ")
            .append(kotlinData.getList2().size())
            .append(" loaded, ")
            .append(((Object[])this.bytesArray4).length)
            .append(" stored\n")
            .append(CollectionsKt.joinToStringWithDefaults(kotlinData.getList2(), "|", null, null, 0, null, obj -> ArraysKt.joinToStringWithDefaults(((KotlinUtil26) obj).getBytes2(), null, null, null, 0, null, null, 63, null), 30, null))
            .append('\n');
         Object[] objs = (Object[])this.bytesArray4;
         var arrayList = new ArrayList(objs.length);
         int value2 = 0;

         for (int i = objs.length; value2 < i; value2++) {
            Object obj2 = objs[value2];
            byte[] bytes = (byte[])obj2;
            arrayList.add(ArraysKt.joinToStringWithDefaults(bytes, null, null, null, 0, null, null, 63, null));
         }

         configModule.logDebug(builder.append(arrayList).toString());
      }

      KotlinHelper8 kotlinHelper;
      label117: {
         KotlinHelper8 kotlinHelper2;
         synchronized (kotlinData) {
            this.checkKotlinData(kotlinData);
            if (kotlinData.getList2().isEmpty() || ((Object[])this.bytesArray4).length == 0) {
               this.getConfigModule().debug(10, "No cache");
               kotlinHelper = new KotlinHelper8((byte)value, KotlinStub15.obtainMap());
               break label117;
            }

            byte[] bytes2 = this.bytesArray5[value];
            ConfigModule configModule2 = this.getConfigModule();
            if (configModule2.isVerbose()) {
               configModule2.logDebug(ArraysKt.joinToStringWithDefaults(bytes2, null, null, null, 0, null, null, 63, null));
            }

            byte[][] bytesArray = kotlinData.getBytesArray();
            if (bytes2 == StringMatching.obtainBytes() || bytesArray == null || bytesArray[value].length == 0) {
               this.getConfigModule().debug(10, "No cache");
               return new KotlinHelper8((byte)value, KotlinStub15.obtainMap());
            }

            kotlinData.getBitflipper().setBytes2(bytesArray[value]);
            ByteArrayOutputStream byteArrayOutputStream = this.computeByteArrayOutputStream(bytes2.length * 4);
            kotlinData.handleBytes(bytes2, byteArrayOutputStream);
            byte[] bytes3 = new byte[byteArrayOutputStream.size()];
            kotlinData.setFlag6(kotlinData.isFlag4());
            byte[] copyOf2 = Arrays.copyOf(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.size());
            Intrinsics.checkNotNull(copyOf2);
            KotlinData5.handleKotlinData(kotlinData, copyOf2, bytes3, false, false, 8, null);
            ConfigModule configModule3 = this.getConfigModule();
            if (configModule3.isVerbose()) {
               configModule3.logDebug(ArraysKt.joinToStringWithDefaults(bytes3, null, null, null, 0, null, null, 63, null));
            }

            String string = kotlinData.computeStringBuilder(bytes3).toString();
            CharSequence message = string;
            char[] chars = new char[]{','};
            Collection split = StringsKt.splitWithDefaults(message, chars, false, 0, 6, null);
            String[] array = (String[])split.toArray(new String[0]);
            HashMap hashMap = new HashMap(array.length * 2, 0.5F);
            int value3 = 0;

            for (int i2 = array.length; value3 < i2; value3++) {
               String text = array[value3];
               if (text.length() > 0) {
                  int[] ints = this.computeInts(text);
                  int value4 = 0;

                  for (int i3 = ints.length; value4 < i3; value4++) {
                     int value5 = ints[value4];
                     Map map = hashMap;
                     Integer boxed = value5;
                     Object obj3 = map.get(boxed);
                     Object obj4;
                     if (obj3 == null) {
                        ArrayList arrayList2 = new ArrayList(64);
                        map.put(boxed, arrayList2);
                        obj4 = arrayList2;
                     } else {
                        obj4 = obj3;
                     }

                     ((ArrayList)obj4).add(text);
                  }
               }
            }

            Map map2 = hashMap;

            for (Entry entry : (Iterable<Entry>) map2.entrySet()) {
               ((ArrayList)entry.getValue()).trimToSize();
            }

            Platform platform = this.platform;
            if (platform != null) {
               ConfigModule module = (ConfigModule)platform.getModule(ConfigModule.class);
               if (module != null) {
                  CommandArguments commandArguments;
                  String string2 = string.toString();
                  String[] texts;
                  if (Intrinsics.areEqual(string2, "sort")) {
                     String[] texts2 = new String[]{"00"};
                     texts = texts2;
                  } else {
                     String[] texts3 = new String[]{"01:" + string2};
                     texts = texts3;
                  }

                  String[] texts4 = texts;
                  commandArguments = new CommandArguments(module, Arrays.copyOf(texts4, texts4.length));
                  if (commandArguments.obtainIntValue() == module.hashCode()) {
                     throw new IllegalArgumentException();
                  }
               }
            }

            KotlinHelper8 kotlinHelper8 = new KotlinHelper8((byte)value, new HashMap(hashMap));
            this.copyOnWriteArrayList.add(new SoftReference<>(kotlinHelper8));
            kotlinHelper2 = kotlinHelper8;
         }

         return kotlinHelper2;
      }

      KotlinData5 kotlinData2;
      // $VF: monitorexit
      return kotlinHelper;
   }

   public final int computeIntValue(char c) {
      return c % ((Object[])this.bytesArray5).length;
   }

   public final int[] computeInts(@NotNull String text) {
      if (text.length() == 0) {
         return new int[]{0};
      }

      int value = text.charAt(0) * 3;
      if (text.length() <= 1) {
         return new int[]{value};
      }

      int value2 = text.substring(0, 2).hashCode() * 13;
      if (text.length() <= 2) {
         return new int[]{value2, value};
      }

      int value3 = text.substring(0, 3).hashCode() * 17;
      return new int[]{value3, value2, value};
   }

   public static final byte[][] access$bytesArray5(SyncModule syncModule) {
      return syncModule.bytesArray5;
   }
}
