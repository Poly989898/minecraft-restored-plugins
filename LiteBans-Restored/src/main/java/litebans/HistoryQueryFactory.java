package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HistoryQueryFactory {
   private HistoryQueryFactory() {
   }

   public final HistoryQuery createQuery(
      @NotNull DatabaseSession databaseSession,
   @Nullable String text,
   @NotNull String text2,
   @NotNull ExecutorSource executorSource,
   boolean flag,
   @Nullable String text3,
   boolean flag2,
   @NotNull Number number,
   @NotNull Number number2
   ) {
      DatabaseSession databaseSession2 = databaseSession;
      return new HistoryQuery(databaseSession, text, executorSource, flag, new ServerScopeClause(databaseSession2.getPlatform(), text2), text3, flag2, number, number2);
   }

   public static HistoryQuery createQueryWithDefaults(
      HistoryQueryFactory historyQueryFactory, DatabaseSession databaseSession, String text, String text2, ExecutorSource executorSource, boolean flag, String text3, boolean flag2, Number number, Number number2, int value, Object obj
   ) {
      if ((value & 4) != 0) {
         executorSource = ExecutorSource.UUID;
      }

      if ((value & 8) != 0) {
         flag = false;
      }

      if ((value & 16) != 0) {
         text3 = null;
      }

      if ((value & 32) != 0) {
         flag2 = true;
      }

      if ((value & 64) != 0) {
         number = 0;
      }

      if ((value & 128) != 0) {
         number2 = 0;
      }

      return historyQueryFactory.createQuery(databaseSession, text, text2, executorSource, flag, text3, flag2, number, number2);
   }

   public HistoryQueryFactory(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
