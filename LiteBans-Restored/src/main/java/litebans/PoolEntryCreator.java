package litebans;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

final class PoolEntryCreator implements Callable {
   private final String text2;
   final HikariPool hikariPool;

   PoolEntryCreator(HikariPool hikariPool, String text) {
      this.hikariPool = hikariPool;
      this.text2 = text;
   }

   public Boolean obtainBoolean() {
      long value = 250L;

      while (this.hikariPool.poolState == 0 && this.obtainFlag()) {
         PoolEntry poolEntry = HikariPool.access$createPoolEntry(this.hikariPool);
         if (poolEntry != null) {
            HikariPool.access$connectionBag(this.hikariPool).add(poolEntry);
            HikariPool.access$LOGGER(this.hikariPool).handleText("%s - Added connection %s", new Object[]{this.hikariPool.poolName, poolEntry.connection2.toString()});
            if (this.text2 != null) {
               this.hikariPool.logPoolState(new String[]{this.text2});
            }

            return Boolean.TRUE;
         }

         if (this.text2 != null) {
            HikariPool.access$LOGGER(this.hikariPool).handleText("%s - Connection add failed, sleeping with backoff: %dms", new Object[]{this.hikariPool.poolName, value});
         }

         TransactionIsolation.handleLongValue(value);
         value = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(this.hikariPool.connectionTimeout, (long)(value * 1.5)));
      }

      return Boolean.FALSE;
   }

   private final synchronized boolean obtainFlag() {
      return this.hikariPool.getTotalConnections() < this.hikariPool.hikariConfig.getMaximumPoolSize() && (HikariPool.access$connectionBag(this.hikariPool).getWaitingThreadCount() > 0 || this.hikariPool.getIdleConnections() < this.hikariPool.hikariConfig.getMinimumIdle());
   }

   @Override
   public Object call() {
      return this.obtainBoolean();
   }
}
