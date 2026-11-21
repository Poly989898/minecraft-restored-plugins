package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;

public final class CommandQueueModule extends PluginModule implements Runnable {
   public static final CommandQueueModuleCompanion COMMAND_QUEUE_MODULE_COMPANION = new CommandQueueModuleCompanion(null);
   private final ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(2048);
   private final AtomicBoolean atomicBoolean7 = new AtomicBoolean();
   public static final long VALUE_64 = 64L;
   public static final int VALUE_2048 = 2048;

   public CommandQueueModule(@NotNull Platform platform) {
      super(platform);
   }

   public final ArrayBlockingQueue getArrayBlockingQueue() {
      return this.arrayBlockingQueue;
   }

   @Override
   public void enable() {
      AtomicBoolean atomicBoolean = this.atomicBoolean7;
      atomicBoolean.set(false);
      this.platform.runAsyncTimer(this, 64L, 64L);
   }

   @Override
   public void run() {
      AtomicBoolean atomicBoolean = this.atomicBoolean7;
      if (atomicBoolean.compareAndSet(false, true)) {
         DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);

         try {
            int value = this.getArrayBlockingQueue().size() <= 256
               ? 0
               : (this.getArrayBlockingQueue().size() <= 512 ? 1 : (this.getArrayBlockingQueue().size() <= 1024 ? 2 : (this.getArrayBlockingQueue().size() < 2048 ? 3 : 4)));
            short value2;
            switch (value) {
               case 0:
                  value2 = 40;
                  break;
               case 1:
                  value2 = 60;
                  break;
               case 2:
                  value2 = 256;
                  break;
               case 3:
                  value2 = 512;
                  break;
               case 4:
                  value2 = 2048;
                  break;
               default:
                  StandardKt.notImplemented();
                  throw new CommandAbortException();
            }

            short value3 = value2;
            if (DatabaseModule.isConnectionAvailableWithDefaults(module, 0, 1, null) && value == 0) {
               value3 = 1;
            }


            try {
               DatabaseSession session = module.openSession();
               Throwable error = null;

               try {
                  int value4 = 1;
                  short value5 = value3;
                  if (value4 <= value5) {
                     while (true) {
                        PlayerInfo poll2 = (PlayerInfo)this.arrayBlockingQueue.poll();
                        if (poll2 == null) {
                           break;
                        }

                        PunishmentQueries.saveLoginWithDefaults(session, poll2.getName(), poll2.getUuid(), poll2.getIp(), false, false, 24, null);
                        if (value4 == value5) {
                           break;
                        }

                        value4++;
                     }
                  }

                  return;
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
               if (!module.isConnectionFailure(sqlException)) {
                  throw sqlException;
               }

               return;
            }
         } catch (Exception e3) {
            if (!(e3 instanceof InterruptedException)) {
               module.logConnectionError(e3);
               return;
            }
         } finally {
            atomicBoolean.set(false);
         }
      }
   }

   @Override
   public void reloadConfig() {
      this.disable();
      this.enable();
   }
}
