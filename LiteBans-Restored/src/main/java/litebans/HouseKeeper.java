package litebans;

import java.util.List;

final class HouseKeeper implements Runnable {
   private volatile long longValue;
   final HikariPool hikariPool;

   private HouseKeeper(HikariPool hikariPool) {
      this.hikariPool = hikariPool;
      this.longValue = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.access$HOUSEKEEPING_PERIOD_MS(this.hikariPool));
   }

   @Override
   public void run() {
      try {
         this.hikariPool.connectionTimeout = this.hikariPool.hikariConfig.getConnectionTimeout();
         this.hikariPool.validationTimeout = this.hikariPool.hikariConfig.getValidationTimeout();
         HikariPool.access$leakTaskFactory(this.hikariPool).setLongValue(this.hikariPool.hikariConfig.getLeakDetectionThreshold());
         this.hikariPool.catalog = this.hikariPool.hikariConfig.getCatalog() != null && !this.hikariPool.hikariConfig.getCatalog().equals(this.hikariPool.catalog) ? this.hikariPool.hikariConfig.getCatalog() : this.hikariPool.catalog;
         long idleTimeout = this.hikariPool.hikariConfig.getIdleTimeout();
         long currentTime2 = ClockSource.currentTime();
         if (ClockSource.plusMillis(currentTime2, 128L) < ClockSource.plusMillis(this.longValue, HikariPool.access$HOUSEKEEPING_PERIOD_MS(this.hikariPool))) {
            HikariPool.access$LOGGER(this.hikariPool)
               .handleText4(
                  "%s - Retrograde clock change detected (housekeeper delta=%s), soft-evicting connections from pool.",
                  new Object[]{this.hikariPool.poolName, ClockSource.elapsedDisplayString(this.longValue, currentTime2)}
            );
            this.longValue = currentTime2;
            this.hikariPool.softEvictConnections();
            return;
         }

         this.longValue = currentTime2;
         String text = "Pool ";
         if (idleTimeout > 0L && this.hikariPool.hikariConfig.getMinimumIdle() < this.hikariPool.hikariConfig.getMaximumPoolSize()) {
            this.hikariPool.logPoolState(new String[]{"Before cleanup "});
            text = "After cleanup  ";
            List list = HikariPool.access$connectionBag(this.hikariPool).values(0);
            int value = list.size() - this.hikariPool.hikariConfig.getMinimumIdle();

            for (PoolEntry poolEntry : (Iterable<PoolEntry>) list) {
               if (value > 0 && ClockSource.elapsedMillis(poolEntry.longValue, currentTime2) > idleTimeout && HikariPool.access$connectionBag(this.hikariPool).reserve(poolEntry)) {
                  this.hikariPool.closeConnection(poolEntry, "(connection has passed idleTimeout)");
                  value--;
               }
            }
         }

         this.hikariPool.logPoolState(new String[]{text});
         HikariPool.access$fillPool(this.hikariPool);
      } catch (Exception e) {
         HikariPool.access$LOGGER(this.hikariPool).handleText2("Unexpected exception in housekeeping task", new Object[]{e});
      }
   }

   HouseKeeper(HikariPool hikariPool, EnumSwitchMap3 enumSwitchMap) {
      this(hikariPool);
   }
}
