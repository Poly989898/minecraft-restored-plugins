package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class IpReportCommand extends AbstractCommand {
   public IpReportCommand(@NotNull Platform platform) {
      super("ipreport", platform);
   }

   @Override
   public boolean obtainFlag() {
      return false;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         Object[] onlineSenders = this.getPlatform().getOnlineSenders();
         var arrayList = new ArrayList();
         int value = 0;

         for (int i = onlineSenders.length; value < i; value++) {
            Object obj = onlineSenders[value];
            LiteSender liteSender2 = (LiteSender)obj;
            if (!Intrinsics.areEqual(liteSender2, liteSender)) {
               arrayList.add(obj);
            }
         }

         List list = arrayList;
         CommandTask commandTask = this;
         DatabaseModule module = (DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               CharSequence replacePlaceholders2 = session.replacePlaceholders(Message.IPREPORT_START.computeText("num", list.size()));
               AltAccountPunisher.sendMessage(this, replacePlaceholders2);
               Iterable iterable = list;

               for (Object obj2 : iterable) {
                  LiteSender liteSender3 = (LiteSender)obj2;
                  DupeIpScanTask punishmentApplyTask = new DupeIpScanTask(liteSender3.getName(), session, null, false, 0, 28, null).obtainPunishmentApplyTask();
                  if (!punishmentApplyTask.isFailed()) {
                     int value2 = punishmentApplyTask.getResultCount();
                     CommandTask commandTask2 = this;
                     if (value2 >= ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getIpreportMinimumAccounts()) {
                        CommandTask commandTask3 = this;
                        CharSequence replacePlaceholders3 = session.replacePlaceholders(Message.ENTRY_3, liteSender3);
                        CharSequence[] messages = new CharSequence[]{"result", punishmentApplyTask.getResultText()};
                        AltAccountPunisher.sendMessage(commandTask3, (CharSequence) (TextUtil.replacePairs(replacePlaceholders3, messages)));
                     }
                  }
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
            if (!module.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
