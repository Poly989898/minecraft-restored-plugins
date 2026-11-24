package io.lumine.mythic.lib.data.sql;

import io.lumine.mythic.lib.data.OfflineDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataHandler;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import org.jetbrains.annotations.NotNull;

public abstract class SQLSynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> implements SynchronizedDataHandler<H, O> {
   private final SQLDataSource dataSource;

   public SQLSynchronizedDataHandler(SQLDataSource dataSource) {
      this.dataSource = dataSource;
   }

   public SQLDataSource getDataSource() {
      return this.dataSource;
   }

   @Override
   public boolean loadData(@NotNull H playerData) {
      return this.newDataSynchronizer(playerData).synchronize();
   }

   @Override
   public abstract void saveData(@NotNull H value, boolean flag);

   @Override
   public void close() {
      this.getDataSource().close();
   }

   public abstract SQLDataSynchronizer newDataSynchronizer(@NotNull H value);
}
