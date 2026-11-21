package litebans;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class BenchGenTask extends CommandTask {
   private final String[] texts;

   public BenchGenTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.texts = texts;
   }

   @Override
   public void run() {
      KotlinRandom kotlinRandom = new KotlinRandom(0L, 1, null);
      PunishmentType[] punishmentTypes = PunishmentType.values();
      int value = 0;

      for (int i = punishmentTypes.length; value < i; value++) {
         PunishmentType punishmentType = punishmentTypes[value];
         if (Intrinsics.areEqual(punishmentType.name(), this.texts[1])) {
            int int2 = Integer.parseInt(this.texts[2]);
            int int3 = Integer.parseInt(this.texts[3]);
            int int4 = Integer.parseInt(this.texts[4]);
            byte b = 15;
            byte b2 = 40;
            List arrayList = new ArrayList(int2);
            List arrayList2 = new ArrayList(int2);
            CommandTask commandTask = this;
            ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
               DatabaseSession databaseSession = (DatabaseSession) obj;
               if (databaseSession.isClosed()) {
                  AltAccountPunisher.sendMessage(this, Message.ERROR_NO_SQL_CONNECTION);
                  return Unit.UNIT;
               }

               for (int i2 = 0; i2 < int2; i2++) {
                  arrayList.add(new UUID(kotlinRandom.nextLong(), kotlinRandom.nextLong()).toString());
               }

               AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (TextUtil.colorize(LiteColor.GREEN, "Generating entries...")));
               long currentTime = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);

               for (int i22 = 0; i22 < int2; i22++) {
                  String string = (String)arrayList.get(i22);
                  boolean flag = kotlinRandom.nextInt(100) < b2;
                  boolean flag2 = kotlinRandom.nextInt(100) < int3;
                  boolean flag3 = flag2 && int4 > 0 && kotlinRandom.nextInt(100) < int4;
                  boolean flag4 = kotlinRandom.nextInt(100) < b;
                  boolean flag5 = !flag4;
                  long value6;
                  if (flag) {
                     value6 = 0L;
                  } else {
                     value6 = currentTime + 300000000 + kotlinRandom.nextInt(1000000000);
                     if (flag4) {
                        value6 = currentTime - kotlinRandom.nextInt(10000000);
                     }
                  }

                  int value7 = kotlinRandom.nextInt(252) + 2;
                  int value8 = kotlinRandom.nextInt(252) + 2;
                  int value9 = kotlinRandom.nextInt(252) + 2;
                  String text = String.valueOf(kotlinRandom.nextInt(252) + 2);
                  if (flag2 && flag3) {
                     text = "%";
                  }

                  String text2 = "" + value7 + '.' + value8 + '.' + value9 + '.' + text;
                  Punishment punishment = new Punishment(punishmentType, string, text2, "", "", "", "*", "", currentTime, value6, (byte)0, false, flag2, flag5, 0L, 17408, null);
                  arrayList2.add(punishment);
               }

               AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (TextUtil.colorize(LiteColor.GREEN, "Adding entries...")));
               CommandTask commandTask2 = this;
               byte debugLevel = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getDebugLevel();
               CommandTask commandTask22 = this;
               ((ConfigModule)commandTask22.getPlatform().getModule(ConfigModule.class)).setDebugLevel((byte)0);
               Iterable iterable = arrayList2;

               for (Object obj2 : iterable) {
                  Punishment punishment2 = (Punishment)obj2;
                  databaseSession.insertPunishment(punishment2);
               }

               CommandTask commandTask3 = this;
               ((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).setDebugLevel(debugLevel);
               long currentTime2 = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);
               AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (TextUtil.colorize(LiteColor.GREEN, "Done in ") + (currentTime2 - currentTime) + " ms."));
               return Unit.UNIT;
            });
            return;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

}
