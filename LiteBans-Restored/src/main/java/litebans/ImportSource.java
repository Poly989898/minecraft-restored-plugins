package litebans;

import java.sql.Connection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PluginMarker
public abstract class ImportSource extends CommandTask {
   private final String source;
   private String tablePrefix;
   private final int batchSize;
   private final long batchDelayMillis;
   private final Logger logger;
   private final Lazy uuidCache;
   private final Lazy importedCount;
   private final Lazy failedCount;

   public ImportSource(@NotNull Platform platform, @NotNull String text, @NotNull String text2, int value, long value2) {
      super(platform);
      this.source = text;
      this.tablePrefix = text2;
      this.batchSize = value;
      this.batchDelayMillis = value2;
      this.logger = platform.getLogger();
      this.uuidCache = KotlinStub5.computeLazy(() -> new HashMap());
      this.importedCount = KotlinStub5.computeLazy(() -> new AtomicLong());
      this.failedCount = KotlinStub5.computeLazy(() -> new AtomicLong());
   }

   public ImportSource(Platform platform, String text, String text2, int value, long value2, int value3, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value3 & 4) != 0) {
         text2 = "";
      }

      if ((value3 & 8) != 0) {
         value = 4096;
      }

      if ((value3 & 16) != 0) {
         value2 = 5000L;
      }

      this(platform, text, text2, value, value2);
   }

   public final String getSource() {
      return this.source;
   }

   public final String getTablePrefix() {
      return this.tablePrefix;
   }

   public final void setTablePrefix(@NotNull String text) {
      this.tablePrefix = text;
   }

   public final int getBatchSize() {
      return this.batchSize;
   }

   public final long getBatchDelayMillis() {
      return this.batchDelayMillis;
   }

   public final Logger getLogger() {
      return this.logger;
   }

   public final HashMap obtainHashMap() {
      Lazy lazy = this.uuidCache;
      return (HashMap)lazy.getValue();
   }

   public final AtomicLong obtainAtomicLong() {
      Lazy lazy = this.importedCount;
      return (AtomicLong)lazy.getValue();
   }

   public final AtomicLong obtainAtomicLong2() {
      Lazy lazy = this.failedCount;
      return (AtomicLong)lazy.getValue();
   }

   public boolean isSource(@NotNull String text) {
      return StringsKt.checkText(this.source, text, true);
   }

   public boolean obtainFlag5() {
      return true;
   }

   public boolean obtainFlag6() {
      return true;
   }

   public void handleLiteSender(@NotNull LiteSender liteSender) {
      CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, Placeholders.replacePlaceholder(Message.IMPORT_FINISH.computeText("bans", this.obtainAtomicLong()), "ipbans", this.obtainAtomicLong2()), null, 4, null);
   }

   public abstract void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag);

   public Void obtainVoid3() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public final String computeText(@Nullable String text) {
      if (text != null && !StringsKt.startsWithWithDefaults(text, '#', false, 2, null)) {
         String string = (String)this.obtainHashMap().get(text);
         if (string != null) {
            return string;
         }

         CommandTask commandTask = this;
         boolean isOnlineMode2 = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isOnlineMode();
         String text2;
         if (isOnlineMode2) {
            Profile profile = UsernameValidator.KOTLIN_DATA.computeProfile(text);
            if (profile == null) {
               return "#";
            }

            text2 = String.valueOf(profile.getUUID());
         } else {
            text2 = TextUtil.offlineUuid(text);
         }

         String text3 = text2;
         this.obtainHashMap().put(text, text3);
         return text3;
      } else {
         return "#";
      }
   }

   public String computeText(@NotNull UUID uuid) {
      String text = UsernameValidator.KOTLIN_DATA.computeText(uuid.toString());
      if (text == null) {
         text = "#";
      }

      return text;
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull AtomicLong atomicLong, @NotNull AtomicLong atomicLong2) {
      String ip = punishment.getIp();
      if (PunishmentQueries.findPunishmentWithDefaults(databaseSession, null, ip, punishment.getType(), false, false, 24, null) == null) {
         databaseSession.insertPunishment(punishment);
         if (punishment.getType() == PunishmentType.BAN) {
            AtomicLong[] atomicLongs = new AtomicLong[]{atomicLong, atomicLong2};
            int value = 0;

            for (int i = atomicLongs.length; value < i; value++) {
               AtomicLong atomicLong3 = atomicLongs[value];
               atomicLong3.incrementAndGet();
            }
         }
      } else {
         this.getPlatform().getLogger().warning("Ignoring duplicate ban for " + ip);
      }
   }

   public final void importPunishment(
      @NotNull DatabaseSession databaseSession,
   @Nullable String text,
   @NotNull String text2,
   long value,
   long value2,
   @Nullable String text3,
   @Nullable UUID uuid,
   @Nullable UUID uuid2,
   @Nullable String text4,
   @NotNull AtomicLong atomicLong,
   @NotNull PunishmentType punishmentType
   ) {
      String text5;
      label57: {
         if (uuid != null) {
            text5 = uuid.toString();
            if (text5 != null) {
               break label57;
            }
         }

         text5 = this.computeText(text);
      }

      String text6;
      label52: {
         text6 = text5;
         if (uuid2 != null) {
            text5 = uuid2.toString();
            if (text5 != null) {
               break label52;
            }
         }

         text5 = this.computeText(text3);
      }

      String text7 = text5;
      if (text4 != null || !StringsKt.checkText(text6, "#", false, 2, null)) {
         CommandTask commandTask = this;
         Punishment punishment2 = PunishmentQueries.findPunishmentWithDefaults(databaseSession, text6, text4, punishmentType, ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).getServerName(), false, false, 48, null);
         if (punishmentType != PunishmentType.WARN && punishment2 != null && Intrinsics.areEqual(punishment2.getUuid(), text6)) {
            this.getPlatform().getLogger().warning("Ignoring duplicate " + punishmentType + " for " + text + " (ip=" + text4 + ')');
         } else {
            boolean flag = text4 != null;
            CommandTask commandTask2 = this;
            String text8 = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getServerName();
            CommandTask commandTask3 = this;
            String scope = ServerScopes.SERVER_SCOPE.resolveScope(((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().getDefaultServerScope(), text8, this.getPlatform().getConsole());
            Punishment punishment = new Punishment(punishmentType, text6, text4, text2, text7, text3, scope, text8, value, value2, (byte)0, false, flag, false, 0L, 27648, null);
            databaseSession.insertPunishment(punishment);
            if (punishmentType == PunishmentType.BAN) {
               atomicLong.incrementAndGet();
               if (flag) {
                  this.obtainAtomicLong2().incrementAndGet();
               }
            }
         }

         if (text != null && text6 != null) {
            new QueueCommandTask(this.getPlatform(), text, text6, "#").run();
         }

         if (text3 != null && text7 != null) {
            new QueueCommandTask(this.getPlatform(), text3, text7, "#").run();
         }
      }
   }

   public static void importPunishmentWithDefaults(
      ImportSource punishCommandBase,
      DatabaseSession databaseSession,
      String text,
      String text2,
      long value,
      long value2,
      String text3,
      UUID uuid,
      UUID uuid2,
      String text4,
      AtomicLong atomicLong,
      PunishmentType punishmentType,
      int value3,
      Object obj
   ) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value3 & 64) != 0) {
         uuid = null;
      }

      if ((value3 & 128) != 0) {
         uuid2 = null;
      }

      if ((value3 & 256) != 0) {
         text4 = null;
      }

      if ((value3 & 512) != 0) {
         atomicLong = punishCommandBase.obtainAtomicLong();
      }

      if ((value3 & 1024) != 0) {
         punishmentType = PunishmentType.BAN;
      }

      punishCommandBase.importPunishment(databaseSession, text, text2, value, value2, text3, uuid, uuid2, text4, atomicLong, punishmentType);
   }

   @Override
   public void run() {
      this.obtainVoid3();
   }
}
