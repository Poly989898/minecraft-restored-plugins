package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class QueueCommandTask extends CommandTask {
   private final String text51;
   private final String text52;
   private final String text53;

   public QueueCommandTask(@NotNull Platform platform, @NotNull String text, @NotNull String text2, @Nullable String text3) {
      super(platform);
      this.text51 = text;
      this.text52 = text2;
      this.text53 = text3;
   }

   @Override
   public void run() {
      try {
         CommandQueueModule module = (CommandQueueModule)this.getPlatform().getModule(CommandQueueModule.class);
         String text = this.text51;
         String text2 = this.text52;
         String text3 = this.text53;
         var arrayBlockingQueue = module.getArrayBlockingQueue();
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
               PlayerInfo playerInfo = (PlayerInfo)obj;
               if (Intrinsics.areEqual(playerInfo.getName(), text) && Intrinsics.areEqual(playerInfo.getUuid(), text2) && Intrinsics.areEqual(playerInfo.getIp(), text3)) {
                  flag = true;
                  break;
               }
            }
         }

         if (!flag) {
            module.getArrayBlockingQueue().put(new PlayerInfo(text, text2, text3));
         }
      } catch (InterruptedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
