package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntryQueueModule extends PluginModule implements Runnable {
   public static final EntryQueueModuleCompanion ENTRY_QUEUE_MODULE_COMPANION = new EntryQueueModuleCompanion(null);
   private final Lazy commandQueueModuleLazy = KotlinStub5.computeLazy(() -> (CommandQueueModule)this.platform.getModule(CommandQueueModule.class));
   private final ArrayBlockingQueue arrayBlockingQueue2 = new ArrayBlockingQueue(512);
   private final AtomicBoolean atomicBoolean3 = new AtomicBoolean();
   public static final long VALUE_80 = 80L;
   public static final long VALUE_30 = 30L;
   public static final int VALUE_512 = 512;

   public EntryQueueModule(@NotNull Platform platform) {
      super(platform);
   }

   private final CommandQueueModule obtainCommandQueueModule() {
      Lazy lazy = this.commandQueueModuleLazy;
      return (CommandQueueModule)lazy.getValue();
   }

   @Override
   public void enable() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      long value = 80L;
      if (module.getSettings().getByteValue() > 1) {
         value = 30L;
      }

      this.platform.runAsyncTimer(this, value, value);
   }

   @Override
   public void disable() {
      this.arrayBlockingQueue2.clear();
      AtomicBoolean atomicBoolean = this.atomicBoolean3;
      atomicBoolean.set(false);
   }

   @Override
   public void reloadConfig() {
      this.disable();
      this.enable();
   }

   @Override
   public void run() {
      AtomicBoolean atomicBoolean = this.atomicBoolean3;
      if (atomicBoolean.compareAndSet(false, true)) {
         DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);

         try {
            int value = this.arrayBlockingQueue2.size() >= 256 ? 3 : (this.arrayBlockingQueue2.size() >= 128 ? 2 : (this.arrayBlockingQueue2.size() >= 64 ? 1 : 0));
            byte b;
            switch (value) {
               case 0:
                  b = 20;
                  break;
               case 1:
                  b = 40;
                  break;
               case 2:
                  b = 50;
                  break;
               case 3:
                  b = 80;
                  break;
               default:
                  StandardKt.notImplemented();
                  throw new CommandAbortException();
            }

            byte b2 = b;
            if (!module.isConnectionAvailable(2) || value > 2) {
               int value2 = 1;
               if (value2 > b2) {
                  return;
               }

               while (true) {
                  PlayerInfo peek2 = (PlayerInfo)this.arrayBlockingQueue2.peek();
                  if (peek2 == null) {
                     return;
                  }

                  if (this.checkPlayerInfo(peek2)) {
                     return;
                  }

                  PlayerInfo poll2 = (PlayerInfo)this.arrayBlockingQueue2.poll();
                  if (poll2 != peek2) {
                     Intrinsics.checkNotNull(poll2);
                     if (this.checkPlayerInfo(poll2)) {
                        this.arrayBlockingQueue2.offer(poll2);
                        return;
                     }
                  }

                  Intrinsics.checkNotNull(poll2);
                  new DupeIpAlertTask(this.platform, poll2.getName(), poll2.getUuid(), poll2.getIp()).run();
                  if (value2 == b2) {
                     return;
                  }

                  value2++;
               }
            }
         } catch (Exception e) {
            if (!(e instanceof InterruptedException)) {
               module.logConnectionError(e);
               return;
            }

            return;
         } finally {
            atomicBoolean.set(false);
         }
      }
   }

   private final boolean checkPlayerInfo(PlayerInfo playerInfo) {
      var arrayBlockingQueue = this.obtainCommandQueueModule().getArrayBlockingQueue();
      boolean flag;
      if (arrayBlockingQueue instanceof Collection && arrayBlockingQueue.isEmpty()) {
         flag = false;
      } else {
         Iterator iterator2 = arrayBlockingQueue.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = false;
               break;
            }

            Object obj = iterator2.next();
            PlayerInfo playerInfo2 = (PlayerInfo)obj;
            if (Intrinsics.areEqual(playerInfo2.getName(), playerInfo.getName()) || Intrinsics.areEqual(playerInfo2.getUuid(), playerInfo.getUuid()) || Intrinsics.areEqual(playerInfo2.getIp(), playerInfo.getIp())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   public final void handleText(@Nullable String text, @Nullable String text2, @Nullable String text3) {
      ThrottleModule module = (ThrottleModule)this.platform.getModule(ThrottleModule.class);
      ConfigModule module2 = (ConfigModule)this.platform.getModule(ConfigModule.class);
      if (module2.getSettings().isNotifyDupeipOnJoin() && !module.checkByteValue((byte)0, text2)) {
         ArrayBlockingQueue arrayBlockingQueue = this.arrayBlockingQueue2;
         Intrinsics.checkNotNull(text);
         Intrinsics.checkNotNull(text2);
         arrayBlockingQueue.offer(new PlayerInfo(text, TargetParser.normalize(text2), text3));
         if (module2.isVerbose()) {
            module2.logDebug("Offer " + text);
         }
      }
   }

}
