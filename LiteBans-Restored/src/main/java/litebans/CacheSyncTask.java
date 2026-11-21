package litebans;

import java.io.Closeable;
import java.lang.ref.Reference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public final class CacheSyncTask extends CommandTask implements Runnable {
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private final CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();

   public CacheSyncTask(@NotNull Platform platform) {
      super(platform);
   }

   public final AtomicBoolean getAtomicBoolean() {
      return this.atomicBoolean;
   }

   public final CopyOnWriteArrayList getCopyOnWriteArrayList() {
      return this.copyOnWriteArrayList;
   }

   @Override
   public void run() {
      try {
         DatabaseModule module = (DatabaseModule)this.getPlatform().getModule(DatabaseModule.class);
         SyncModule module2 = (SyncModule)this.getPlatform().getModule(SyncModule.class);
         CommandTask commandTask = this;
         ConfigModule module3 = (ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class);
         if (module3.isVerbose()) {
            module3.logDebug(KotlinUtil2.KOTLIN_UTIL2_COMPANION.obtainUnsafeLazyImpl().isInitialized());
         }

         KotlinData5 kotlinData5 = (KotlinData5)KotlinUtil2.KOTLIN_UTIL2_COMPANION.obtainUnsafeLazyImpl().getValue();
         Intrinsics.checkNotNull(module);
         Intrinsics.checkNotNull(module2);
         this.handleKotlinCommandTaskSub(this, kotlinData5, module, module2);
         byte b = 100;

         for (int i = 0; i < b; i++) {
            this.handleKotlinCommandTaskSub(this, kotlinData5, module, module2);
            Thread.sleep(70L);
         }
      } catch (InterruptedException e) {
         this.perform4();
      }

      this.atomicBoolean.set(false);
   }

   private final void perform4() {
      this.copyOnWriteArrayList.clear();
   }

   private final void handleKotlinCommandTaskSub(CacheSyncTask kotlinCommandTaskSub, KotlinData5 kotlinData, DatabaseModule databaseModule, SyncModule syncModule) {
      try {
         boolean flag = false;
         if (!kotlinCommandTaskSub.copyOnWriteArrayList.isEmpty()) {
            byte[][] bytesArray = kotlinData.getBytesArray();
            if (bytesArray == null) {
               int value = 0;
               byte[][] bytesArray2 = new byte[4][];

               while (value < 4) {
                  int value2 = value;
                  bytesArray2[value2] = StringMatching.obtainBytes();
                  value++;
               }

               bytesArray = bytesArray2;
            }

            byte[][] bytesArray3 = bytesArray;
            kotlinData.setBytesArray(bytesArray3);
            Object[] objs = (Object[])bytesArray3;
            int value3 = 0;
            int length2 = objs.length;

            while (true) {
               if (value3 >= length2) {
                  flag = false;
                  break;
               }

               Object obj = objs[value3];
               byte[] bytes = (byte[])obj;
               if (bytes.length == 0) {
                  flag = true;
                  break;
               }

               value3++;
            }

            if (flag) {

               try {
                  DatabaseSession session = databaseModule.openSession();
                  Throwable error = null;

                  try {
                     if (session.isOpen()) {
                        try {
                           ResultSet selectCacheEntries2 = syncModule.selectCacheEntries(session);
                           Throwable error2 = null;

                           try {

                              while (selectCacheEntries2.next()) {
                                 bytesArray3[selectCacheEntries2.getInt("id")] = selectCacheEntries2.getBytes("b");
                              }

                           } catch (Throwable e2) {
                              error2 = e2;
                              throw SneakyThrow.<RuntimeException>rethrow(e2);
                           } finally {
                              KotlinUtil15.handleAutoCloseable(selectCacheEntries2, error2);
                           }
                        } catch (Throwable e3) {
                           if (!(e3 instanceof SQLException)) {
                              throw SneakyThrow.<RuntimeException>rethrow(e3);
                           }
                           SQLException sqlException = (SQLException) e3;
                           databaseModule.logConnectionError(sqlException);
                        }
                     }

                  } catch (Throwable e4) {
                     error = e4;
                     throw SneakyThrow.<RuntimeException>rethrow(e4);
                  } finally {
                     CloseableKt.handleCloseable(session, error);
                  }
               } catch (Throwable e5) {
                  if (!(e5 instanceof SQLException)) {
                     throw SneakyThrow.<RuntimeException>rethrow(e5);
                  }
                  SQLException sqlException2 = (SQLException) e5;
                  if (!databaseModule.isConnectionFailure(sqlException2)) {
                     throw sqlException2;
                  }
               }
            }

            try {
               Iterable copyOnWriteArrayList2 = kotlinCommandTaskSub.copyOnWriteArrayList;

               for (Object obj2 : copyOnWriteArrayList2) {
                  Byte byte2 = (Byte)obj2;
                  if (byte2 >= 0) {
                     var copyOnWriteArrayList3 = syncModule.getCopyOnWriteArrayList();
                     boolean flag2;
                     if (copyOnWriteArrayList3 instanceof Collection && copyOnWriteArrayList3.isEmpty()) {
                        flag2 = true;
                     } else {
                        label367: {
                           for (Object obj3 : copyOnWriteArrayList3) {
                              Reference reference = (Reference)obj3;
                              KotlinHelper8 kotlinHelper8 = (KotlinHelper8)reference.get();
                              boolean flag3;
                              if (kotlinHelper8 != null) {
                                 byte b = kotlinHelper8.getByteValue();
                                 flag3 = byte2 == b;
                              } else {
                                 flag3 = false;
                              }

                              if (flag3) {
                                 flag2 = false;
                                 break label367;
                              }
                           }

                           flag2 = true;
                        }
                     }

                     if (flag2) {
                        syncModule.computeKotlinHelper(byte2, kotlinData);
                     }
                  }
               }

               syncModule.checkKotlinData(kotlinData);
            } catch (IndexOutOfBoundsException e6) {
               kotlinCommandTaskSub.handleException(e6, kotlinData, syncModule);
            }

            kotlinCommandTaskSub.perform4();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleException(Exception exception, KotlinData5 kotlinData, SyncModule syncModule) {
      try {
         String text;
         Logger logger;
         StringBuilder builder;
         label16: {
            logger = this.getPlatform().getLogger();
            builder = new StringBuilder().append("Cache desync, reloading. (error ");
            text = exception.getMessage();
            if (text != null) {
               CharSequence message = text;
               char[] chars = new char[]{' '};
               List split = StringsKt.splitWithDefaults(message, chars, false, 0, 6, null);
               if (split != null) {
                  text = (String)split.get(1);
                  break label16;
               }
            }

            text = null;
         }

         logger.warning(builder.append(text).append(')').toString());
         kotlinData.setBytesArray(null);
         syncModule.handleIntValue(0);
         CommandTask commandTask = this;
         ConfigModule module = (ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class);
         if (module.isVerbose()) {
            module.logDebug(exception);
         }

         Thread.sleep(1000L);
      } catch (InterruptedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
