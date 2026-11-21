package litebans;

final class ConnectionDeadTask implements Runnable {
   private final PoolEntry poolEntry;
   final HikariPool hikariPool;

   ConnectionDeadTask(HikariPool hikariPool, PoolEntry poolEntry) {
      this.hikariPool = hikariPool;
      this.poolEntry = poolEntry;
   }

   @Override
   public void run() {
      if (HikariPool.access$connectionBag(this.hikariPool).reserve(this.poolEntry)) {
         if (!this.hikariPool.isConnectionAlive(this.poolEntry.connection2)) {
            HikariPool.access$evictConnectionInternal(this.hikariPool, this.poolEntry, "(connection is dead)", true);
            this.hikariPool.addBagItem(HikariPool.access$connectionBag(this.hikariPool).getWaitingThreadCount());
         } else {
            HikariPool.access$connectionBag(this.hikariPool).unreserve(this.poolEntry);
            HikariPool.access$LOGGER(this.hikariPool).handleText("%s - keepalive: connection %s is alive", new Object[]{this.hikariPool.poolName, this.poolEntry.connection2.toString()});
         }
      }
   }
}
