package litebans;

final class MaxLifetimeTask implements Runnable {
   private final PoolEntry poolEntry;
   final HikariPool hikariPool;

   MaxLifetimeTask(HikariPool hikariPool, PoolEntry poolEntry) {
      this.hikariPool = hikariPool;
      this.poolEntry = poolEntry;
   }

   @Override
   public void run() {
      if (HikariPool.access$evictConnectionInternal(this.hikariPool, this.poolEntry, "(connection has passed maxLifetime)", false)) {
         this.hikariPool.addBagItem(HikariPool.access$connectionBag(this.hikariPool).getWaitingThreadCount());
      }
   }
}
