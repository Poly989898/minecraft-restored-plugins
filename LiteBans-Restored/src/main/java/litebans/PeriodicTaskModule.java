package litebans;

import java.io.Closeable;
import java.lang.ref.Reference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import org.jetbrains.annotations.NotNull;

public final class PeriodicTaskModule extends PluginModule implements Runnable {
   public static final PeriodicTaskModuleCompanion PERIODIC_TASK_MODULE_COMPANION = new PeriodicTaskModuleCompanion(null);
   private final Lazy databaseModuleLazy = KotlinStub5.computeLazy(() -> (DatabaseModule)this.platform.getModule(DatabaseModule.class));
   public static final long VALUE_48000 = 48000L;

   public PeriodicTaskModule(@NotNull Platform platform) {
      super(platform);
   }

   public final DatabaseModule obtainDatabaseModule() {
      Lazy lazy = this.databaseModuleLazy;
      return (DatabaseModule)lazy.getValue();
   }

   @Override
   public void enable() {
      this.platform.runAsyncTimer(this, 48000L, 48000L);
   }

   @Override
   public void reloadConfig() {
      this.disable();
      this.enable();
   }

   @Override
   public void run() {
      try {
         DatabaseModule databaseModule = this.obtainDatabaseModule();

         try {
            DatabaseSession session = databaseModule.openSession();
            Throwable error = null;

            try {
               this.handleDatabaseSession2(session);
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
            if (!databaseModule.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final void handleDatabaseSession2(@NotNull DatabaseSession databaseSession) {
      Object obj = null;
      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      PunishmentLookupModule module2 = (PunishmentLookupModule)databaseSession.getPlatform().getModule(PunishmentLookupModule.class);
      SyncModule module3 = (SyncModule)databaseSession.getPlatform().getModule(SyncModule.class);
      LiteSender[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
      Intrinsics.checkNotNull(onlineSenders);
      if (onlineSenders.length == 0) {
         if (module.getSettings().isMutesEnabled()) {
            module2.getMuteChatTask().getMutedPlayers().clear();
         }

         if (databaseSession.getPlatform().getPlatformId() == 1) {
            this.handleDatabaseSession3(databaseSession);
         }
      } else {
         if (module.getSettings().isMutesEnabled()) {
            module2.getMuteChatTask().getMutedPlayers().values().removeIf(obj2 -> (Boolean)((Function1) obj).computeObj(obj2));
         }

         if (databaseSession.getPlatform().getPlatformId() == 1) {
            this.handleDatabaseSession(databaseSession, onlineSenders);
         }
      }

      byte b = module3.obtainByteValue();
      module3.handleByteValue((byte)(b + 1));
      if (module3.getCopyOnWriteArrayList().size() > 8) {
         Iterable iterable = module3.getCopyOnWriteArrayList();
         var arrayList = new ArrayList();

         for (Object obj3 : iterable) {
            Reference reference = (Reference)obj3;
            if (reference.get() != null) {
               arrayList.add(obj3);
            }
         }

         List list = arrayList;
         if (list.isEmpty()) {
            module3.getCopyOnWriteArrayList().clear();
         } else {
            module3.handleCopyOnWriteArrayList(new CopyOnWriteArrayList(list));
         }
      }
   }

   private final void handleDatabaseSession3(DatabaseSession databaseSession) {
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      LoginHandler loginHandler = module.getLoginHandler();
      Intrinsics.checkNotNullExpressionValue(loginHandler, "");
      BungeeLoginHandler bungeeLoginHandler = (BungeeLoginHandler)loginHandler;
      bungeeLoginHandler.getConcurrentHashMap2().clear();
      bungeeLoginHandler.getConcurrentHashMap3().clear();
   }

   private final void handleDatabaseSession(DatabaseSession databaseSession, LiteSender[] liteSenders) {
      Object obj = null;
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      LoginHandler loginHandler = module.getLoginHandler();
      Intrinsics.checkNotNullExpressionValue(loginHandler, "");
      BungeeLoginHandler bungeeLoginHandler = (BungeeLoginHandler)loginHandler;
      bungeeLoginHandler.getConcurrentHashMap2().values().removeIf(obj2 -> (Boolean)((Function1) obj).computeObj(obj2));
      if (bungeeLoginHandler.getConcurrentHashMap2().isEmpty()) {
         bungeeLoginHandler.getConcurrentHashMap3().clear();
      } else {
         ((KeySetView)bungeeLoginHandler.getConcurrentHashMap3().keySet()).removeIf(obj3 -> (Boolean)((Function1) obj).computeObj(obj3));
      }
   }

   public final boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull LiteSender[] liteSenders, @NotNull Punishment punishment) {
      LiteSender[] liteSenders2 = liteSenders;
      int value = 0;
      int length2 = liteSenders2.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = true;
            break;
         }

         LiteSender liteSender = liteSenders2[value];
         if (databaseSession.isAffecting(punishment, liteSender)) {
            flag = false;
            break;
         }

         value++;
      }

      return flag;
   }

   private static final boolean checkPeriodicTaskModule(PeriodicTaskModule periodicTaskModule, DatabaseSession databaseSession, LiteSender[] liteSenders, Punishment punishment) {
      Intrinsics.checkNotNull(liteSenders);
      Intrinsics.checkNotNull(punishment);
      return periodicTaskModule.checkDatabaseSession(databaseSession, liteSenders, punishment);
   }

   private static final boolean checkPeriodicTaskModule(PeriodicTaskModule periodicTaskModule, DatabaseSession databaseSession, LiteSender[] liteSenders, Collection collection) {
      Intrinsics.checkNotNull(collection);
      boolean flag;
      if (((Collection)collection).isEmpty()) {
         flag = false;
      } else {
         for (Object obj : collection) {
            Punishment punishment = (Punishment)obj;
            if (!periodicTaskModule.checkDatabaseSession(databaseSession, liteSenders, punishment)) {
               return false;
            }
         }

         flag = false;
      }

      return !flag;
   }

   private static final boolean checkBungeeLoginHandler(BungeeLoginHandler bungeeLoginHandler, Punishment punishment) {
      var collection = bungeeLoginHandler.getConcurrentHashMap2().values();
      boolean flag;
      if (((Collection)collection).isEmpty()) {
         flag = true;
      } else {
         Iterator iterator2 = collection.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = true;
               break;
            }

            Object obj = iterator2.next();
            Collection collection2 = (Collection)obj;
            Intrinsics.checkNotNull(collection2);
            if (collection2 instanceof Collection && ((Collection)collection2).isEmpty()) {
               flag = false;
            } else {
               label48: {
                  for (Object obj2 : collection2) {
                     Punishment punishment2 = (Punishment)obj2;
                     if (punishment2.getId() == punishment.getId()) {
                        flag = true;
                        break label48;
                     }
                  }

                  flag = false;
               }
            }

            if (flag) {
               flag = false;
               break;
            }
         }
      }

      return flag;
   }

}
