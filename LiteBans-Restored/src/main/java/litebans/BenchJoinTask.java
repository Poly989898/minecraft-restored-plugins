package litebans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import org.jetbrains.annotations.NotNull;

public final class BenchJoinTask extends CommandTask {
   private final String[] texts;

   public BenchJoinTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.texts = texts;
   }

   @Override
   public void run() {
      LiteSender sender = this.getSender();
      KotlinRandom kotlinRandom = new KotlinRandom(0L, 1, null);
      int value = StringMatching.getInt(this.texts, 1, 512);
      int value2 = StringMatching.getInt(this.texts, 2, 4096);
      int value3 = StringMatching.getInt(this.texts, 3, 6);
      ServerInfo kotlinServerInfo = new KotlinServerInfoImpl();
      List arrayList = new ArrayList(value);

      for (int i = 0; i < value; i++) {
         UUID uuid = new UUID(kotlinRandom.nextLong(), kotlinRandom.nextLong());
         int value4 = kotlinRandom.nextInt(252) + 2;
         int value5 = kotlinRandom.nextInt(252) + 2;
         int value6 = kotlinRandom.nextInt(252) + 2;
         int value7 = kotlinRandom.nextInt(252) + 2;
         String text = "" + value4 + '.' + value5 + '.' + value6 + '.' + value7;
         String text2 = String.valueOf(kotlinRandom.nextInt(999999999));
         arrayList.add(new KotlinLiteSenderImpl(text2, text, uuid));
      }

      CommandTask commandTask = this;
      ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).setDebugLevel((byte)0);
      Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(1, value3);
      Iterator iterator2 = kotlinIntProgressionSub.iterator();

      while (iterator2.hasNext()) {
         int value8 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
         this.getPlatform().execute(() -> {
            long value22 = System.nanoTime();
            Iterable kotlinIntProgressionSub2 = new KotlinIntProgressionSub(1, value2);
            Iterator iterator22 = kotlinIntProgressionSub2.iterator();

            while (iterator22.hasNext()) {
               int value32 = ((KotlinIteratorImpl4)iterator22).obtainIntValue4();
               LiteSender liteSender2 = (LiteSender)arrayList.get(kotlinRandom.nextInt(arrayList.size()));
               CommandTask commandTask2 = this;
               LoginHandler loginHandler = ((BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class)).getLoginHandler();
               loginHandler.handleObj(new ServerConnectEvent(new FakeProxiedPlayer(liteSender2, kotlinServerInfo), kotlinServerInfo, Reason.PLUGIN, null));
            }

            long value42 = System.nanoTime() - value22;
            sender.sendMessage(LiteColor.GREEN + " -> " + LiteColor.GOLD + NumberFormats.computeText2(value42 / 1000.0 / 1000.0) + ' ' + LiteColor.GREEN + "ms to process " + value2 + " events.");
         });
      }
   }

}
